import {config, createLocalVue, mount} from "@vue/test-utils";
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

// This 'mocks' out the html canvas as it's not implemented by the jsdom by default
// CALEB IS A GENIUS
document.getElementById = () => document.createElement('canvas');

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

    wrapper = mount(SalesReportPage, { //shallowMount and mount has different work
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
        expect(wrapper.contains('#total-results')).toBeFalsy();

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
        await wrapper.vm.getSalesReport(dateRange);

        expect(wrapper.vm.groupBy).toBe('month');
        expect(wrapper.vm.groupByOptions).toStrictEqual({day: 'Daily', week: 'Weekly', month: 'Monthly'});
    });

    it('Narrows to day when narrow range from year to one week', async () => {
        wrapper.vm.groupBy = 'month'
        const dateRange = [new Date(2021, 11, 27), new Date(2021, 11, 31)];
        await wrapper.vm.getSalesReport(dateRange);

        expect(wrapper.vm.groupBy).toBe('day');
        expect(wrapper.vm.groupByOptions).toStrictEqual({day: 'Daily'});
    });

    it('Goes to year when range increases from one day to two years', async () => {
        wrapper.vm.groupBy = 'day'
        const dateRange = [new Date(2020, 0, 1), new Date(2021, 11, 31)];
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
        wrapper.vm.dateRange = dateRange;
        await wrapper.vm.getSalesReport(dateRange);

        wrapper.vm.groupBy = 'month';
        await wrapper.vm.getSalesReport(dateRange);

        expect(wrapper.vm.groupBy).toBe('month');
        expect(wrapper.vm.groupByOptions).toStrictEqual({
            "day": "Daily",
            "month": "Monthly",
            "week": "Weekly",
            "year": "Yearly",
        });
    });
})