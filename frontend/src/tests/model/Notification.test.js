import {shallowMount, createLocalVue, config} from '@vue/test-utils';

import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import Notification from "../../components/model/Notification";
let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings

let mockListing;
let notification;

let $route;
let $router;

jest.mock('../../Api');

const $log = {
    debug: jest.fn(),
};


beforeEach(() => {
    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    notification = {type: "Purchased listing", message: 'y', subjectId: 1}
    $route = {
        name: "users",
        params: {
            id: 0
        },
        query: {}
    };

    $router = {
        push: jest.fn(),
        replace: jest.fn(),
        go: jest.fn(),
    }
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
    Api.patchNotification = jest.fn()

    wrapper = shallowMount(Notification, {
        localVue,
        propsData: {notification: notification},
        mocks: {$log, $router, $route},
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
        location: "Upper Riccarton, Christchurch, Canterbury, New Zealand",
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
describe('check starring api call is correct', async () => {

    test('check api call sets starred to true if currently false', async () =>  {
        wrapper.vm.updatedNotification.starred = false;
        wrapper.vm.updatedNotification.id = 1;
        await wrapper.vm.$forceUpdate();
        wrapper.vm.starNotification();
        expect(Api.patchNotification).toBeCalledWith(1,{"starred": true})
    });

    test('check api call sets starred to false if currently true', async () =>  {
        wrapper.vm.updatedNotification.starred = true;
        wrapper.vm.updatedNotification.id = 1;
        await wrapper.vm.$forceUpdate();
        wrapper.vm.starNotification();
        expect(Api.patchNotification).toBeCalledWith(1,{"starred": false})
    });
});

describe('Route based on clicked notification', () => {

    test('Routes to correct address', async () => {
        wrapper.vm.updatedNotification.subjectId = 1;
        wrapper.vm.updatedNotification.type = "Liked Listing";
        await wrapper.vm.goToListing()
        expect($router.push).toHaveBeenCalledWith('/listings/1');
    })

    test('Routes to correct address', async () => {
        wrapper.vm.updatedNotification.subjectId = 1;
        wrapper.vm.updatedNotification.type = "Expired Marketplace Card";
        await wrapper.vm.goToListing()
        expect($router.push).toHaveBeenCalledTimes(0);
    })
});
