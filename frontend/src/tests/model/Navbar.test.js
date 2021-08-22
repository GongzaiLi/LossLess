import {createLocalVue, mount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import NavBar from '../../components/model/Navbar'; // name of your Vue component
import Auth from '../../auth'
import Api from '../../Api'

let wrapper;

let $route;

let $router;

let userData = {
  "id": 100,
  "firstName": "John",
  "lastName": "Smith",
  "middleName": "Hector",
  "nickname": "Jonny",
  "bio": "Likes long walks on the beach",
  "email": "johnsmith99@gmail.com",
  "dateOfBirth": "1999-04-27",
  "phoneNumber": "+64 3 555 0129",
  currentlyActingAs: null,
  "homeAddress": {
    "streetNumber": "3/24",
    "streetName": "Ilam Road",
    "city": "Christchurch",
    "region": "Canterbury",
    "country": "New Zealand",
    "postcode": "90210"
  },
  "created": "2020-07-14T14:32:00Z",
  "role": "user",
  "businessesAdministered": [
    {
      "id": 100,
      "administrators": [
        "string"
      ],
      "primaryAdministratorId": 20,
      "name": "Lumbridge General Store",
      "description": "A one-stop shop for all your adventuring needs",
      "address": {
        "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
      },
      "businessType": "Accommodation and Food Services",
      "created": "2020-07-14T14:52:00Z"
    }
  ]
}

let $currentUser = userData;

const date = new Date();
const currentDateWithin24 = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'T'+(date.getHours()+2)+':'+date.getMinutes()+':'+date.getUTCSeconds()+'Z'


const expiringCards = [
    {
      id: 2,
      title: 'card 2 that expires within next 24 hours',
      displayPeriodEnd: currentDateWithin24
    },
    {
      id: 3,
      title: 'card 2 that expires within next 24 hours',
      displayPeriodEnd: currentDateWithin24
    }
    ]

jest.mock('../../Api');

beforeEach(() => {
  $route = {
    name: "users",
    params: {
      id: 0
    },
    query: {}
  };

  $router = {
    push: jest.fn(),
    replace: jest.fn(),
    go: jest.fn(),
  }

  const localVue = createLocalVue();

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(Auth);

  Api.getExpiredCards.mockResolvedValue({data: expiringCards});
  Api.clearHasCardsExpired.mockResolvedValue({response: {status: 200}});
  Api.getNotifications.mockResolvedValue({data: []});

  wrapper = mount(NavBar, {
    localVue,
    propsData: {},
    mocks: {$route, $router, $currentUser, displayPeriodEnd: { split: 'asd'  }},
    stubs: ['router-link'],
    methods: {},
    computed: {},
  })
});

afterEach(() => {
  wrapper.destroy();
});

describe('NavBar', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});

describe('User Drop Down', () => {
  test('shows businesses administered', async () => {
    expect(wrapper.findAll(".business-name-drop-down").length).toEqual(1);
    expect(wrapper.find(".business-name-drop-down").text()).toBe("Lumbridge General Store");
  })

  test('shows multiple businesses administered', async () => {
    const prevBusinesses = JSON.parse(JSON.stringify(userData.businessesAdministered));

    userData.businessesAdministered.push({
      "id": 101,
      "administrators": [],
      "primaryAdministratorId": 20,
      "name": "Another Store Name",
      "businessType": "Blah Blah Blah",
      "created": "2020-07-14T14:52:00Z"
    });
    await wrapper.vm.$forceUpdate();  // Force vm to refresh and update with the new user data
    expect(wrapper.findAll(".business-name-drop-down").length).toEqual(2);
    expect(wrapper.findAll(".business-name-drop-down").at(0).text()).toEqual("Lumbridge General Store");
    expect(wrapper.findAll(".business-name-drop-down").at(1).text()).toEqual("Another Store Name");

    userData.businessesAdministered = prevBusinesses; // Reset businesses
  })

  test('works if no businesses administered', async () => {
    const prevBusinesses = JSON.parse(JSON.stringify(userData.businessesAdministered));

    userData.businessesAdministered = [];
    await wrapper.vm.$forceUpdate();  // Force vm to refresh and update with the new user data

    expect(wrapper.find(".business-name-drop-down").exists()).toBeFalsy();
    expect(wrapper.find("hr").exists()).toBeFalsy();

    userData.businessesAdministered = prevBusinesses; // Reset businesses
  })
});


