import {shallowMount, createLocalVue} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import businessProfile from '../BusinessProfile';
import Api from "../../Api";

let wrapper;


const $route = {
  params: {
    id: 0
  }
};

const $log = {
  debug() {
  }
};




jest.mock('../../Api');

beforeEach(() => {
  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  Api.getBusiness.mockRejectedValue(new Error(''));



  wrapper = shallowMount(businessProfile, {
    localVue,
    propsData: {},
    mocks: {$route, $log},
    stubs: {'another-component': true},
    methods: {},
  });

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
  expect(wrapper.vm.businessData).toEqual(response.data);
});


test('get-address', async () => {
  const address = {
    streetNumber: "3/24",
    streetName: "Ilam Road",
    city: "Christchurch",
    region: "Canterbury",
    country: "New Zealand",
    postcode: "90210"
  };

  wrapper.vm.businessData.address = address;
  await wrapper.vm.$nextTick();
  expect(wrapper.vm.getAddress).toEqual("3/24 Ilam Road Christchurch Canterbury New Zealand 90210");
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
      address: {
        streetNumber: "3/24",
        streetName: "Ilam Road",
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