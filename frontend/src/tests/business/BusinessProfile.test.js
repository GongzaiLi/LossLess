import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import businessProfile from '../../components/business/BusinessProfile';
import Api from "../../Api";
import VueRouter from 'vue-router';
import makeAdminModal from "../../components/business/MakeAdminModal";


let userData = {
    id: 1,
    currentlyActingAs: null,
  businessesAdministered: [],
  }
;

const mockUserAuthPlugin = function install(Vue) {
  Vue.mixin({
    computed: {
      $currentUser: {
        get: function () {
          return userData;
        },
        set: function () {
          return null;
        },
      },
    }
  });
}
jest.mock('../../Api');
let wrapper;


const $log = {
  debug() {
  }
};

config.showDeprecationWarnings = false;  //to disable deprecation warnings


beforeEach(() => {
  const localVue = createLocalVue();
  const router = new VueRouter();

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(VueRouter);
  localVue.use(mockUserAuthPlugin);

  Api.getBusiness.mockRejectedValue(new Error(''));


  wrapper = shallowMount(businessProfile, {
    localVue,
    router,
    propsData: {},
    mocks: {$log},
    stubs: {'another-component': true},
    methods: {},
  });

  wrapper.route = {
    params: {
      id: 0,
    }
  };

});

afterEach(() => {
  wrapper.destroy();
});


test('get-normal-data', async () => {
  const response = {
    data: {
      email: 'blah@blah',
      phoneNumber: '+64 3 555 0129',
      id: 1,
      primaryAdministratorId: 20,
      name: "Lumbridge General Store",
      description: "A one-stop shop for all your adventuring needs",
      address: {
        streetNumber: "3/24",
        streetName: "Ilam Road",
        suburb: "a suburb",
        city: "Christchurch",
        region: "Canterbury",
        country: "New Zealand",
        postcode: "90210"
      },
      businessType: "Accommodation and Food Services",
      created: "2019-07-14T14:52:00Z",
    }
  };
  Api.getBusiness.mockResolvedValue(response);
  await wrapper.vm.getBusinessInfo(0);
  expect(wrapper.vm.businessData).toEqual(response.data);
});

test('check-can-revoke-admin-invalid-returns-false', async () => {

  const data = {
    email: 'blah@blah',
    phoneNumber: '+64 3 555 0129',
    id: 1,
    primaryAdministratorId: 2,
    name: "Lumbridge General Store",
    description: "A one-stop shop for all your adventuring needs",
    administrators: [
      {
        id: 0,
        firstName: 'James',
        lastName: 'Harris',
        middleName: 'Edward',
        nickname: 'Jimmy',
        bio: 'My name is James',
        email: 'jeh128@uclive.ac.nz',
        dateOfBirth: '2000-10-10',
        phoneNumber: '0271234567',
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "a suburb",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: '',
        role: '',
        businessesAdministered: [
          "0"
        ]
      },
      {
        id: 1,
        firstName: 'Michael',
        lastName: 'Hartley',
        middleName: 'Steven',
        nickname: 'Jimmy',
        bio: 'My name is Michael',
        email: 'micky@gmail.com',
        dateOfBirth: '2000-11-10',
        phoneNumber: '027121212',
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "a suburb",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: '',
        role: '',
        businessesAdministered: [
          "0"
        ]
      }
    ],
    address: {
      streetNumber: "3/24",
      streetName: "Ilam Road",
      suburb: "a suburb",
      city: "Christchurch",
      region: "Canterbury",
      country: "New Zealand",
      postcode: "90210"
    },
    businessType: "Accommodation and Food Services",
    created: "2019-07-14T14:52:00Z"
  }

  const user = {
    id: 1,
    role: "user",
  }

  wrapper.vm.businessData = data;

  wrapper.vm.$currentUser = user;

  await wrapper.vm.$nextTick();

  const canRevokeAdmin = wrapper.vm.checkCanRevokeAdmin(1);

  expect(canRevokeAdmin).toEqual(false);
})

test('check-can-revoke-admin-invalid-as-user-is-user-returns-false', async () => {

  const data = {
    email: 'blah@blah',
    phoneNumber: '+64 3 555 0129',
    id: 1,
    primaryAdministratorId: 0,
    name: "Lumbridge General Store",
    description: "A one-stop shop for all your adventuring needs",
    administrators: [
      {
        id: 0,
        firstName: 'James',
        lastName: 'Harris',
        middleName: 'Edward',
        nickname: 'Jimmy',
        bio: 'My name is James',
        email: 'jeh128@uclive.ac.nz',
        dateOfBirth: '2000-10-10',
        phoneNumber: '0271234567',
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "a suburb",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: '',
        role: '',
        businessesAdministered: [
          "0"
        ]
      },
      {
        id: 1,
        firstName: 'Michael',
        lastName: 'Hartley',
        middleName: 'Steven',
        nickname: 'Jimmy',
        bio: 'My name is Michael',
        email: 'micky@gmail.com',
        dateOfBirth: '2000-11-10',
        phoneNumber: '027121212',
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "a suburb",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: '',
        role: '',
        businessesAdministered: [
          "0"
        ]
      }
    ],
    address: {
      streetNumber: "3/24",
      streetName: "Ilam Road",
      suburb: "a suburb",
      city: "Christchurch",
      region: "Canterbury",
      country: "New Zealand",
      postcode: "90210"
    },
    businessType: "Accommodation and Food Services",
    created: "2019-07-14T14:52:00Z"
  }

  const user = {
    id: 1,
    role: "user",
  }

  wrapper.vm.businessData = data;

  wrapper.vm.$currentUser = user;

  await wrapper.vm.$nextTick();

  const canRevokeAdmin = wrapper.vm.checkCanRevokeAdmin(1);

  expect(canRevokeAdmin).toEqual(false);
})

