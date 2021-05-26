import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import AddListingCard from "../../components/listing/AddListingCard";


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

jest.mock('../../Api');

beforeEach(() => {

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(AddListingCard, {
        localVue,
        propsData: {
            setUpListingPage: () => {
            },
        },
        mocks: {$route, $log},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('testing create listing', () => {

    beforeEach(()=> {
        wrapper.vm.selectedInventoryItem = {expires: "3000-12-12", product: {id: 1}};
        Api.createListing.mockResolvedValue({response: {status: 201}, data: {listingId: 0}});
    })

    test('quantity is 0 Error message in listing card error', () => {
        wrapper.vm.listingData.quantity = 0;
        wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("Quantity must be more than zero");
    })
    test('closes date is null, date is set to expiry date', () => {
        wrapper.vm.listingData.closes = null;
        wrapper.vm.createListing();
        expect(wrapper.vm.listingData.closes).toBe( wrapper.vm.selectedInventoryItem.expires);
    })
    test('closes time is null time set to 00:00:00', () => {
        wrapper.vm.listingData.closesTime = null;
        wrapper.vm.createListing();
        expect(wrapper.vm.listingData.closesTime).toBe("00:00:00");

    })
    test('closes is in past Error message in listing card error', () => {
        wrapper.vm.listingData.closes = wrapper.vm.getToday();
        wrapper.vm.listingData.closesTime = "00:00:00";
        wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("Listing must close in the future, Check the time of closure");
    })
})


describe('Testing api post request (Create a new Listing function)', () => {

    beforeEach(()=> {
        wrapper.vm.selectedInventoryItem = {expires: "3000-12-12", product: {id: 1}};
    })

    it('Successfully creates a Listing ', async () => {
        Api.createListing.mockResolvedValue({response: {status: 201}, data: {listingId: 0}});
        await wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("");
    });

    it('400 given Inventory ID does not exist', async () => {
        Api.createListing.mockRejectedValue({
            response: {status: 400, data: "Inventory with given id does not exist"},
        });
        await wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("Inventory with given id does not exist");
    });

    it('403 forbidden test when create Listing', async () => {
        Api.createListing.mockRejectedValue({
            response: {status: 403, data: "Forbidden. You are not an authorized administrator"},
        });
        await wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("Forbidden. You are not an authorized administrator");
    });

    it('No internet test when create Listing', async () => {
        Api.createListing.mockRejectedValue({
            request: {path: 'blah'},
        });
        await wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("No Internet Connectivity");
    });

    it('500 server error when create Listing', async () => {
        Api.createListing.mockRejectedValue({
            response: {status: 500},
        });
        await wrapper.vm.createListing();
        expect(wrapper.vm.listingCardError).toBe("Server error");
    });
})

describe('Check correct Date and times are returned', () => {
    test("getToday returns the date of today", () => {
        let date = new Date();
        let today = date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');
        expect(wrapper.vm.getToday()).toBe(today);
    });
    test("getTimeNow returns current time", () => {
        let date = new Date();
        let now = date.getHours() + ":" + date.getMinutes();
        expect(wrapper.vm.getTimeNow()).toBe(now);
    })
})


