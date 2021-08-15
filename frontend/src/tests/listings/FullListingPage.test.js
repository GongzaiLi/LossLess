import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import ListingFullPage from "../../components/listing/ListingFullPage";

config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;

const $route = {
    params: {
        id: 0
    }
};

const $router = {
    push: jest.fn(),
}

const $log = {
    debug() {
    }
};

const $currentUser = {
    role: 'user',
    currentlyActingAs: {
        id: 0
    },
};


jest.mock('../../Api');

beforeEach(() => {

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);
    Api.getUserCurrency.mockResolvedValue(
        {
            symbol: '$',
            code: 'USD',
            name: 'US Dollar'
        });
    Api.getBusiness.mockResolvedValue({data: {"address": {"country": "New Zealand"}}});
    wrapper = shallowMount(ListingFullPage, {
        localVue,
        propsData: {},
        mocks: {$route, $log, $currentUser, $router},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Purchase Button', () => {
    Api.purchaseListing.mockResolvedValue();

    test('the purchase button function calls the api request', async () => {
        await wrapper.vm.purchaseListingRequest();
        expect(Api.purchaseListing).toHaveBeenCalled();
    });

    test('a successful purchase routes to the home page', async () => {
        await wrapper.vm.purchaseListingRequest();
        expect($router.push).toHaveBeenCalled();

    })
});

