import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import productCatalogue from '../product/ProductCatalogue';
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


describe('check-product-catalogue-page', () => {

  test('product-found', async () => {

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
    expect(wrapper.vm.productFound).toBeTruthy();
  });

  test('product-not-found', async () => {

    const response = null;
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(0);
    expect(wrapper.vm.productFound).toBeFalsy();
  });

  test('product-found-in-html', async () => {

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

    await wrapper.vm.$forceUpdate();

    const msg = 'Displaying 1 - 1 of total 1 results.';
    expect(wrapper.html()).toContain(msg);
  });

  test('product-not-found-in-html', async () => {

    const response = null;
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(0);

    await wrapper.vm.$forceUpdate();

    const msg = 'No Product to display';
    expect(wrapper.html()).toContain(msg);
  });
});


describe('check-pagination-function', () => {

  describe('check-itemsRangeMin-function', () => {

    test('1_total_result-in-1_perPage-in-1_currentPage', () => {

      wrapper.vm.items = Array(1).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMin).toBe(1);
    });

    test('1_total_result-in-100_perPage-in-1_currentPage', () => {

      wrapper.vm.items = Array(1).fill({id: 1});
      wrapper.vm.perPage = 100;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMin).toBe(1);
    });

    test('100_total_result-in-1_perPage-in-1_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMin).toBe(1);

    });

    test('1000_total_result-in-10_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMin).toBe(1);
    });

    test('100_total_result-in-10_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 10;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMin).toBe(91);
    });

    test('1000_total_result-in-10_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 10;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMin).toBe(91);
    });

    test('100_total_result-in-5_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 5;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMin).toBe(46);
    });

    test('1000_total_result-in-5_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 5;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMin).toBe(46);
    });

    test('100_total_result-in-1_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMin).toBe(10);
    });

    test('1000_total_result-in-1_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMin).toBe(10);
    });

  });

  describe('check-itemsRangeMax-function', () => {

    test('1_total_result-in-1_perPage-in-1_currentPage', () => {

      wrapper.vm.items = Array(1).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMax).toBe(1);
    });

    test('1_total_result-in-100_perPage-in-1_currentPage', () => {

      wrapper.vm.items = Array(1).fill({id: 1});
      wrapper.vm.perPage = 100;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMax).toBe(1);
    });

    test('100_total_result-in-1_perPage-in-1_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMax).toBe(1);

    });

    test('1000_total_result-in-10_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 1;
      expect(wrapper.vm.itemsRangeMax).toBe(1);
    });

    test('100_total_result-in-10_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 10;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(100);
    });

    test('1000_total_result-in-10_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 10;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(100);
    });

    test('100_total_result-in-5_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 5;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(50);
    });

    test('1000_total_result-in-5_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 5;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(50);
    });

    test('100_total_result-in-1_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(100).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(10);
    });

    test('1000_total_result-in-1_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(10);
    });

    test('1000_total_result-in-1_perPage-in-10_currentPage', () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 1;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(10);
    });

    test('1000_total_result-in-100_perPage-in-10_currentPage', async () => {

      wrapper.vm.items = Array(1000).fill({id: 1});
      wrapper.vm.perPage = 100;
      wrapper.vm.currentPage = 10;
      expect(wrapper.vm.itemsRangeMax).toBe(1000);
    });
  });

});

describe('check-setDescription-function', () => {
  test('description-less-then-10-words', () => {
    const description = "Baked Beans as they should be.";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Beans as they should be...');
  });

  test('description-1-words', () => {
    const description = "Baked";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked...');
  });

  test('description-more-then-10-words', () => {
    const description = "Baked Beans as they should be. Baked Beans as they should be. Baked Beans as they should be.";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Beans as they should be. Baked Beans as they...');
  });
});

describe('check-setCreated-function', () => {
  test('set-mouth-less-then-10-data', () => {
    const created =  "2021-04-14T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('14/04/2021');
  });

  test('set-month-more-then-9-data', () => {
    const created =  "2021-10-14T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('14/10/2021');
  });

  test('set-day-more-then-9-data', () => {
    const created =  "2021-10-10T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('10/10/2021');
  });

  test('set-day-less-then-10-data', () => {
    const created =  "2021-10-02T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('02/10/2021');
  });
});