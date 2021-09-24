import {config, createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue} from "bootstrap-vue";
import ListingsDurationsGraph from "../../components/sales-report/ListingsDurationsGraph";
import Api from "../../Api";

config.showDeprecationWarnings = false  //to disable deprecation warnings
let wrapper;

const $route = {
  params: {
    id: 0
  }
};

jest.mock('../../Api');

beforeEach(() => {
  const localVue = createLocalVue()
  localVue.use(BootstrapVue);

  // This 'mocks' out the html canvas as it's not implemented by the jsdom by default
  // CALEB IS A GENIUS
  document.getElementById = () => document.createElement('canvas');

  const data = {
    0: 123,
    1: 3257,
    3: 240
  };

  Api.getListingDurations.mockResolvedValue({data});

  wrapper = shallowMount(ListingsDurationsGraph, {
    localVue,
    mocks: {$route},
    methods: {},
    propsData: {
      dateRange: [new Date(), new Date()]
    }
  });
});

afterEach(() => {
  wrapper.destroy();
});


describe('ListingsDurationsGraph', () => {

  it('Has offset data when mounted', async () => {
    expect(wrapper.vm.chart.data.datasets[0].data)
      .toStrictEqual({
        0.5: 123,
        1.5: 3257,
        3.5: 240
      });
  });
})


describe('Label Callbacks', () => {
  let callbacks;
  beforeEach(() => {
    callbacks = wrapper.vm.chart.options.plugins.tooltip.callbacks;
  })

  it('Displays x at 0 when x is 0.5', () => {
    const t = {
      parsed: {
        x: 0.5,
        y: 420
      }
    };
    wrapper.vm.showTotalValue = false;

    expect(callbacks.title([t])).toBe(" Sold 0 - 1 days before closing");
    expect(callbacks.label(t)).toBe(" 420 listings");
  });


  it('Displays x at 4 when x is 3.5', () => {
    const t = {
      parsed: {
        x: 3.5,
        y: 69
      }
    };
    wrapper.vm.showTotalValue = false;

    expect(callbacks.title([t])).toBe(" Sold 3 - 4 days before closing");
    expect(callbacks.label(t)).toBe(" 69 listings");
  });
})