describe('User role', () => {
  test('works for admin', async () => {
    wrapper.vm.$currentUser.role = 'globalApplicationAdmin';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userBadgeRole).toEqual("Site Admin");
  })
  test('works for dgaa', async () => {
    wrapper.vm.$currentUser.role = 'defaultGlobalApplicationAdmin';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userBadgeRole).toEqual("Default Site Admin");
  })
  test('works for user', async () => {
    wrapper.vm.$currentUser.role = 'user';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userBadgeRole).toEqual("");
  })
});


describe('Got to profile', () => {
  test('works when acting as user', async () => {
    await wrapper.find("#go-to-profile a").trigger("click");
    await wrapper.vm.$nextTick();

    expect($router.push).toHaveBeenCalledWith({path: '/users/100'});
  })

  test('works when acting as business', async () => {
    wrapper.vm.$currentUser.currentlyActingAs = {id: 69};
    await wrapper.vm.$nextTick();
    await wrapper.find("#go-to-profile a").trigger("click");
    await wrapper.vm.$nextTick();

    expect($router.push).toHaveBeenCalledWith({path: '/businesses/69'});
  })
});

describe('Act as business', () => {
  test('works on click with single business', async () => {
    await wrapper.find(".business-name-drop-down a").trigger("click");
    await wrapper.vm.$nextTick();
    expect(wrapper.findAll(".business-name-drop-down").length).toBe(0);
    expect(wrapper.findAll("hr").length).toEqual(2);
    expect(wrapper.find("#profile-name").text()).toEqual("Lumbridge General Store");
    expect(wrapper.find(".user-name-drop-down").text()).toBe("John");

    expect(wrapper.html()).not.toContain("Create Business");

    userData.currentlyActingAs = null;
  })

  test('can go back to single user', async () => {
    userData.currentlyActingAs = userData.businessesAdministered[0];

    await wrapper.vm.$forceUpdate();  // Force vm to refresh and update with the new user data

    await wrapper.find(".user-name-drop-down a").trigger("click");
    await wrapper.vm.$nextTick();
    expect(wrapper.findAll(".business-name-drop-down").length).toEqual(1);
    expect(wrapper.find(".business-name-drop-down").text()).toBe("Lumbridge General Store");
    expect(wrapper.find("#profile-name").text()).toEqual("John");
    expect(wrapper.findAll("hr").length).toEqual(2);
    expect(wrapper.findAll(".user-name-drop-down").length).toBe(0);

    expect(wrapper.html()).toContain("Create Business");

    userData.currentlyActingAs = null;
  })

  test('works on click with multiple business', async () => {
    const prevUser = JSON.parse(JSON.stringify(userData));

    userData.businessesAdministered.push({
      "id": 101,
      "administrators": [],
      "primaryAdministratorId": 20,
      "name": "Another Store Name",
      "businessType": "Blah Blah Blah",
      "created": "2020-07-14T14:52:00Z"
    });
    await wrapper.vm.$forceUpdate();  // Force vm to refresh and update with the new user data

    await wrapper.findAll(".business-name-drop-down a").at(1).trigger("click");
    await wrapper.vm.$nextTick();

    expect(wrapper.find("#profile-name").text()).toEqual("Another Store Name");
    expect(wrapper.findAll(".business-name-drop-down").length).toBe(1);
    expect(wrapper.find(".business-name-drop-down").text()).toBe("Lumbridge General Store");
    expect(wrapper.find(".user-name-drop-down").text()).toBe("John");
    expect(wrapper.findAll("hr").length).toEqual(3);
    userData = prevUser;
  })
});

describe("Listing search ", () => {
  test('reloads if query is the name', async () => {
    $route.name = 'listings-search';
    $route.query.searchQuery = 'ABCDE';
    wrapper.vm.searchQuery = 'ABCDE';
    wrapper.vm.search();
    await wrapper.vm.$nextTick();
    expect($router.replace).toHaveBeenCalled();
  })

  test('goes to new route if query is not the name', async () => {
    $route.name = 'home';
    wrapper.vm.searchQuery = 'ABCDE';
    wrapper.vm.search();
    await wrapper.vm.$nextTick();
    expect($router.replace).toHaveBeenCalled();
  })

  test('clears search query after submitted', async () => {
    wrapper.vm.searchQuery = 'ABCDE';
    wrapper.vm.search();
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.searchQuery).toStrictEqual('');
  })
});