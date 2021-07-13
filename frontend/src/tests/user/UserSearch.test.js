import {mount, createLocalVue} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import UserSearch from '../../components/user/UserSearch';
import Api from "../../Api";

let wrapper;


const $route = {
  params: {
    id: 0
  }
};

const $router = {
  push: jest.fn()
};

const $log = {
  debug(blah) {
    return blah;
    //console.log(blah);
  }
};

let $currentUser = {
  role: 'user'
}

jest.mock('../../Api');

beforeEach(() => {
  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  Api.getBusiness.mockRejectedValue(new Error(''));

  wrapper = mount(UserSearch, {
    localVue,
    propsData: {},
    mocks: {$route, $log, $currentUser, $router},
    methods: {},
  });

});

afterEach(() => {
  wrapper.destroy();
});

const normalResponse = { data: [
  {
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
]};

describe('User Search', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('Does not display roles if normal user', async () => {
    Api.searchUser.mockResolvedValue(normalResponse);

    await wrapper.vm.displayResults('a');
    await wrapper.vm.$nextTick();

    const headers = wrapper.findAll('th');
    for (let i=0; i<headers.length; i++) {
      expect(headers.at(i).text()).not.toContain('User Type');
    }
  });

  test('Can click entry to get to user profile', async () => {
    Api.searchUser.mockResolvedValue(normalResponse);

    await wrapper.vm.displayResults('a');
    await wrapper.vm.$nextTick();

    await wrapper.find('td').trigger('click');

    expect($router.push).toHaveBeenCalledWith({path: `/users/100`});
  });

  test('Displays roles if user is admin', async () => {
    $currentUser.role = 'globalApplicationAdmin';
    Api.searchUser.mockResolvedValue(normalResponse);

    await wrapper.vm.displayResults('a');
    await wrapper.vm.$nextTick();

    const headers = wrapper.findAll('th');
    expect(headers.at(0).text()).toContain('Name');
    expect(headers.at(1).text()).toContain('Nick Name');
    expect(headers.at(2).text()).toContain('Email');
    expect(headers.at(3).text()).toContain('Location');
    expect(headers.at(4).text()).toContain('User Type');
    expect(wrapper.html()).toContain('USER');
  });

  test('Displays roles if user is default admin', async () => {
    $currentUser.role = 'defaultGlobalApplicationAdmin';
    Api.searchUser.mockResolvedValue(normalResponse);

    await wrapper.vm.displayResults('a');
    await wrapper.vm.$nextTick();

    const headers = wrapper.findAll('th');
    expect(headers.at(0).text()).toContain('Name');
    expect(headers.at(1).text()).toContain('Nick Name');
    expect(headers.at(2).text()).toContain('Email');
    expect(headers.at(3).text()).toContain('Location');
    expect(headers.at(4).text()).toContain('User Type');
    expect(wrapper.html()).toContain('USER');
  });

  test('get user role string works for user', async () => {
    const role = await wrapper.vm.getUserRoleString({role: "user"});
    expect(role).toBe("USER");
  });

  test('get user role string works for admin', async () => {
    const role = await wrapper.vm.getUserRoleString({role: "globalApplicationAdmin"});
    expect(role).toBe("ADMIN");
  });

  test('get user role string works for default admin', async () => {
    const role = await wrapper.vm.getUserRoleString({role: "defaultGlobalApplicationAdmin"});
    expect(role).toBe("DEFAULT ADMIN");
  });
});
