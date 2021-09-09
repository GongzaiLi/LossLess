import {shallowMount, createLocalVue, config} from '@vue/test-utils';

import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import Router from 'vue-router'
import Notification from "../../components/model/Notification";
let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings

let mockListing;
let notification;
let mockExpiredCards;

jest.mock('../../Api');

const $log = {
    debug: jest.fn(),
};


beforeEach(() => {
    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    notification = {type: "Purchased listing", message: 'y', subjectId: 1}

    mockListing = {
        "id": 1,
        "businessId": 1,
        "product": {
            "name": "Watties Baked Beans - 420g can",
            "primaryImage": {
                "fileName": "media/images/a76ff8d8-e2ec-4fe3-95d7-235ef8e54565.png",
                "id": 1
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
        "quantity": 5,
        "usersLiked": 1,
        "price": 5.99,
        "created": "2000-07-14T11:44:00Z",
        "closes": "2021-07-21T23:59:00Z"
    };


    Api.getPurchaseListing.mockResolvedValue({data: mockListing});
    Api.getUserCurrency.mockResolvedValue({symbol: '$', code: 'NZD'});

    wrapper = shallowMount(Notification, {
        localVue,
        propsData: {notification: notification},
        mocks: {$log},
        stubs: {},
        methods: {},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Home-page', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

describe('check-purchased-listing-notification', () => {
    const afterUpdate = {
        location: "Upper Riccarton, Christchurch, Canterbury, New Zealand 90210",
        message: "5 x Watties Baked Beans - 420g can",
        price: "$5.99 NZD",
        subjectId: 1,
        type: "Purchased listing"
    }

    test('check-if-notification-updates-with-listing-info', async () => {
        await wrapper.vm.updatePurchasedNotifications(notification)
        await wrapper.vm.$forceUpdate();
        expect(wrapper.vm.updatedNotification).toStrictEqual(afterUpdate);
    })

});

describe('Checks if API archiveNotification request is called when archiveNotification method is called', () => {
    test('archiveNotification patch request is 200', async () => {
        const response = {
            response: {status: 200}
        }
        await Api.archiveNotification.mockResolvedValue(response);

        await wrapper.vm.archiveNotification();
        expect(Api.archiveNotification).toHaveBeenCalled();
    })
});
