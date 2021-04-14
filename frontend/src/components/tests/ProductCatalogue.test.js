import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import productCatalogue from '../ProductCatalogue';
import Api from "../../Api";

config.showDeprecationWarnings = false  //to disable deprecation warnings

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

  Api.getProducts.mockRejectedValue(new Error(''));

  wrapper = shallowMount(productCatalogue, {
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

describe('check-getProducts-API-function', () => {
  test('get-normal-data', async () => {
    const response = {
      data: [{
        id: "WATT-420-BEANS",
        name: "Watties Baked Beans - 420g can",
        description: "Baked Beans as they should be.",
        recommendedRetailPrice: 2.2,
        created: "2021-04-14T13:01:58.660Z"
      }]
    };
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(0);
    expect(wrapper.vm.items).toEqual(response.data);
  });

});