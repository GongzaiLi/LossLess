import {config, createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue} from "bootstrap-vue";
import Api from "../../Api";
import TopManufacturersReport from "../../components/sales-report/TopManufacturersReport";

config.showDeprecationWarnings = false  //to disable deprecation warnings
let wrapper;
let callbacks;

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

beforeEach(async () => {
    const localVue = createLocalVue()
    localVue.use(BootstrapVue);

    Api.getManufacturersReport.mockResolvedValue({
        data: [{
            manufacturer: "Oliver's Emporium",
            totalProductPurchases: 123,
            totalValue: 50,
            totalLikes: 910
        }]
    });

    const currency = {
        symbol: '$',
        code: 'USD',
        name: 'United States Dollar'
    };

    // This 'mocks' out the html canvas as it's not implemented by the jsdom by default
    // CALEB IS A GENIUS
    document.getElementById = () => document.createElement('canvas');

    wrapper = shallowMount(TopManufacturersReport, {
        localVue,
        propsData: {
            currency,
            dateRange: [new Date(), new Date()]
        },
        mocks: {$route, $currentUser, $log},
        methods: {},
    });

    // Mounted is async so we really do need all these nextTicks to wait for the mounted to finish
    // Yeah this is really dumb, if you have a better solution let us know
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();

    callbacks = wrapper.vm.chart.options.plugins.tooltip.callbacks;
});

afterEach(() => {
    wrapper.destroy();
});

describe('Label Callbacks Testing', () => {
    const t = {
        label: "LabelName",
        dataIndex: 0
    };

    it('Displays quantities on top when selected quantity', () => {
        wrapper.vm.doughnutOption = "totalProductPurchases";

        expect(callbacks.label(t)).toStrictEqual([" LabelName", " Total Quantity Sold: 123", " Total Value Sold: $50", " Total Likes: 910",]);
    });

    it('Displays prices on top when selected prices', () => {
        wrapper.vm.doughnutOption = "totalValue";

        expect(callbacks.label(t)).toStrictEqual([" LabelName", " Total Value Sold: $50", " Total Quantity Sold: 123", " Total Likes: 910",]);
    });

    it('Displays likes on top when selected likes', () => {
        wrapper.vm.doughnutOption = "totalLikes";

        expect(callbacks.label(t)).toStrictEqual([" LabelName", " Total Likes: 910", " Total Value Sold: $50", " Total Quantity Sold: 123",]);
    });
})

describe('Update Chart value checking', () => {
    it('Displays quantities when selected quantity', () => {
        wrapper.vm.doughnutOption = "totalProductPurchases";
        wrapper.vm.updateChart();

        expect(wrapper.vm.chart.data.datasets[0].data).toStrictEqual([123]);
    });

    it('Displays prices when selected prices', () => {
        wrapper.vm.doughnutOption = "totalValue";
        wrapper.vm.updateChart();

        expect(wrapper.vm.chart.data.datasets[0].data).toStrictEqual([50]);
    });

    it('Displays likes when selected likes', () => {
        wrapper.vm.doughnutOption = "totalLikes";
        wrapper.vm.updateChart();

        expect(wrapper.vm.chart.data.datasets[0].data).toStrictEqual([910]);
    });
})

describe('Check correct graph option for each sort.', () => {

    it('Displays quantities when sorted by quantity', () => {
        wrapper.vm.sortBy = "totalProductPurchases";
        wrapper.vm.getManufacturersReport(wrapper.vm.dateRange);

        expect(wrapper.vm.doughnutOption).toStrictEqual("totalProductPurchases");
    });

    it('Displays values when sorted by values', () => {
        wrapper.vm.sortBy = "totalValue";
        wrapper.vm.getManufacturersReport(wrapper.vm.dateRange);

        expect(wrapper.vm.doughnutOption).toStrictEqual("totalValue");
    });

    it('Displays likes when sorted by likes', () => {
        wrapper.vm.sortBy = "totalLikes";
        wrapper.vm.getManufacturersReport(wrapper.vm.dateRange);

        expect(wrapper.vm.doughnutOption).toStrictEqual("totalLikes");
    });
})