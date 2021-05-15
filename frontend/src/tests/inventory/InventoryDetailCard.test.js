import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import InventoryDetailCard from "../../components/inventory/InventoryDetailCard";
import Api from "../../Api";

config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;

const $route = {
  params: {
    id: 0
  }
};

const $log = {
  debug() {
  }
};

const $currentUser = {
  role: 'user',
  currentlyActingAs: {
    id: 0
  },
  businessesAdministered: [
    {id: 0, name: "blah"},
    {id: 1, name: "blah1"},
    {id: 2, name: "blah2"}
  ]
};

jest.mock('../../Api');

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(InventoryDetailCard, {
    localVue,
    propsData: {
      inventory: {
        product: {
          id: "WATT-420-BEANS",
          name: "Watties Baked Beans - 420g can",
          description: "Baked Beans as they should be.",
          recommendedRetailPrice: 2.2,
          created: "2021-04-14T13:01:58.660Z"
        },
        quantity: 4,
        pricePerItem: 6.5,
        totalPrice: 21.99,
        manufactured: "2021-05-14",
        sellBy: "2021-05-14",
        bestBefore: "2021-05-14",
        expires: "2021-05-14"
      },
      setUpInventoryPage: () => {},
    },
    mocks: {$route, $log, $currentUser},
    stubs: {},
    methods: {},
  });


});

afterEach(() => {
  wrapper.destroy();
});

describe('check setDate function', () => {

  test('check setDate function with normal date', () => {
    const date = '2021-05-13T00:32:00Z';
    expect(wrapper.vm.setDate(date)).toStrictEqual('Thu, 13 May 2021');
  })
})


describe('check calculateTotalPrice function', () => {

  test('check calculateTotalPrice function with 0 price Per Item and 0 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 0;
    wrapper.vm.inventoryInfo.quantity = 0;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  });

  test('check calculateTotalPrice function with 1 price Per Item and 0 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 1;
    wrapper.vm.inventoryInfo.quantity = 0;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  })

  test('check calculateTotalPrice function with 0 price Per Item and 1 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 0;
    wrapper.vm.inventoryInfo.quantity = 1;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  })

  test('check calculateTotalPrice function with 10 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(100);
  })

  test('check calculateTotalPrice function with 10.01 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10.01;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(100.1);
  })

  test('check calculateTotalPrice function with 10.21 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10.21;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(102.1);
  })

})

describe('Testing api put/post request and the response method with errors', () => {


  it('Succesfully creates a product ', async () => {
    Api.createInventory.mockResolvedValue({response: {status: 200}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.createInventory(mockEvent);


    expect(wrapper.vm.inventoryCardError).toBe("");
  });

  it("400 given Product ID doesn't exist", async () => {
    Api.createInventory.mockRejectedValue({response: {status: 400, data: "Product with given id does not exist"}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.createInventory(mockEvent);

    expect(wrapper.vm.inventoryCardError).toBe("Product with given id does not exist");
  });

  it('403 forbidden test', async () => {
    Api.createInventory.mockRejectedValue({response: {status: 403}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.createInventory(mockEvent);

    expect(wrapper.vm.inventoryCardError).toBe("Forbidden. You are not an authorized administrator");
  });

  it('No internet test', async () => {
    Api.createInventory.mockRejectedValue({request: {path: 'blah'}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.createInventory(mockEvent);

    expect(wrapper.vm.inventoryCardError).toBe("No Internet Connectivity");
  });

  it('500 server error', async () => {
    Api.createInventory.mockRejectedValue({response: {status: 500}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.createInventory(mockEvent);

    expect(wrapper.vm.inventoryCardError).toBe("Server error");
  });
})
