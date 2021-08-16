import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import ListingFullPage from "../../components/listing/ListingFullPage";
import VueRouter from "vue-router";


config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;
let mockListing;

jest.mock('../../Api');

beforeEach(() => {
    mockListing = {
        "id": 1,
        "businessId": 1,
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
        "business": {
            "address": {
                "streetNumber": "3/24",
                "streetName": "Ilam Road",
                "suburb": "Upper Riccarton",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            },
        },
        "usersLiked": 1,
        "price": 5.99,
        "created": "2000-07-14T11:44:00Z",
        "closes": "2021-07-21T23:59:00Z"
    };

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);
    localVue.use(VueRouter);

    Api.getUserCurrency.mockResolvedValue(
      {
        symbol: '$',
          code: 'USD',
          name: 'US Dollar'
    });
    Api.getBusiness.mockResolvedValue({data: {"address": {"country": "New Zealand"}}});
    Api.getListing.mockResolvedValue({data: mockListing});

    const router = new VueRouter();

    wrapper = shallowMount(ListingFullPage, {
        localVue,
        router
    });
});

afterEach(() => {
    wrapper.destroy();
});


describe('Testing listing data is set using params or api request', () => {

    test('Set listing data using params', async () => {
        await wrapper.vm.setListingData();
        await wrapper.vm.$forceUpdate();
        expect(wrapper.vm.listingItem).toBe(mockListing);

    })

    test('Test like text based on number of likes, with 1 like', async () => {
        await wrapper.vm.setListingData();
        await wrapper.vm.$forceUpdate();
        expect(wrapper.vm.getLikeString).toBe("user likes this listing");
    })

    test('Test like text based on number of likes, with more than 1 likes', async () => {
        mockListing.usersLiked = 2;
        await wrapper.vm.setListingData();
        await wrapper.vm.$forceUpdate();
        expect(wrapper.vm.getLikeString).toBe("users like this listing");
    })
})

describe('Listing not exists message', () => {

    test('appears when route changed to nonsense value', async () => {
        Api.getListing.mockRejectedValueOnce(new Error());
        await wrapper.vm.$router.push("/listings/10000");
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        expect(wrapper.html()).toContain("This Listing no longer exists");
    })
})