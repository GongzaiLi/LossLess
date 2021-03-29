import {createLocalVue, shallowMount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import UserSearch from '../UserSearch'; // name of your Vue component

let wrapper;

let userData = {
  role:"user"
}
// fake the localStorage to doing the testing.
const mockUserAuthPlugin = function install(Vue) {
  Vue.mixin({
    methods: {
      $getCurrentUser: () => userData
    }
  });
}

beforeEach(() => {

  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(mockUserAuthPlugin);

  wrapper = shallowMount(UserSearch, {
    localVue,
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {},
    computed: {},
  })
});

afterEach(() => {
  wrapper.destroy();
});

describe('UserSearch', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});

//Boundary itemsRangeMin()
test('1_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMin', async () => {


  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

test('1000_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

test('1_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

test('1000_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(1);
})

//Blue sky itemsRangeMin()
test('50_total_result-in-10_perPage-in-2_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(11);
})

test('50_total_result-in-10_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(11);
})

test('100_total_result-in-20_perPage-in-5_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 100;
  wrapper.vm.perPage = 20;
  wrapper.vm.currentPage = 5;
  expect(wrapper.vm.itemsRangeMin)
    .toBe(81);
})
//Exception itemsRangeMin()
test('0_total_result-in-0_perPage-in-1_currentPage-to-itemsRangeMin', async () => {
  wrapper.vm.totalResults = 0;
  wrapper.vm.perPage = 0;
  wrapper.vm.currentPage = 1;
  expect(parseInt(wrapper.vm.itemsRangeMin))
    .toEqual(0);
})



//Boundary itemsRangeMax()
test('1_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1);
})

test('1000_total_result-in-1_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1);
})

test('1_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1);
})

test('1000_total_result-in-1000_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 1000;
  wrapper.vm.perPage = 1000;
  wrapper.vm.currentPage = 1;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(1000);
})

//Blue sky itemsRangeMax()
test('50_total_result-in-10_perPage-in-2_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(20);
})

test('50_total_result-in-10_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 2;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(20);
})

test('100_total_result-in-20_perPage-in-5_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 100;
  wrapper.vm.perPage = 20;
  wrapper.vm.currentPage = 5;
  expect(wrapper.vm.itemsRangeMax)
    .toBe(100);
})
//Exception itemsRangeMax()
test('0_total_result-in-0_perPage-in-1_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 0;
  wrapper.vm.perPage = 0;
  wrapper.vm.currentPage = 1;
  expect(parseInt(wrapper.vm.itemsRangeMax))
    .toEqual(0);
})
test('51_total_result-in-10_perPage-in-6_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 51;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 6;
  expect(parseInt(wrapper.vm.itemsRangeMax))
      .toEqual(51);
})
test('50_total_result-in-10_perPage-in-5_currentPage-to-itemsRangeMax', async () => {
  wrapper.vm.totalResults = 50;
  wrapper.vm.perPage = 10;
  wrapper.vm.currentPage = 5;
  expect(parseInt(wrapper.vm.itemsRangeMax))
      .toEqual(50);
})