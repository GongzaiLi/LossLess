import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import productCatalogue from '../../components/product/ProductCatalogue';
import Api from "../../Api";
import productDetailCard from "../../components/product/ProductDetailCard";
import Pagination from "../../components/model/Pagination";

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
  Api.getBusiness.mockResolvedValue({data: {"address": {"country": "New Zealand"}}});
  Api.getUserCurrency.mockResolvedValue({
    symbol: '$',
    code: 'USD',
    name: 'United States Dollar'
  });

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
    const productsResponse = {
      data: [{
        id: "WATT-420-BEANS",
        name: "Watties Baked Beans - 420g can",
        description: "Baked Beans as they should be.",
        recommendedRetailPrice: 2.2,
        created: "2021-04-14T13:01:58.660Z"
      }]
    };
    const businessResponse = {
      data: {
      "id": 100,
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
    };

    const mockCurrencyData = {
      symbol: '$',
      code: 'NZD',
      name: 'New Zealand Dollar'
    };
    Api.getProducts.mockResolvedValue(productsResponse);
    Api.getBusiness.mockResolvedValue(businessResponse);

    const userCurrencyMock = jest.fn();
    userCurrencyMock.mockResolvedValue(mockCurrencyData);
    Api.getUserCurrency = userCurrencyMock;

    await wrapper.vm.getProducts(0);

    expect(wrapper.vm.items).toEqual(productsResponse.data);
    expect(wrapper.vm.currency).toEqual(mockCurrencyData);
    expect(userCurrencyMock).toHaveBeenCalledWith('New Zealand');
  });
});


describe('check-product-catalogue-page', () => {


  test('product-found-in-html-test-pagination-display', async () => {
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

    const pagination = wrapper.findComponent(Pagination);
    expect(pagination.exists()).toBeTruthy();
  });

  test('product-not-found-in-html-test-pagination-not-display', async () => {

    const response = null;
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(0);

    await wrapper.vm.$forceUpdate();

    const pagination = wrapper.findComponent(Pagination);
    expect(pagination.exists()).toBeFalsy();
  });

});

describe('check-setDescription-function', () => {
  test('description-less-then-20-characters', () => {
    const description = "Baked Beans as they";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Beans as they...');
  });

  test('description-1-character', () => {
    const description = "B";
    expect(wrapper.vm.setDescription(description)).toEqual('B...');
  });

  test('description-more-then-20-characters', () => {
    const description = "Baked Beans as they should be. Baked Beans as they should be. Baked Beans as they should be.";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Beans as they...');
  });

  test('description-less-then-20-characters-end-with-"."', () => {
    const description = "Baked Beans.";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Beans...');
  });

  test('description-less-then-20-characters-end-with-". "', () => {
    const description = "Baked Beans. ";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Beans...');
  });

  test('description-one-space', () => {
    const description = " ";
    expect(wrapper.vm.setDescription(description)).toEqual('...');
  });

  test('description-ten-space', () => {
    const description = "           ";
    expect(wrapper.vm.setDescription(description)).toEqual('...');
  });

  test('description-one-character-end-with-10-space', () => {
    const description = "a           ";
    expect(wrapper.vm.setDescription(description)).toEqual('a...');
  });

  test('description-one-character-start-with-10-space', () => {
    const description = "           a";
    expect(wrapper.vm.setDescription(description)).toEqual('a...');
  });
});

describe('check-setCreated-function', () => {
  test('set-mouth-less-then-10-data', () => {
    const created = "2021-04-14T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('14/04/2021');
  });

  test('set-month-more-then-9-data', () => {
    const created = "2021-10-14T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('14/10/2021');
  });

  test('set-day-more-then-9-data', () => {
    const created = "2021-10-10T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('10/10/2021');
  });

  test('set-day-less-then-10-data', () => {
    const created = "2021-10-02T13:01:58.660Z";
    expect(wrapper.vm.setCreated(created)).toEqual('02/10/2021');
  });
});

describe('check-model-product-card-page', () => {
  test('check-product-detail-card-component-exists', async () => {
    const response = {
      data: [{
        id: "WATT-420-BEANS1",
        name: "Watties Baked Beans - 430g can",
        description: "Aaked Beans as they should be.",
        recommendedRetailPrice: 2.2,
        created: "2021-03-14T13:01:58.660Z",
        image: 'https://mk0kiwikitchenr2pa0o.kinstacdn.com/wp-content/uploads/2016/05/Watties-Baked-Beans-In-Tomato-Sauce-420g.jpg',
      }]
    };
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(0);
    await wrapper.vm.tableRowClick(response.data[0]);

    await wrapper.vm.$forceUpdate();

    expect(wrapper.find(productDetailCard).exists()).toBeTruthy()
  });

  test('check-product-detail-card-component-exists-when-click-edit-button', async () => {
    const response = {
      data: [{
        id: "WATT-420-BEANS1",
        name: "Watties Baked Beans - 430g can",
        description: "Aaked Beans as they should be.",
        recommendedRetailPrice: 2.2,
        created: "2021-03-14T13:01:58.660Z",
        image: 'https://mk0kiwikitchenr2pa0o.kinstacdn.com/wp-content/uploads/2016/05/Watties-Baked-Beans-In-Tomato-Sauce-420g.jpg',
      }]
    };
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(0);
    await wrapper.vm.editProduct(response.data[0]);

    await wrapper.vm.$forceUpdate();

    expect(wrapper.find(productDetailCard).exists()).toBeTruthy()
  })
});

describe('Testing api put request and the response method with errors', () => {

  it('Succesfully edits a product ', async () => {
    Api.modifyProduct.mockResolvedValue({response : {status: 200}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.modifyProductAPI(mockEvent);

    expect(wrapper.vm.errors).toStrictEqual([]);
    expect(Api.getProducts).toHaveBeenCalledWith(0);
  });

  it('400 error test if Product ID already exists', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 400, data: "Product ID provided already exists."}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProductAPI(mockEvent);

    expect(wrapper.vm.errors).toStrictEqual(["Product ID provided already exists."]);
  });

  it('400 error test if Product ID provided does not exist', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 400, data: "Product does not exist."}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProductAPI(mockEvent);

    expect(wrapper.vm.errors).toStrictEqual(["Product does not exist."]);
  });

  it('403 error test', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 403}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProductAPI(mockEvent);

    expect(wrapper.vm.errors).toStrictEqual(["Forbidden. You are not an authorized administrator"]);
  });

  it('other error test', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 500}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProductAPI(mockEvent);

    expect(wrapper.vm.errors).toStrictEqual(["Server error"]);
  });

});
