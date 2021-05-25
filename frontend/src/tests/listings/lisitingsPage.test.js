import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import ListingPage from "../../components/listing/ListingsPage";


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
    Api.getBusiness.mockResolvedValue({data: {"address": {"country": "New Zealand"}}});
    wrapper = shallowMount(ListingPage, {
        localVue,
        propsData: {},
        mocks: {$route, $log, $currentUser},
    });
});

afterEach(() => {
    wrapper.destroy();
});


describe('Testing api get request (get all Listing function)', () => {
    const listing = {
        data: [
            {
                "id": 57,
                "inventoryItem": {
                    "id": 101,
                    "product": {
                        "id": "WATT-420-BEANS",
                        "name": "Watties Baked Beans - 420g can",
                        "description": "Baked Beans as they should be.",
                        "manufacturer": "Heinz Wattie's Limited",
                        "recommendedRetailPrice": 2.2,
                        "created": "2021-05-25T04:58:19.046Z",
                        "images": [
                            {
                                "id": 1234,
                                "filename": "/media/images/23987192387509-123908794328.png",
                                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                            }
                        ]
                    },
                    "quantity": 4,
                    "pricePerItem": 6.5,
                    "totalPrice": 21.99,
                    "manufactured": "2021-05-25",
                    "sellBy": "2021-05-25",
                    "bestBefore": "2021-05-25",
                    "expires": "2021-05-25"
                },
                "quantity": 3,
                "price": 17.99,
                "moreInfo": "Seller may be willing to consider near offers",
                "created": "2021-07-14T11:44:00Z",
                "closes": "2021-07-21T23:59:00Z"
            }
        ]
    };


    test('', async () => {
        Api.getListings.mockResolvedValue(listing);
        await wrapper.vm.getListings();
        await wrapper.vm.$forceUpdate();

        expect(wrapper.vm.cards.length).toBe(1);
        expect(wrapper.vm.cards[0]).toBe(listing.data[0]);

    })
})