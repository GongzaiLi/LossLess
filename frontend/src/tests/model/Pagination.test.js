import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import pagination from "../../components/model/Pagination";

let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings

const localVue = createLocalVue()
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);

beforeEach(() => {
  wrapper = shallowMount(pagination, {
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

describe('Pagination', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});


describe('check-itemsRangeMin-function', () => {

  test('1_total_result-in-1_perPage-in-1_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 1, perPage: 1});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMin).toBe(1);
  });

  test('1_total_result-in-100_perPage-in-1_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 1, perPage: 100});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMin).toBe(1);
  });

  test('100_total_result-in-1_perPage-in-1_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 100, perPage: 1});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMin).toBe(1);

  });

  test('1000_total_result-in-10_perPage-in-10_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 1});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMin).toBe(1);
  });

  test('100_total_result-in-10_perPage-in-10_currentPage-min', async () => {


    await wrapper.setProps({totalItems: 100, perPage: 10});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMin).toBe(91);
  });

  test('1000_total_result-in-10_perPage-in-10_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 10});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMin).toBe(91);
  });

  test('100_total_result-in-5_perPage-in-10_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 100, perPage: 5});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMin).toBe(46);
  });

  test('1000_total_result-in-5_perPage-in-10_currentPage-min', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 5});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMin).toBe(46);
  });

  test('100_total_result-in-1_perPage-in-10_currentPage', async () => {

    await wrapper.setProps({totalItems: 100, perPage: 1});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMin).toBe(10);
  });

  test('1000_total_result-in-1_perPage-in-10_currentPage', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 1});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMin).toBe(10);
  });

});


describe('check-itemsRangeMax-function', () => {

  test('1_total_result-in-1_perPage-in-1_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1, perPage: 1});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMax).toBe(1);
  });

  test('1_total_result-in-100_perPage-in-1_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1, perPage: 100});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMax).toBe(1);
  });

  test('100_total_result-in-1_perPage-in-1_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 100, perPage: 1});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMax).toBe(1);

  });

  test('1000_total_result-in-10_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 1});
    wrapper.vm.currentPage = 1;
    expect(wrapper.vm.itemsRangeMax).toBe(1);
  });

  test('100_total_result-in-10_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 100, perPage: 10});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMax).toBe(100);
  });

  test('1000_total_result-in-10_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 10});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMax).toBe(100);
  });

  test('100_total_result-in-5_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 100, perPage: 5});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMax).toBe(50);
  });

  test('1000_total_result-in-5_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 5});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMax).toBe(50);
  });

  test('1000_total_result-in-1_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 1});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMax).toBe(10);
  });

  test('1000_total_result-in-100_perPage-in-10_currentPage-max', async () => {

    await wrapper.setProps({totalItems: 1000, perPage: 100});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.itemsRangeMax).toBe(1000);
  });
});

describe('check-jumpToPagesOptions-function', () => {
  test('10_total_items-in-10_per_page-for-1-page', async () => {
    await wrapper.setProps({totalItems: 10, perPage: 10});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.jumpToPagesOptions).toStrictEqual([1]);
  });

  test('100_total_items-in-10_per_page-for-10-pages', async () => {
    await wrapper.setProps({totalItems: 100, perPage: 10});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.jumpToPagesOptions).toStrictEqual([1,2,3,4,5,6,7,8,9,10]);
  });

  test('1_total_items-in-100_per_page-for-1-page', async () => {
    await wrapper.setProps({totalItems: 1, perPage: 100});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.jumpToPagesOptions).toStrictEqual([1]);
  });

  test('9_total_items-in-2_per_page-for-1-page', async () => {
    await wrapper.setProps({totalItems: 9, perPage: 2});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.jumpToPagesOptions).toStrictEqual([1,2,3,4,5]);
  });

  test('0_total_items-in-2_per_page-for-0-page', async () => {
    await wrapper.setProps({totalItems: 0, perPage: 2});
    wrapper.vm.currentPage = 10;
    expect(wrapper.vm.jumpToPagesOptions).toStrictEqual([]);
  });
})

describe('check-pageChange-function', () => {
  test('when-change-currentPage', async () => {
    expect(wrapper.vm.currentPage).toBe(1);
    wrapper.vm.currentPage = 100;
    await wrapper.vm.pageChange();

    await wrapper.vm.$forceUpdate();

    const [[emitted]] = wrapper.emitted().input;
    expect(emitted).toBe(100);
  });
});

