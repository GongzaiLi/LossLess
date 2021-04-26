import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue} from 'bootstrap-vue';
import CreateProduct from '../CreateProduct';
import VueRouter from 'vue-router';
import Api from "../../Api";

jest.mock('../../Api')

const $log = {
  debug: jest.fn(),
};



const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(VueRouter);

config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;

const router = new VueRouter();


let fullName = "Beans"
const description = "Baked Beans.";
const manufacturer = "Watties";
const recommendedRetailPrice = "1.00";



beforeEach(() => {
  wrapper = mount(CreateProduct, {
    localVue,
    router,
    mocks: {$log}
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('CreateProduct Script Testing', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });



  test('getProductData returns correct number of attributes', async () => {
    const expectedAttributes = 4;
    expect(Object.keys(wrapper.vm.getProductData()).length).toBe(expectedAttributes);

  });

  test('Get business data returns correct values', () => {


    wrapper.vm.fullName = fullName;
    wrapper.vm.description = description;
    wrapper.vm.manufacturer = manufacturer;
    wrapper.vm.recommendedRetailPrice = recommendedRetailPrice;

    expect(wrapper.vm.getProductData().fullName).toBe(fullName);
    expect(wrapper.vm.getProductData().description).toBe(description);
    expect(wrapper.vm.getProductData().manufacturer).toBe(manufacturer);
    expect(wrapper.vm.getProductData().recommendedRetailPrice).toBe(recommendedRetailPrice);
  });
});

describe('Testing api post request and the response method with errors', () => {

  it('Succesfully creates a product', async () => {
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