test('check-can-revoke-admin-valid-returns-true', async () => {

  const data = {
    email: 'blah@blah',
    phoneNumber: '+64 3 555 0129',
    id: 1,
    primaryAdministratorId: 1,
    name: "Lumbridge General Store",
    description: "A one-stop shop for all your adventuring needs",
    administrators: [
      {
        id: 0,
        firstName: 'James',
        lastName: 'Harris',
        middleName: 'Edward',
        nickname: 'Jimmy',
        bio: 'My name is James',
        email: 'jeh128@uclive.ac.nz',
        dateOfBirth: '2000-10-10',
        phoneNumber: '0271234567',
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "a suburb",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: '',
        role: '',
        businessesAdministered: [
          "0"
        ]
      },
      {
        id: 1,
        firstName: 'Michael',
        lastName: 'Hartley',
        middleName: 'Steven',
        nickname: 'Jimmy',
        bio: 'My name is Michael',
        email: 'micky@gmail.com',
        dateOfBirth: '2000-11-10',
        phoneNumber: '027121212',
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "a suburb",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: '',
        role: '',
        businessesAdministered: [
          "0"
        ]
      }
    ],
    address: {
      streetNumber: "3/24",
      streetName: "Ilam Road",
      suburb: "a suburb",
      city: "Christchurch",
      region: "Canterbury",
      country: "New Zealand",
      postcode: "90210"
    },
    businessType: "Accommodation and Food Services",
    created: "2019-07-14T14:52:00Z"
  }

  const user = {
    id: 0,
    role: "user",
  }

  wrapper.vm.businessData = data;

  wrapper.vm.$currentUser = user;

  await wrapper.vm.$nextTick();

  const canRevokeAdmin = wrapper.vm.checkCanRevokeAdmin(1);

  expect(canRevokeAdmin).toEqual(false);
})

test('remove-Admin-Click-Handler', async () => {
  const response = null;
  Api.revokeBusinessAdmin.mockResolvedValue(response);
  await wrapper.vm.removeAdminClickHandler(1);
  expect(wrapper.vm.error).toStrictEqual([]);
});

test('remove-Admin-Click-Handler-null-user', async () => {
  const response = null;
  Api.revokeBusinessAdmin.mockResolvedValue(response);
  const result = await wrapper.vm.removeAdminClickHandler(null);
  expect(result).toStrictEqual(undefined);
});

test('business-not-found', async () => {
  const response = null;
  Api.getBusiness.mockResolvedValue(response);
  await wrapper.vm.getBusinessInfo(999);
  expect(wrapper.vm.businessFound).toBeFalsy();
});

test('business-found', async () => {
  const response = {
    data: {
      email: 'blah@blah',
      phoneNumber: '+64 3 555 0129',
      id: 1,
      primaryAdministratorId: 20,
      name: "Lumbridge General Store",
      description: "A one-stop shop for all your adventuring needs",
      address: {
        streetNumber: "3/24",
        streetName: "Ilam Road",
        suburb: "a suburb",
        city: "Christchurch",
        region: "Canterbury",
        country: "New Zealand",
        postcode: "90210"
      },
      businessType: "Accommodation and Food Services",
      created: "2019-07-14T14:52:00Z"
    }
  };
  Api.getBusiness.mockResolvedValue(response);
  await wrapper.vm.getBusinessInfo(0);
  expect(wrapper.vm.businessFound).toBeTruthy();
});


test('business-not-found-in-html', async () => {
  const response = null;
  Api.getBusiness.mockResolvedValue(response);
  await wrapper.vm.getBusinessInfo(999);
  const msg = 'The business you are looking for does not exist.';
  expect(wrapper.html()).toContain(msg);
});

