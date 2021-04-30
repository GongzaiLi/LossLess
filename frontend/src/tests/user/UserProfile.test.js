import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import userProfile from '../../components/user/UserProfile';
import Api from "../../Api";
import VueRouter from 'vue-router';

// const $route = {
//   params: {
//     id: 0
//   }
// };
config.showDeprecationWarnings = false  //to disable deprecation warnings
const $log = {
  debug: jest.fn(),
};

jest.mock('../../Api');

let wrapper;
let mockDateNow = '2019-05-14T11:01:58.135Z';
jest
  .spyOn(global.Date, 'now')
  .mockImplementationOnce(() =>
    new Date(mockDateNow).valueOf()
  );


beforeEach(() => {
  const localVue = createLocalVue();
  const router = new VueRouter();
  localVue.use(VueRouter);
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  Api.getUser.mockRejectedValue(new Error(''));

  wrapper = shallowMount(userProfile, {
    localVue,
    router,
    propsData: {},
    mocks: {$log},
    stubs: {},
    methods: {},
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Profile', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});


test('only-first-and-last-name', async () => {
  wrapper.vm.userData.firstName = "First";
  wrapper.vm.userData.lastName = "Last";
  await wrapper.vm.$nextTick();
  expect(wrapper.vm.fullName).toEqual("First Last");
});

test('first-and-middle-and-last-name', async () => {
  wrapper.vm.userData.firstName = "First";
  wrapper.vm.userData.lastName = "Last";
  wrapper.vm.userData.middleName = "Middle";
  await wrapper.vm.$nextTick();
  expect(wrapper.vm.fullName).toEqual("First Middle Last");
});

test('first-and-middle-and-last-and-nick-name', async () => {
  wrapper.vm.userData.firstName = "First";
  wrapper.vm.userData.lastName = "Last";
  wrapper.vm.userData.middleName = "Middle";
  wrapper.vm.userData.nickName = "Nick";
  await wrapper.vm.$nextTick();
  expect(wrapper.vm.fullName).toEqual("First Middle Last (Nick)");
});

test('first-and-last-and-nick-name', async () => {
  wrapper.vm.userData.firstName = "First";
  wrapper.vm.userData.lastName = "Last";
  wrapper.vm.userData.nickName = "Nick";
  await wrapper.vm.$nextTick();
  expect(wrapper.vm.fullName).toEqual("First Last (Nick)");
});


// getUserInfo
describe('check-getUserInfo-API-function-in-user-profile', () => {
  test('get-user-data', async () => {
    const response = {
      data: {
        id: 100,
        firstName: "John",
        lastName: "Smith",
        middleName: "Hector",
        nickname: "Jonny",
        bio: "Likes long walks on the beach",
        email: "johnsmith99@gmail.com",
        dateOfBirth: "1999-04-27",
        phoneNumber: "+64 3 555 0129",
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: "2020-07-14T14:32:00Z",
        role: "user",
        businessesAdministered: [
          {
            id: 100,
            administrators: [
              "string"
            ],
            primaryAdministratorId: 20,
            name: "Lumbridge General Store",
            description: "A one-stop shop for all your adventuring needs",
            address: {
              streetNumber: "3/24",
              streetName: "Ilam Road",
              city: "Christchurch",
              region: "Canterbury",
              country: "New Zealand",
              postcode: "90210"
            },
            businessType: "Accommodation and Food Services",
            created: "2020-07-14T14:52:00Z"
          }
        ]
      }
    };
    Api.getUser.mockResolvedValue(response);
    await wrapper.vm.getUserInfo(0);

    //await wrapper.vm.$forceUpdate();
    expect(wrapper.vm.userData).toEqual(response.data);
  });
});

//giveAdmin
describe('Testing-api-put-giveAdmin', () => {
  beforeEach(() => {
    wrapper.vm.userData.role = "user";
  });

  it('giveAdmin-Action-completed-successfully-status-200', async () => {
    Api.makeUserAdmin.mockResolvedValue({
      status: 200
    });

    wrapper.vm.userData.id = 0;
    await wrapper.vm.giveAdmin();
    expect(wrapper.vm.userData.role).toStrictEqual('globalApplicationAdmin');
  });

  it('giveAdmin-give-failure-status-500', async () => {
    Api.makeUserAdmin.mockRejectedValue({
      status: 500
    });

    wrapper.vm.userData.id = 0;
    window.alert = () => {};
    await wrapper.vm.giveAdmin();
    expect(wrapper.vm.userData.role).toStrictEqual("user");
  });
});

//revokeAdmin

describe('Testing-api-put-revokeAdmin', () => {
  beforeEach(() => {
    wrapper.vm.userData.role = "globalApplicationAdmin";
  });

  it('revokeAdmin-Action-completed-successfully-status-200', async () => {
    Api.revokeUserAdmin.mockResolvedValue({
      status: 200
    });

    wrapper.vm.userData.id = 0;
    await wrapper.vm.revokeAdmin();
    expect(wrapper.vm.userData.role).toStrictEqual('user');
  });

  it('revokeAdmin-give-failure-status-500', async () => {
    Api.revokeUserAdmin.mockRejectedValue({
      status: 500
    });

    wrapper.vm.userData.id = 0;
    window.alert = () => {};
    await wrapper.vm.revokeAdmin();
    expect(wrapper.vm.userData.role).toStrictEqual("globalApplicationAdmin");
  });
});
