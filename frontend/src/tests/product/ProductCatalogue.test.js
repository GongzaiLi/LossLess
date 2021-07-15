import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import productCatalogue from '../../components/product/ProductCatalogue';
import Api from "../../Api";
import productDetailCard from "../../components/product/ProductDetailCard";

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

const $currentUser = {
  role: 'user',
  currentlyActingAs: {
    id: 0
  },
  businessesAdministered: [
    {id: 0, name: "blah"},
    {id: 1, name: "blah1"},
    {id: 2, name: "blah2"}
  ]
};


jest.mock('../../Api');

beforeEach(() => {

  Api.getBusiness.mockResolvedValue({data: {name: "Blah"}});

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(productCatalogue, {
    localVue,
    propsData: {},
    mocks: {$route, $log, $currentUser},
    methods: {},
  });

  wrapper.vm.$refs.productTable = {
    getProducts: jest.fn()
  };
});

afterEach(() => {
  wrapper.destroy();
});

describe('check-model-product-card-page', () => {
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

    await wrapper.vm.refreshTable(0);
    await wrapper.vm.openEditProductCard(response.data[0]);

    expect(wrapper.find(productDetailCard).exists()).toBeTruthy()
  })

  test('check-product-detail-card-component-exists-when-click-create-button', async () => {
    await wrapper.vm.openCreateProductModal();

    expect(wrapper.find(productDetailCard).exists()).toBeTruthy()
  })

  test('check-product-detail-card-component-not-exists-when-click-close-button', async () => {
    await wrapper.vm.openCreateProductModal();

    await wrapper.vm.closeProductCardModal();

    expect(wrapper.find(productDetailCard).exists()).toBeTruthy()
  })
});

describe('Testing api put/post request and the response method with errors', () => {

  it('Succesfully edits a product ', async () => {
    Api.modifyProduct.mockResolvedValue({response : {status: 201}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.modifyProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("");
    expect(Api.modifyProduct).toHaveBeenCalled();
  });

  it('Succesfully creates a product ', async () => {
    Api.createProduct.mockResolvedValue({response : {status: 201}, data: {productId: 'AAA'}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.createProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("");
    expect(Api.createProduct).toHaveBeenCalled();
  });

  it('Create product but receives 413 (image) error ', async () => {
    Api.createProduct.mockResolvedValue({response : {status: 201}, data: {productId: '51-A'}});
    Api.uploadProductImage.mockRejectedValue({response : {status: 413, data: "Image larger than 5MB"}});

    wrapper.vm.productDisplayedInCard.images = [{filename: 'blah'}];
    await wrapper.vm.createProduct();

    expect(wrapper.vm.productCardError).toBe("");
    expect(wrapper.vm.imageError).toBe("Image larger than 5MB");
  });

  it('400 error test if Product ID already exists', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 400, data: "Product ID provided already exists."}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("Product ID provided already exists.");
  });

  it('400 error test if Product ID provided does not exist', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 400, data: "Product does not exist."}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("Product does not exist.");
  });

  it('403 error test', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 403}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("Forbidden. You are not an authorized administrator");
  });

  it('no internet test', async () => {
    Api.modifyProduct.mockRejectedValue({request: {path: 'blah'}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("No Internet Connectivity");
  });

  it('other error test', async () => {
    Api.modifyProduct.mockRejectedValue({response : {status: 500}});

    const mockEvent = {preventDefault: jest.fn()};
    await wrapper.vm.modifyProduct(mockEvent);

    expect(wrapper.vm.productCardError).toBe("Server error");
  });

});

describe('businessNameIfAdminOfThisBusiness', () => {

  it('Works if user admins business', async () => {
    expect(wrapper.vm.businessNameIfAdminOfThisBusiness).toBe("blah");
  });

  it('Works if user not admins business', async () => {
    wrapper.vm.$route = {
      params: {
        id: 3
      }
    }

    expect(wrapper.vm.businessNameIfAdminOfThisBusiness).toBe(null);
  });

})


describe('Testing currently acting as watcher', () => {

  it('Does not load data if switch to normal user', async () => {
    wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', null);

    jest.spyOn(wrapper.vm, 'refreshTable');
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.refreshTable).not.toBeCalled();
  });

  it('Does not load data if switch to other business', async () => {
    wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 1});

    jest.spyOn(wrapper.vm, 'refreshTable');
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.refreshTable).not.toBeCalled();
  });

  it('Loads if switch to other business and acting as', async () => {
    wrapper.vm.$route = {
      params: {
        id: 2
      }
    }
    wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 2});

    jest.spyOn(wrapper.vm, 'refreshTable');
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.refreshTable).toHaveBeenCalledWith(2);
  });


  it('Loads data if switch to other business but is admin', async () => {
    wrapper.vm.$set(wrapper.vm.$currentUser, 'role', 'globalApplicationAdmin');
    wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 1});

    jest.spyOn(wrapper.vm, 'refreshTable');
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.refreshTable).toHaveBeenCalledWith(0);
  });

});
