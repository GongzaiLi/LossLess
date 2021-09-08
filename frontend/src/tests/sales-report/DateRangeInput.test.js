import {mount, createLocalVue} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import DateRangeInput from '../../components/sales-report/DateRangeInput';

let wrapper;
// let mockDateNow = '2019-05-14T11:01:58.135Z';
// jest
//   .spyOn(global.Date, 'now')
//   .mockImplementationOnce(() =>
//     new Date(mockDateNow).valueOf()
//   );


beforeEach(() => {
  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = mount(DateRangeInput, {
    localVue,
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {},
  });
  wrapper.setProps({getSalesReport: jest.fn()});
});

afterEach(() => {
  wrapper.destroy();
});

// const getLastEmitted = function () {
//   return wrapper.emitted().input[wrapper.emitted().input.length - 1][0];
// }

describe('DateRangeInput', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('emits null by default', async () => {
    await wrapper.find("#filterDateBtn").trigger("submit");
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateRange).toStrictEqual(null);
  });

  test('emits year range when select single year', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(1).setSelected();
    await wrapper.find("#yearSelector").setValue(2020);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = wrapper.vm.dateRange;
    expect(start.toLocaleString()).toBe("1/1/2020, 12:00:00 AM");
    expect(end.toLocaleString()).toBe("12/31/2020, 11:59:59 PM");
  });

  test('emits month range when select single month', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(2).setSelected();
    await wrapper.find("#yearSelector").setValue(2020);
    await wrapper.find("#monthSelector").setValue(5);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = wrapper.vm.dateRange;
    expect(start.toLocaleString()).toBe("6/1/2020, 12:00:00 AM");
    expect(end.toLocaleString()).toBe("6/30/2020, 11:59:59 PM");
  });

  test('emits week range when select single week', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(3).setSelected();
    wrapper.vm.selectedWeek = new Date(2021, 7, 29);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = wrapper.vm.dateRange;
    expect(start.toLocaleString()).toBe("8/29/2021, 12:00:00 AM");
    expect(end.toLocaleString()).toBe("9/4/2021, 11:59:59 PM");
  });

  test('emits day range when select single day', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(4).setSelected();
    wrapper.vm.selectedDay = new Date(2021, 3, 20);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = wrapper.vm.dateRange;
    expect(start.toLocaleString()).toBe("4/20/2021, 12:00:00 AM");
    expect(end.toLocaleString()).toBe("4/20/2021, 11:59:59 PM");
  });

  test('emits day range when select range of days', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(5).setSelected();
    wrapper.vm.startDay = new Date(2021, 3, 19);
    wrapper.vm.endDay = new Date(2021, 3, 20);
    await wrapper.find("#filterDateBtn").trigger("submit");

    await wrapper.vm.$nextTick();
    const [start, end] = wrapper.vm.dateRange;
    expect(start.toLocaleString()).toBe("4/19/2021, 12:00:00 AM");
    expect(end.toLocaleString()).toBe("4/20/2021, 11:59:59 PM");
  });

  test('disables filter button when date range invalid', async () => {
    await wrapper.find('#dateTypeSelect').findAll('option').at(5).setSelected();
    wrapper.vm.startDay = new Date(2021, 3, 21);
    wrapper.vm.endDay = new Date(2021, 3, 20);

    await wrapper.vm.$nextTick();

    expect(wrapper.find("#filterDateBtn").element.disabled).toBe(true);
  });
});