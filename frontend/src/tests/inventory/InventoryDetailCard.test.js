import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import InventoryDetailCard from "../../components/inventory/InventoryDetailCard";
import Api from "../../Api";

config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;
let manufacturedDates;


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
  manufacturedDates = {manufactured: "2000-05-22", sellBy: "3021-05-22", bestBefore: "3021-05-22", expires: "3021-05-22"};
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

  test('check calculateTotalPrice function with -10.21 price Per Item and 10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = -10.21;
    wrapper.vm.inventoryInfo.quantity = 10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
  })

  test('check calculateTotalPrice function with 10 price Per Item and -10 quantity', () => {
    wrapper.vm.inventoryInfo.pricePerItem = 10;
    wrapper.vm.inventoryInfo.quantity = -10;
    wrapper.vm.calculateTotalPrice();
    expect(wrapper.vm.inventoryInfo.totalPrice).toBe(0);
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


describe('Editing products', () => {


  it('Succesfully edits a product ', async () => {
    Api.modifyInventory.mockResolvedValue({response: {status: 200}});

    wrapper.vm.$bvModal.hide = jest.fn();

    await wrapper.vm.editInventory();

    expect(wrapper.vm.inventoryCardError).toBe("");
    expect(wrapper.vm.$bvModal.hide).toHaveBeenCalled();
  });

  it("Displays errors if exist", async () => {
    Api.modifyInventory.mockRejectedValue({response: {status: 400, data: "Product with given id does not exist"}});

    await wrapper.vm.editInventory();

    expect(wrapper.vm.inventoryCardError).toBe("Product with given id does not exist");
  });
})


describe('Editing products', () => {

  it('Succesfully edits a product ', async () => {
    Api.modifyInventory.mockResolvedValue({response: {status: 200}});

    wrapper.vm.$bvModal.hide = jest.fn();

    await wrapper.vm.editInventory();

    expect(wrapper.vm.inventoryCardError).toBe("");
    expect(wrapper.vm.$bvModal.hide).toHaveBeenCalled();
  });

  it("Displays errors if exist", async () => {
    Api.modifyInventory.mockRejectedValue({response: {status: 400, data: "Product with given id does not exist"}});

    await wrapper.vm.editInventory();

    expect(wrapper.vm.inventoryCardError).toBe("Product with given id does not exist");
  });
})


describe('select-product-modal', () => {
  test('selectProduct works', () => {
    wrapper.vm.selectProduct({id: 'ABC'});

    expect(wrapper.vm.inventoryInfo.productId).toBe('ABC')
  });
})

describe('manufactured date_validation', () => {
  test('manufactured invalid for future', () => {
    manufacturedDates.manufactured = "3000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(false);
    expect(wrapper.vm.inventoryCardError).toBe("Manufactured date must be in the Past or Today");
  });
  test('manufactured valid for today', () => {
    manufacturedDates.manufactured = new Date().toJSON().slice(0, 10);
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });

  test('manufactured valid for past', () => {
    manufacturedDates.manufactured = "2000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });

})

describe('sell by date_validation', () => {
  test('sell by valid for future', () => {
    manufacturedDates.sellBy = "3000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });
  test('sell by valid for today', () => {
    manufacturedDates.sellBy = new Date().toJSON().slice(0, 10);
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });

  test('sell by invalid for past', () => {
    manufacturedDates.sellBy = "2000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(false);
    expect(wrapper.vm.inventoryCardError).toBe("Sell by date must be in the future");
  });

})

describe('expires date_validation', () => {
  test('expires valid for future', () => {
    manufacturedDates.expires = "3000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });
  test('expires valid for today', () => {
    manufacturedDates.expires = new Date().toJSON().slice(0, 10);
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });

  test('expires invalid for past', () => {
    manufacturedDates.expires = "2000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(false);
    expect(wrapper.vm.inventoryCardError).toBe("Expiry date must be in the future");
  });

})

describe('best before date_validation', () => {
  test('best before valid for future', () => {
    manufacturedDates.bestBefore = "3000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });
  test('best before valid for today', () => {
    manufacturedDates.bestBefore = new Date().toJSON().slice(0, 10);
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(true);
    expect(wrapper.vm.inventoryCardError).toBe("");
  });

  test('best before valid for past', () => {
    manufacturedDates.bestBefore = "2000-05-22";
    expect(wrapper.vm.validInventoryDates(manufacturedDates)).toBe(false);
    expect(wrapper.vm.inventoryCardError).toBe("Best before date must be in the future");
  });

})