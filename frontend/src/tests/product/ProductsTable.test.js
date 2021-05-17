import Api from "../../Api";
import {createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
import ProductsTable from "../../components/product/ProductsTable";
import Pagination from "../../components/model/Pagination";


jest.mock('../../Api');

let wrapper

const $log = {
  debug() {
  }
};

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

  wrapper = shallowMount(ProductsTable, {
    localVue,
    mocks: {
      $log
    }
  });

});

const business = {
  "id": 0,
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
};

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

    const mockCurrencyData = {
      symbol: '$',
      code: 'NZD',
      name: 'New Zealand Dollar'
    };
    Api.getProducts.mockResolvedValue(productsResponse);

    const userCurrencyMock = jest.fn();
    userCurrencyMock.mockResolvedValue(mockCurrencyData);
    Api.getUserCurrency = userCurrencyMock;

    await wrapper.vm.getProducts(business);

    expect(wrapper.vm.items).toEqual(productsResponse.data);
    expect(wrapper.vm.currency).toEqual(mockCurrencyData);
    expect(userCurrencyMock).toHaveBeenCalledWith('New Zealand');
  });
});


describe('check-product-catalogue-page', () => {

  test('product-found-in-html-test-product-table-display', async () => {
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
    await wrapper.vm.getProducts(business);
    await wrapper.vm.$forceUpdate();


    expect(wrapper.vm.$refs.productCatalogueTable.$props.items[0].id).toBe("WATT-420-BEANS");
    expect(wrapper.vm.$refs.productCatalogueTable.$props.items[0].name).toBe("Watties Baked Beans - 420g can");
    expect(wrapper.vm.$refs.productCatalogueTable.$props.items[0].description).toBe("Baked Beans as they should be.");
    expect(wrapper.vm.$refs.productCatalogueTable.$props.items[0].recommendedRetailPrice).toBe(2.2);
    expect(wrapper.vm.$refs.productCatalogueTable.$props.items[0].created).toBe("2021-04-14T13:01:58.660Z");
  });


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
    await wrapper.vm.getProducts(business);

    await wrapper.vm.$forceUpdate();

    const pagination = wrapper.findComponent(Pagination);
    expect(pagination.exists()).toBeTruthy();
  });

  test('product-not-found-in-html-test-pagination-not-display', async () => {

    const response = null;
    Api.getProducts.mockResolvedValue(response);
    await wrapper.vm.getProducts(business);

    await wrapper.vm.$forceUpdate();

    const pagination = wrapper.findComponent(Pagination);
    expect(pagination.exists()).toBeFalsy();
  });

});


describe('check-setDescription-function', () => {
  test('description-less-then-10-characters', () => {
    const description = "Baked Bean";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Bean');
  });

  test('description-1-character', () => {
    const description = "B";
    expect(wrapper.vm.setDescription(description)).toEqual('B');
  });

  test('description-more-then-10-characters', () => {
    const description = "Baked Beans as they should be. Baked Beans as they should be. Baked Beans as they should be.";
    expect(wrapper.vm.setDescription(description)).toEqual('Baked Bean...');
  });

  test('description-one-space', () => {
    const description = " ";
    expect(wrapper.vm.setDescription(description)).toEqual('');
  });

  test('description-ten-space', () => {
    const description = "          ";
    expect(wrapper.vm.setDescription(description)).toEqual('');
  });

  test('description-one-character-end-with-10-space', () => {
    const description = "a          ";
    expect(wrapper.vm.setDescription(description)).toEqual('a...');
  });

  test('description-one-character-start-with-10-space', () => {
    const description = "         a";
    expect(wrapper.vm.setDescription(description)).toEqual('a');
  });
});