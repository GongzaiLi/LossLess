import {createLocalVue, mount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import NavBar from '../Navbar'; // name of your Vue component
import Auth from '../../auth'
import Api from '../../Api'

let wrapper;

const $route = {
  name: "users",
  params: {
    id: 0
  }
}

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
  "businessesPrimarilyAdministered": [
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

beforeEach(() => {
  const localVue = createLocalVue();

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(Auth);

  wrapper = mount(NavBar, {
    localVue,
    propsData: {},
    mocks: {$route, $currentUser},
    stubs: {},
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
    const prevBusinesses = JSON.parse(JSON.stringify(userData.businessesPrimarilyAdministered));

    userData.businessesPrimarilyAdministered.push({
      "id": 100,
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

    userData.businessesPrimarilyAdministered = prevBusinesses; // Reset businesses
  })

  test('works if no businesses administered', async () => {
    const prevBusinesses = JSON.parse(JSON.stringify(userData.businessesPrimarilyAdministered));

    userData.businessesPrimarilyAdministered = [];
    await wrapper.vm.$forceUpdate();  // Force vm to refresh and update with the new user data

    expect(wrapper.find(".business-name-drop-down").exists()).toBeFalsy();
    expect(wrapper.find("hr").exists()).toBeFalsy();

    userData.businessesPrimarilyAdministered = prevBusinesses; // Reset businesses
  })
});


describe('Act as business', () => {
  test('works on click with single business', async () => {
    const actAsBusiness = jest.spyOn(Api, 'setBusinessActingAs');
    await wrapper.find(".business-name-drop-down a").trigger("click");
    await wrapper.vm.$nextTick();
    expect(actAsBusiness).toHaveBeenCalledWith(100);
    expect(wrapper.findAll(".business-name-drop-down").length).toBe(0);
    expect(wrapper.findAll("hr").length).toEqual(1);
    expect(wrapper.find("#profile-name").text()).toEqual("Lumbridge General Store");
    expect(wrapper.find(".user-name-drop-down").text()).toBe("John");

    userData.currentlyActingAs = null;
  })

  test('can go back to single user', async () => {
    userData.currentlyActingAs = userData.businessesPrimarilyAdministered[0];

    await wrapper.vm.$forceUpdate();  // Force vm to refresh and update with the new user data

    const actAsBusiness = jest.spyOn(Api, 'setBusinessActingAs');
    await wrapper.find(".user-name-drop-down a").trigger("click");
    await wrapper.vm.$nextTick();
    expect(actAsBusiness).toHaveBeenCalledWith(null);
    expect(wrapper.findAll(".business-name-drop-down").length).toEqual(1);
    expect(wrapper.find(".business-name-drop-down").text()).toBe("Lumbridge General Store");
    expect(wrapper.find("#profile-name").text()).toEqual("John");
    expect(wrapper.findAll("hr").length).toEqual(1);
    expect(wrapper.findAll(".user-name-drop-down").length).toBe(0);

    userData.currentlyActingAs = null;
  })

  test('works on click with multiple business', async () => {
    const prevUser = JSON.parse(JSON.stringify(userData));
    const actAsBusiness = jest.spyOn(Api, 'setBusinessActingAs');

    userData.businessesPrimarilyAdministered.push({
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

    expect(actAsBusiness).toHaveBeenCalledWith(101);

    expect(wrapper.find("#profile-name").text()).toEqual("Another Store Name");
    expect(wrapper.findAll(".business-name-drop-down").length).toBe(1);
    expect(wrapper.find(".business-name-drop-down").text()).toBe("Lumbridge General Store");
    expect(wrapper.find(".user-name-drop-down").text()).toBe("John");
    expect(wrapper.findAll("hr").length).toEqual(2);
    userData = prevUser;
  })
});