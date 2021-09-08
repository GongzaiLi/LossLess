import {config, createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
import Api from "../../Api";
import SalesReportPage from "../../components/sales-report/SalesReportPage";

config.showDeprecationWarnings = false  //to disable deprecation warnings
let wrapper;

const $log = {
  debug() {
  }
};

const SalesReport = [
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
]

const $route = {
  params: {
    id: 0
  }
};


const $currentUser = {
  role: 'user',
  currentlyActingAs: {
    id: 0
  },
  businessesAdministered: [
    {id: 0, name: "blah"},
  ]
};


jest.mock('../../Api');

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  Api.getBusiness.mockResolvedValue({data: {"address": {"country": "New Zealand"}}});
  Api.getUserCurrency.mockResolvedValue({
    symbol: '$',
    code: 'USD',
    name: 'United States Dollar'
  });

  wrapper = shallowMount(SalesReportPage, {
    localVue,
    propsData: {},
    mocks: {$route, $currentUser, $log},
    stubs: {'another-component': true},
    methods: {},
  });

});

afterEach(() => {
  wrapper.destroy();
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


describe('test get request with the getSalesReport', () => {

  it('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  it('test getSalesReport method when get 2021-09-01 to 2021-09-04 is successful', async () => {
    let dateRange = [new Date('2021-09-01'), new Date('2021-09-04')];
    Api.getSalesReport.mockResolvedValue({data: SalesReport});
    await wrapper.vm.getSalesReport(dateRange);

    await wrapper.vm.$forceUpdate();

    expect(wrapper.vm.groupedResults).toStrictEqual(SalesReport);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[0]).toStrictEqual(SalesReport[0]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[1]).toStrictEqual(SalesReport[1]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[2]).toStrictEqual(SalesReport[2]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items[3]).toStrictEqual(SalesReport[3]);

    let totalPurchases = SalesReport.reduce((count, item) => {
      return count + item.totalValue
    }, 0);

    let totalValue = SalesReport.reduce((count, item) => {
      return count + item.totalValue
    }, 0);

    expect(wrapper.findAll('h4').at(0).text()).toEqual(`${totalPurchases} Total Items Sold`);
    expect(wrapper.findAll('h4').at(1).text()).toEqual(`$${totalValue} USD Total Value`);

    expect(Api.getSalesReport).toHaveBeenCalled();
  });


  it('test getSalesReport method when get data is failed', async () => {
    let dateRange = [new Date('2021-09-01'), new Date('2021-09-04')];
    Api.getSalesReport.mockRejectedValue();
    await wrapper.vm.getSalesReport(dateRange);

    await wrapper.vm.$forceUpdate();

    expect(wrapper.vm.groupedResults).toStrictEqual([]);
    expect(wrapper.vm.$refs.salesReportTable.$props.items).toStrictEqual([]);


    expect(wrapper.findAll('h4').at(0).text()).toEqual(`0 Total Items Sold`);
    expect(wrapper.findAll('h4').at(1).text()).toEqual(`$0 USD Total Value`);

    expect(Api.getSalesReport).toHaveBeenCalled();
  });
})

describe('check the date range is the same day', () => {

  it('the date range is the same day', async () => {
    wrapper.vm.dateRange = [new Date('2021-09-01'), new Date('2021-09-01')];
    expect(wrapper.vm.isOneDay).toBeTruthy();
  });

  it('Works if user not admins business', async () => {
    wrapper.vm.dateRange = [new Date('2021-09-01'), new Date('2021-09-02')];
    expect(wrapper.vm.isOneDay).toBeFalsy();
  });

})