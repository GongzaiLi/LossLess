import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import InventoryDetailCard from "../../components/inventory/InventoryDetailCard";


config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(InventoryDetailCard, {
    localVue,
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {},
  });

});

afterEach(() => {
  wrapper.destroy();
});

describe('check setDate function', () => {

  test('check setDate function with normal date', () => {
    const date = '2021-05-13T00:32:00Z';
    expect(wrapper.vm.setDate(date)).toStrictEqual('Thu, 13 May 2021');
  })
})



describe('check calculateTotalPrice function', () => {

  test('check calculateTotalPrice function with 0 price Per Item and 0 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 0;
    wrapper.vm.inventoryInfo.quantity = 0;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  });

  test('check calculateTotalPrice function with 1 price Per Item and 0 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 1;
    wrapper.vm.inventoryInfo.quantity = 0;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  })

  test('check calculateTotalPrice function with 0 price Per Item and 1 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 0;
    wrapper.vm.inventoryInfo.quantity = 1;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  })

  test('check calculateTotalPrice function with 10 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(100);
  })

  test('check calculateTotalPrice function with 10.01 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10.01;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(100.1);
  })

  test('check calculateTotalPrice function with 10.21 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10.21;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(102.1);
  })

})
