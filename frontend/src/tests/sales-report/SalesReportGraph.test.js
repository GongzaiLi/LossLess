import {config, createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue} from "bootstrap-vue";
import SalesReportGraph from "../../components/sales-report/SalesReportGraph";

config.showDeprecationWarnings = false  //to disable deprecation warnings
let wrapper;

const $log = {
    debug() {
    }
};

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

    const currency ={
        symbol: '$',
        code: 'USD',
        name: 'United States Dollar'
    };

    // This 'mocks' out the html canvas as it's not implemented by the jsdom by default
    // CALEB IS A GENIUS
    document.getElementById = () => document.createElement('canvas');

    wrapper = shallowMount(SalesReportGraph, { //shallowMount and mount has different work
        localVue,
        propsData: {
            currency,
            groupBy: 'week',
            reportData: null
        },
        mocks: {$route, $currentUser, $log},
        methods: {},
    });
});

afterEach(() => {
    wrapper.destroy();
});


describe('SalesReportGraph', () => {

    it('Refreshed graph if sales data changed', async () => {
        const reportData = [
            {
                "startDate": "2021-09-01",
                "endDate": "2021-09-30",
                "totalPurchases": 1,
                "totalValue": 1.0
            },
            {
                "startDate": "2021-10-01",
                "endDate": "2021-10-30",
                "totalPurchases": 2,
                "totalValue": 2.0
            },
        ];
        wrapper.setProps({
            reportData,
            groupBy: 'month'
        });

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.chart.data.datasets[0].data[0].label).toBe("September 2021");
        expect(wrapper.vm.chart.data.datasets[0].data[1].label).toBe("October 2021");
        expect(wrapper.vm.chart.data.datasets[0].data[0].totalPurchases).toBe(1);
        expect(wrapper.vm.chart.data.datasets[0].data[1].totalValue).toBe(2.0);
    });

    it('Displays year labels if group by year', async () => {
        const reportData = [
            {
                "startDate": "2021-01-01",
                "endDate": "2021-12-31",
                "totalPurchases": 1,
                "totalValue": 1.0
            },
        ];
        wrapper.setProps({
            reportData,
            groupBy: 'year'
        });

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.chart.data.datasets[0].data[0].label).toBe("2021");
    });

    it('Displays date labels if group by date', async () => {
        const reportData = [
            {
                "startDate": "2021-01-01",
                "endDate": "2021-12-31",
                "totalPurchases": 1,
                "totalValue": 1.0
            },
        ];
        wrapper.setProps({
            reportData,
            groupBy: 'day'
        });

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.chart.data.datasets[0].data[0].label).toBe("2021-01-01");
    });

})


describe('Label Callbacks', () => {
    let callbacks;
    beforeEach(() => {
        callbacks = wrapper.vm.chart.options.plugins.tooltip.callbacks;
    })
    const t = {
        raw: {
            "startDate": "2021-01-01",
            "endDate": "2021-12-31",
            "totalPurchases": 1,
            "totalValue": 1.0
        }
    };

    it('Displays total value then total price when bars are total value', () => {
        wrapper.vm.showTotalValue = false;

        expect(callbacks.label(t)).toBe("Total Sales: 1");
        expect(callbacks.afterLabel(t)).toBe("Total Value: $1");
    });


    it('Displays total price then total value when bars are total price', () => {
        wrapper.vm.showTotalValue = true;

        expect(callbacks.label(t)).toBe("Total Value: $1");
        expect(callbacks.afterLabel(t)).toBe("Total Sales: 1");
    });
})