import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import CreateProduct from '../../components/product/CreateProduct';
import VueRouter from 'vue-router';
import Api from "../../Api";

jest.mock('../../Api')

const $log = {
  debug: jest.fn(),
};





config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;



const id = "WATT-420-BEANS"
let name = "Beans"
const description = "Baked Beans.";
const manufacturer = "Watties";
const recommendedRetailPrice = "1.00";



beforeEach(() => {
  const localVue = createLocalVue();
  const router = new VueRouter();

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(VueRouter);

  wrapper = mount(CreateProduct, {
    localVue,
    router,
    propsData: {},
    mocks: {$log},
    stubs: {},
    methods: {},
  });

  wrapper.vm.businessId = 0;
});

afterEach(() => {
  wrapper.destroy();
});

describe('CreateProduct Script Testing', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });



  test('getProductData returns correct number of attributes', async () => {
    const expectedAttributes = 5;
    expect(Object.keys(wrapper.vm.getProductData()).length).toBe(expectedAttributes);

  });

  test('Get business data returns correct values', () => {


    wrapper.vm.name = name;
    wrapper.vm.description = description;
    wrapper.vm.manufacturer = manufacturer;
    wrapper.vm.recommendedRetailPrice = recommendedRetailPrice;
    wrapper.vm.id = id;

    expect(wrapper.vm.getProductData().name).toBe(name);
    expect(wrapper.vm.getProductData().description).toBe(description);
    expect(wrapper.vm.getProductData().manufacturer).toBe(manufacturer);
    expect(wrapper.vm.getProductData().recommendedRetailPrice).toBe(recommendedRetailPrice);
    expect(wrapper.vm.getProductData().id).toBe(id);
  });
});

describe('Testing api post request and the response method with errors', () => {

  it('Succesfully creates a product ', async () => {
    Api.createProduct.mockImplementation(() => Promise.resolve({
      response : {status: 200}
    }));
    const mockEvent = {preventDefault: jest.fn()}
    const result = await wrapper.vm.CreateProduct(mockEvent);
    expect(result).toBe("success");
  });
  it('400 error test', async () => {

    Api.createProduct.mockImplementation(() => Promise.reject({
      response : {status: 400}
    }));
    const mockEvent = {preventDefault: jest.fn()};
    const result = await wrapper.vm.CreateProduct(mockEvent);
    expect(result).toBe("Creation failed. Please try again");
  });
  it('403 error test', async () => {

    Api.createProduct.mockImplementation(() => Promise.reject({
      response : {status: 403}
    }));
    const mockEvent = {preventDefault: jest.fn()};
    const result = await wrapper.vm.CreateProduct(mockEvent);
    expect(result).toBe("Forbidden. You are not an authorized administrator");
  });
  it('other error test', async () => {

    Api.createProduct.mockImplementation(() => Promise.reject({
      response : {status: 500}
    }));
    const mockEvent = {preventDefault: jest.fn()};
    const result = await wrapper.vm.CreateProduct(mockEvent);
    expect(result).toBe("Server error");
  });




});


