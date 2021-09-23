import {config, createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue} from "bootstrap-vue";
import TopProductsReport from "../../components/sales-report/TopReport";
import Api from "../../Api";

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

  Api.getProductsReport.mockResolvedValue({data: [{
    product: {
      id: "YEET",
    },
    totalProductPurchases: 420,
    totalValue: 69,
    totalLikes: 666
  }]});

  Api.getManufacturersReport.mockResolvedValue({data: [{
      manufacturer: "The Maker",
      totalProductPurchases: 420,
      totalValue: 69,
      totalLikes: 666
    }]});

  const currency = {
    symbol: '$',
    code: 'USD',
    name: 'United States Dollar'
  };

  // This 'mocks' out the html canvas as it's not implemented by the jsdom by default
  // CALEB IS A GENIUS
  document.getElementById = () => document.createElement('canvas');

  wrapper = shallowMount(TopProductsReport, {
    localVue,
    propsData: {
      currency,
      dateRange: [new Date(), new Date()],
      isTopProducts: true
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

describe('Label Callbacks', () => {
  const t = {
    label: "YEET",
    dataIndex: 0
  };

  it('Displays quantities on top when selected quantity', () => {
    wrapper.vm.doughnutOption = "totalProductPurchases";

    expect(callbacks.label(t)).toStrictEqual([" YEET", " Total Quantity Sold: 420", " Total Value Sold: $69", " Total Likes: 666",]);
  });

  it('Displays prices on top when selected prices', () => {
    wrapper.vm.doughnutOption = "totalValue";

    expect(callbacks.label(t)).toStrictEqual([" YEET", " Total Value Sold: $69", " Total Quantity Sold: 420", " Total Likes: 666",]);
  });

  it('Displays likes on top when selected likes', () => {
    wrapper.vm.doughnutOption = "totalLikes";

    expect(callbacks.label(t)).toStrictEqual([" YEET", " Total Likes: 666", " Total Value Sold: $69", " Total Quantity Sold: 420",]);
  });
})

describe('Update Chart', () => {
  it('Displays quantities when selected quantity', () => {
    wrapper.vm.doughnutOption = "totalProductPurchases";
    wrapper.vm.updateChart();

    expect(wrapper.vm.chart.data.datasets[0].data).toStrictEqual([420]);
  });

  it('Displays prices when selected prices', () => {
    wrapper.vm.doughnutOption = "totalValue";
    wrapper.vm.updateChart();

    expect(wrapper.vm.chart.data.datasets[0].data).toStrictEqual([69]);
  });

  it('Displays likes when selected likes', () => {
    wrapper.vm.doughnutOption = "totalLikes";
    wrapper.vm.updateChart();

    expect(wrapper.vm.chart.data.datasets[0].data).toStrictEqual([666]);
  });
})

describe('test watches', () => {
  test('check-get-listings-is-called-when-current-page-updated', async () => {
    let date = new Date("2020-09-22");
    await wrapper.setProps({dateRange: [date, date]});
    await wrapper.vm.$nextTick();

    expect(Api.getProductsReport).toHaveBeenLastCalledWith(0,"2020-09-22", "2020-09-22", "quantity", "DESC");
  });
  test('check results updated when sort direction changes', async () => {
    wrapper.vm.sortDesc = false;
    await wrapper.vm.$nextTick();

    expect(Api.getProductsReport).toHaveBeenCalled();
  });

  test('check results updated when sort option changes', async () => {
    wrapper.vm.sortBy = 'totalValue';
    await wrapper.vm.$nextTick();

    expect(Api.getProductsReport).toHaveBeenCalled();
  });



})

describe('Check filter Products Report results.', () => {
  const product = {
    product: {
      name: 'product'
    },
    totalLikes: 1,
    totalProductPurchases: 1,
    totalValue: 1
  }
  it('Products Report results are more then ten', () => {
    wrapper.vm.results = new Array(12).fill(product);

    wrapper.vm.filterResults();

    expect(wrapper.vm.topTenResults.length).toBe(11);
    expect(wrapper.vm.topTenResults[10]).toStrictEqual({"product": {"name": "Other"}, "totalLikes": 2, "totalProductPurchases": 2, "totalValue": 2})
  });

  it('Manufacturer Report results are more then ten', async () => {
    await wrapper.setProps({
      isTopProducts: false,
    });
    wrapper.vm.results = new Array(12).fill(product);

    wrapper.vm.filterResults();

    expect(wrapper.vm.topTenResults.length).toBe(11);
    expect(wrapper.vm.topTenResults[10]).toStrictEqual({"manufacturer": "Other", "totalLikes": 2, "totalProductPurchases": 2, "totalValue": 2})
  });



  it('Products Report results do not have "other" when 10 results', () => {
    wrapper.vm.results = new Array(10).fill(product);
    wrapper.vm.filterResults();

    expect(wrapper.vm.topTenResults.length).toBe(10);
    expect(wrapper.vm.topTenResults[9]).toStrictEqual(product);
  });

  it('Products Report results do not have "other" when 9 results', () => {
    wrapper.vm.results = new Array(9).fill(product);
    wrapper.vm.filterResults();

    expect(wrapper.vm.topTenResults.length).toBe(9);
    expect(wrapper.vm.topTenResults[8]).toStrictEqual(product);
  });

})

describe('Check correct tab is shown based on prop', () => {

  it('Products Report prop is true so products are shown', async () => {
    let date = new Date("2020-09-22");

    await wrapper.setProps({
      isTopProducts: true,
      dateRange: [date, date]
    });
    await wrapper.vm.$nextTick();

    wrapper.vm.getFields()

    expect(Api.getProductsReport).toHaveBeenLastCalledWith(0,"2020-09-22", "2020-09-22", "quantity", "DESC");
    expect(wrapper.vm.fields[0].label).toStrictEqual('Product Code')
  });


  it('Products Report prop is false so manufacturers are shown', async () => {
    let date = new Date("2020-09-22");

    await wrapper.setProps({
      isTopProducts: false,
      dateRange: [date, date]
    });
    await wrapper.vm.$nextTick();

    wrapper.vm.getFields()

    expect(Api.getManufacturersReport).toHaveBeenLastCalledWith(0,"2020-09-22", "2020-09-22", "quantity", "DESC");
    expect(wrapper.vm.fields[0].label).toStrictEqual('Manufacturer')
  });
})