test('business-found-in-html', async () => {
  const response = {
    data: {
      email: 'blah@blah',
      phoneNumber: '+64 3 555 0129',
      id: 1,
      primaryAdministratorId: 20,
      name: "Lumbridge General Store",
      description: "A one-stop shop for all your adventuring needs",
      administrators: [
        {
          id: 0,
          firstName: 'James',
          lastName: 'Harris',
          middleName: 'Edward',
          nickname: 'Jimmy',
          bio: 'My name is James',
          email: 'jeh128@uclive.ac.nz',
          dateOfBirth: '2000-10-10',
          phoneNumber: '0271234567',
          homeAddress: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: "a suburb",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
          },
          created: '',
          role: '',
          businessesAdministered: [
            "0"
          ]
        },
        {
          id: 1,
          firstName: 'Michael',
          lastName: 'Hartley',
          middleName: 'Steven',
          nickname: 'Jimmy',
          bio: 'My name is Michael',
          email: 'micky@gmail.com',
          dateOfBirth: '2000-11-10',
          phoneNumber: '027121212',
          homeAddress: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: "a suburb",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
          },
          created: '',
          role: '',
          businessesAdministered: [
            "0"
          ]
        }
      ],
      address: {
        streetNumber: "3/24",
        streetName: "Ilam Road",
        suburb: "a suburb",
        city: "Christchurch",
        region: "Canterbury",
        country: "New Zealand",
        postcode: "90210"
      },
      businessType: "Accommodation and Food Services",
      created: "2019-07-14T14:52:00Z"
    }
  };

  Api.getBusiness.mockResolvedValue(response);
  await wrapper.vm.getBusinessInfo(0);
  await wrapper.vm.$nextTick();

  expect(wrapper.html()).toContain(response.data.email);
  expect(wrapper.html()).toContain(response.data.phoneNumber);
  expect(wrapper.html()).toContain(response.data.businessType);
  expect(wrapper.html()).toContain(response.data.description);

});

test('business-administrators-table-correct-data', async () => {
  const response = {
    data: {
      email: 'blah@blah',
      phoneNumber: '+64 3 555 0129',
      id: 1,
      primaryAdministratorId: 20,
      name: "Lumbridge General Store",
      description: "A one-stop shop for all your adventuring needs",
      administrators: [
        {
          id: 0,
          firstName: 'James',
          lastName: 'Harris',
          middleName: 'Edward',
          nickname: 'Jimmy',
          bio: 'My name is James',
          email: 'jeh128@uclive.ac.nz',
          dateOfBirth: '2000-10-10',
          phoneNumber: '0271234567',
          homeAddress: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: "a suburb",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
          },
          created: '',
          role: '',
          businessesAdministered: [
            "0"
          ]
        },
        {
          id: 1,
          firstName: 'Michael',
          lastName: 'Hartley',
          middleName: 'Steven',
          nickname: 'Jimmy',
          bio: 'My name is Michael',
          email: 'micky@gmail.com',
          dateOfBirth: '2000-11-10',
          phoneNumber: '027121212',
          homeAddress: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: "a suburb",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
          },
          created: '',
          role: '',
          businessesAdministered: [
            "0"
          ]
        }
      ],
      address: {
        streetNumber: "3/24",
        streetName: "Ilam Road",
        suburb: "a suburb",
        city: "Christchurch",
        region: "Canterbury",
        country: "New Zealand",
        postcode: "90210"
      },
      businessType: "Accommodation and Food Services",
      created: "2019-07-14T14:52:00Z"
    }
  };

  Api.getBusiness.mockResolvedValue(response);
  await wrapper.vm.getBusinessInfo(0);
  await wrapper.vm.$nextTick();

  expect(wrapper.vm.$refs.businessAdministratorsTable.$props.items[0].firstName).toBe("James");
  expect(wrapper.vm.$refs.businessAdministratorsTable.$props.items[1].firstName).toBe("Michael");
});

test('make-Admin-Handler', async () => {
  const response = null;
  Api.makeBusinessAdmin.mockResolvedValue(response);
  await wrapper.vm.makeAdminHandler(1);
  expect(wrapper.vm.error).toStrictEqual([]);
});

test('make-Admin-Handler-null', async () => {
  const response = null;
  Api.makeBusinessAdmin.mockResolvedValue(response);
  const result = await wrapper.vm.makeAdminHandler(null);
  expect(result).toStrictEqual(undefined);
});

describe('check-modal-Make-Admin-Modal', () => {
  test('check-product-detail-card-component-exists-when-click-create-button', async () => {
    await wrapper.vm.showMakeAdminModal();

    await wrapper.vm.$forceUpdate();

    expect(wrapper.find(makeAdminModal).exists()).toBeTruthy()
  })
});

it('400 error test', async () => {
  Api.makeBusinessAdmin.mockRejectedValue({response: {status: 400, data: {message: "User with ID does not exist"}}});

  await wrapper.vm.makeAdminHandler(1);

  expect(wrapper.vm.makeAdminError).toBe("User with ID does not exist");
});

it('403 error test', async () => {
  Api.makeBusinessAdmin.mockRejectedValue({response: {status: 403}});

  await wrapper.vm.makeAdminHandler(1);

  expect(wrapper.vm.makeAdminError).toBe("Forbidden. You are not an authorized administrator");
});

it('no internet test', async () => {
  Api.makeBusinessAdmin.mockRejectedValue({request: {path: 'blah'}});

  await wrapper.vm.makeAdminHandler(1);

  expect(wrapper.vm.makeAdminError).toBe("No Internet Connectivity");
});

it('other error test', async () => {
  Api.makeBusinessAdmin.mockRejectedValue({response: {status: 500}});

  await wrapper.vm.makeAdminHandler(1);

  expect(wrapper.vm.makeAdminError).toBe("Server error");
});


