import {config, createLocalVue, mount} from "@vue/test-utils";
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
import Api from "../../Api";
import SalesReportTab from "../../components/sales-report/SalesReportTab";

config.showDeprecationWarnings = false  //to disable deprecation warnings
let wrapper;

const $log = {
  debug() {
  }
};

const SalesReport = {
  reportData: [
    {
      "startDate": "2021-09-01",
      "endDate": "2021-09-01",
      "totalPurchases": 1,
      "totalValue": 1.0
    },
    {
      "startDate": "2021-09-02",
      "endDate": "2021-09-02",
      "totalPurchases": 2,
      "totalValue": 2.0
    },
    {
      "startDate": "2021-09-03",
      "endDate": "2021-09-03",
      "totalPurchases": 3,
      "totalValue": 3.0
    },
    {
      "startDate": "2021-09-04",
      "endDate": "2021-09-04",
      "totalPurchases": 4,
      "totalValue": 4
    }
  ],
  startTruncated: false,
  endTruncated: false,
}

const $route = {
  params: {
    id: 0
  }
};

// This 'mocks' out the html canvas as it's not implemented by the jsdom by default
// CALEB IS A GENIUS
document.getElementById = () => document.createElement('canvas');

jest.mock('../../Api');

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  Api.getSalesReport.mockResolvedValue({data: SalesReport});

  wrapper = mount(SalesReportTab, { //shallowMount and mount has different work
    localVue,
    propsData: {
      dateRange: [new Date(2010, 1, 1), new Date(2021, 1, 1)],
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'United States Dollar'
      }
    },
    mocks: {$route, $log},
    methods: {},
  });

});

afterEach(() => {
  wrapper.destroy();
});

describe('test get request with the getSalesReport', () => {

  it('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  it('test getSalesReport method when get 2021-09-01 to 2021-09-04 is successful', async () => {
    let dateRange = [new Date('2021-09-01'), new Date('2021-09-04')];
    Api.getSalesReport.mockResolvedValue({data: SalesReport});
    await wrapper.vm.getSalesReport(dateRange);

    await wrapper.vm.$forceUpdate();

    expect(wrapper.vm.reportDataList).toStrictEqual(SalesReport.reportData);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[0]).toStrictEqual(SalesReport.reportData[0]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[1]).toStrictEqual(SalesReport.reportData[1]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[2]).toStrictEqual(SalesReport.reportData[2]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[3]).toStrictEqual(SalesReport.reportData[3]);

    expect(wrapper.findAll('h4').at(0).text()).toEqual(`10 Total Items Sold`);
    expect(wrapper.findAll('h4').at(1).text()).toEqual(`$10 USD Total Value`);

    expect(Api.getSalesReport).toHaveBeenCalled();
  });
})


describe('check the table fields', () => {
  beforeEach(async () => {
    const dateRange = [new Date(2010, 1, 1), new Date(2021, 1, 1)];
    Api.getSalesReport.mockResolvedValue({data: SalesReport});
    await wrapper.vm.getSalesReport(dateRange);
    await wrapper.vm.$nextTick();
  })

  it('the date groupBy day', async () => {
    await wrapper.find('#periodSelector').findAll('option').at(0).setSelected();
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.groupBy).toBe('day');

    expect(wrapper.vm.$refs.salesReportTable.$props.fields.length).toBe(3);
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[0].label).toBe('Date');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[1].key).toBe('totalPurchases');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[2].key).toBe('totalValue');
  });
  it('the date groupBy week', async () => {

    await wrapper.find('#periodSelector').findAll('option').at(1).setSelected();
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.groupBy).toBe('week');

    expect(wrapper.vm.$refs.salesReportTable.$props.fields.length).toBe(4);
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[0].key).toBe('startDate');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[1].key).toBe('endDate');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[2].key).toBe('totalPurchases');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[3].key).toBe('totalValue');

  });

  it('the date groupBy month', async () => {
    await wrapper.find('#periodSelector').findAll('option').at(2).setSelected();
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.groupBy).toBe('month');

    expect(wrapper.vm.$refs.salesReportTable.$props.fields.length).toBe(3);
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[0].label).toBe('Month');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[1].key).toBe('totalPurchases');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[2].key).toBe('totalValue');

  });

  it('the date groupBy year', async () => {
    await wrapper.find('#periodSelector').findAll('option').at(3).setSelected();
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.groupBy).toBe('year');

    expect(wrapper.vm.$refs.salesReportTable.$props.fields.length).toBe(3);
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[0].label).toBe('Year');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[1].key).toBe('totalPurchases');
    expect(wrapper.vm.$refs.salesReportTable.$props.fields[2].key).toBe('totalValue');
  });

})


