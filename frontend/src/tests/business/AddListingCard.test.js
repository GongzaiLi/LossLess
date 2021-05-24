import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import AddListingCard from "../../components/business/AddListingCard";


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


describe('Testing api post request (Create a new Listing function)', () => {


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
