import {mount, createLocalVue} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import DateRangeInput from '../../components/sales-report/DateRangeInput';

let wrapper;
jest
  .spyOn(global.Date, 'now')
  .mockImplementationOnce(() =>
    new Date(2021, 9, 9).valueOf()
  );


beforeEach(() => {
  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = mount(DateRangeInput, {
    localVue,
    propsData: {
      allTimeStart: new Date(2021, 1, 1)
    },
    mocks: {},
    stubs: {},
    methods: {},
  });
  wrapper.setProps({getSalesReport: jest.fn()});
});

afterEach(() => {
  wrapper.destroy();
});

const getLastEmitted = function() {
  return wrapper.emitted().input[wrapper.emitted().input.length - 1][0];
}

describe('DateRangeInput', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('emits all time by default', async () => {
    await wrapper.find("#filterDateBtn").trigger("submit");
    await wrapper.vm.$nextTick();
    const [start, end] = getLastEmitted();
    expect(start.getTime()).toBe((new Date(2021, 1, 1, 0, 0, 0)).getTime());
    expect(end.getTime()).toBe((new Date(2021, 9, 9, 23, 59, 59, 999)).getTime());
  });

  test('emits year range when select single year', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(1).setSelected();
    await wrapper.find("#yearSelector").setValue(2020);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = getLastEmitted();
    expect(start.getTime()).toBe((new Date(2020, 0, 1, 0, 0, 0)).getTime());
    expect(end.getTime()).toBe((new Date(2020, 11, 31, 23, 59, 59, 999)).getTime());
  });

  test('emits month range when select single month', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(2).setSelected();
    await wrapper.find("#yearSelector").setValue(2020);
    await wrapper.find("#monthSelector").setValue(5);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = getLastEmitted();
    expect(start.getTime()).toBe((new Date(2020, 5, 1, 0, 0, 0)).getTime());
    expect(end.getTime()).toBe((new Date(2020, 5, 30, 23, 59, 59, 999)).getTime());
  });

  test('emits week range when select single week', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(3).setSelected();
    wrapper.vm.selectedWeek = new Date(2021, 7, 26);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = getLastEmitted();
    expect(start.getTime()).toBe((new Date(2021, 7, 26, 0, 0, 0)).getTime());
    expect(end.getTime()).toBe((new Date(2021, 8, 1, 23, 59, 59, 999)).getTime());
  });

  test('emits day range when select single day', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(4).setSelected();
    wrapper.vm.selectedDay = new Date(2021, 3, 20);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = getLastEmitted();
    expect(start.getTime()).toBe((new Date(2021, 3, 20, 0, 0, 0)).getTime());
    expect(end.getTime()).toBe((new Date(2021, 3, 20, 23, 59, 59, 999)).getTime());
  });

  test('emits day range when select range of days', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(5).setSelected();
    wrapper.vm.startDay = new Date(2021, 3, 19);
    wrapper.vm.endDay = new Date(2021, 3, 20);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = getLastEmitted();
    expect(start.getTime()).toBe((new Date(2021, 3, 19, 0, 0, 0)).getTime());
    expect(end.getTime()).toBe((new Date(2021, 3, 20, 23, 59, 59, 999)).getTime());
  });

  test('disables filter button when date range invalid', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(5).setSelected();
    wrapper.vm.startDay = new Date(2021, 2, 21);
    wrapper.vm.endDay = new Date(2021, 2, 20);

    await wrapper.vm.$nextTick();

    expect(wrapper.find("#filterDateBtn").element.disabled).toBe(true);
  });
});
