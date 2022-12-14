import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import ListingPage from "../../components/listing/BusinessListingsPage";


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

const strippedListings = {
    data: {
        totalItems: 3,
        listings: [
            {
                "id": 1,
                "inventoryItem": {
                    "id": 101,
                    "product": {
                        "name": "Watties Baked Beans - 420g can",
                        "primaryImage": {
                            "fileName": "media/images/a76ff8d8-e2ec-4fe3-95d7-235ef8e54565.png",
                            "id": 1
                        },
                        "images": [
                            {
                                "id": 1234,
                                "filename": "/media/images/23987192387509-123908794328.png",
                                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                            }
                        ]
                    },
                },
                "price": 5.99,
                "created": "2000-07-14T11:44:00Z",
                "closes": "2021-07-21T23:59:00Z"
            },
            {
                "id": 2,
                "inventoryItem": {
                    "id": 102,
                    "product": {
                        "name": "Baked Beans - 420g can",
                        "primaryImage": {
                            "fileName": "media/images/a76ff8d8-e2ec-4fe3-95d7-235ef8e54565.png",
                            "id": 1
                        },
                        "images": [
                            {
                                "id": 1234,
                                "filename": "/media/images/23987192387509-123908794328.png",
                                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                            }
                        ]
                    },
                },
                "price": 10,
                "created": "2021-07-14T11:44:00Z",
                "closes": "2021-07-21T23:59:00Z"
            },
            {
                "id": 3,
                "inventoryItem": {
                    "id": 103,
                    "product": {
                        "name": "Turnips",
                        "primaryImage": {
                            "fileName": "media/images/a76ff8d8-e2ec-4fe3-95d7-235ef8e54565.png",
                            "id": 1
                        },
                        "images": [
                            {
                                "id": 1234,
                                "filename": "/media/images/23987192387509-123908794328.png",
                                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                            }
                        ]
                    },
                },
                "price": 999,
                "created": "2021-08-14T11:44:00Z",
                "closes": "2022-01-21T23:59:00Z"
            }
        ]
    }
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
        data: {
            totalItems: 1,
            listings: [
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
                            ],
                            "primaryImage": {
                                "fileName": "media/images/a76ff8d8-e2ec-4fe3-95d7-235ef8e54565.png",
                                "id": 1
                            }
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
        }
    };


    test('', async () => {
        Api.getListings.mockResolvedValue(listing);
        await wrapper.vm.getListings();
        await wrapper.vm.$forceUpdate();

        expect(wrapper.vm.cards.length).toBe(1);
        expect(wrapper.vm.cards[0]).toBe(listing.data.listings[0]);

    })
})
