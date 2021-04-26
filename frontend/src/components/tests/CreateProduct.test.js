import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue} from 'bootstrap-vue';
import CreateProduct from '../CreateProduct';
import VueRouter from 'vue-router';


let userData = {
  id: 1
}
const $log = {
  debug: jest.fn(),
};

// const $currentUser = {
//   id: 1
// }


// fake the localStorage to doing the testing.
const mockUserAuthPlugin = function install(Vue) {
  Vue.mixin({
    computed: {
      $currentUser: {
        get: function () {
          return userData;
        }
      },
    }
  });
}


const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(VueRouter);
localVue.use(mockUserAuthPlugin);

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
  const response401 = ["error", {response: {status: 401}, message: "Access token is missing or invalid"}];
  const responseNot401 = ["error", {response: {status: 500}, message: "Network Error"}];
  const responseNoError = ["success", {response: {status: 201}, message: "Business Registered"}];


  it('Succesful api sent', async () => {

    const mockEvent = {preventDefault: jest.fn()}
    const result = await wrapper.vm.CreateProduct(mockEvent);
    expect(result).toBe("success");
  });




});