describe('check groupBy resets when date range narrowed', () => {
  beforeAll(async () => {
    Api.getSalesReport.mockResolvedValue({data: SalesReport});
  })

  it('Narrows to month when narrow range from several years to one year', async () => {
    wrapper.vm.groupBy = 'year'
    const dateRange = [new Date(2021, 0, 1), new Date(2021, 11, 31)];
    await wrapper.setProps({
      dateRange
    });
    await wrapper.vm.getSalesReport(dateRange);

    expect(wrapper.vm.groupBy).toBe('month');
    expect(wrapper.vm.groupByOptions).toStrictEqual({day: 'Daily', week: 'Weekly', month: 'Monthly'});
  });

  it('Narrows to day when narrow range from year to one week', async () => {
    wrapper.vm.groupBy = 'month'
    const dateRange = [new Date(2021, 11, 27), new Date(2021, 11, 31)];
    await wrapper.setProps({
      dateRange
    });
    await wrapper.vm.getSalesReport(dateRange);

    expect(wrapper.vm.groupBy).toBe('day');
    expect(wrapper.vm.groupByOptions).toStrictEqual({day: 'Daily'});
  });

  it('Goes to year when range increases from one day to two years', async () => {
    wrapper.vm.groupBy = 'day'
    const dateRange = [new Date(2020, 0, 1), new Date(2021, 11, 31)];
    await wrapper.setProps({
      dateRange
    });
    await wrapper.vm.getSalesReport(dateRange);

    expect(wrapper.vm.groupBy).toBe('year');
    expect(wrapper.vm.groupByOptions).toStrictEqual({
      "day": "Daily",
      "month": "Monthly",
      "week": "Weekly",
      "year": "Yearly",
    });
  });

  it('Does not change when only groupBy changes', async () => {
    wrapper.vm.groupBy = 'year';
    const dateRange = [new Date(2020, 0, 1), new Date(2021, 11, 31)];
    await wrapper.setProps({
      dateRange
    });
    await wrapper.vm.getSalesReport(dateRange);

    wrapper.vm.groupBy = 'month';
    await wrapper.vm.getSalesReport(dateRange, true);

    expect(wrapper.vm.groupBy).toBe('month');
    expect(wrapper.vm.groupByOptions).toStrictEqual({
      "day": "Daily",
      "month": "Monthly",
      "week": "Weekly",
      "year": "Yearly",
    });
  });
})

describe('Date truncated message', () => {
  beforeAll(async () => {
    Api.getSalesReport.mockResolvedValue({data: SalesReport});
  })

  it('Does not exist if periods align', async () => {
    wrapper.vm.startTruncated = false;
    wrapper.vm.endTruncated = false;

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.dateTruncatedMessage).toBeFalsy();
  });

  it('Shows correct message when start date truncates', async () => {
    wrapper.vm.startTruncated = true;
    wrapper.vm.endTruncated = false;

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.dateTruncatedMessage).toBe("The report does not start on the first day of a year, so the first year in the data will be truncated by the start date.");
  });

  it('Shows correct message when end date truncates', async () => {
    wrapper.vm.startTruncated = false;
    wrapper.vm.endTruncated = true;

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.dateTruncatedMessage).toBe("The report does not end on the last day of a year, so the last year in the data will be truncated by the end date.");
  });

  it('Shows correct message when start and end date truncates', async () => {
    wrapper.vm.startTruncated = true;
    wrapper.vm.endTruncated = true;

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.dateTruncatedMessage).toBe("The report does not start on the first day of a year or end on the last day of a year, so the first and last years will be truncated by the start and end dates.");
  });

})