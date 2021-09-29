import {shallowMount, createLocalVue, config} from '@vue/test-utils';

import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import homePage from '../../components/user/HomePage';
import Api from "../../Api";
import Router from 'vue-router'
import MarketplaceSection from "../../components/marketplace/MarketplaceSection";


config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;

let userData = {
    id: 1,
    currentlyActingAs: null,
}

jest.mock('../../Api');

const mockUserAuthPlugin = function install(Vue) {
    Vue.mixin({
        computed: {
            $currentUser: {
                get: function () {
                    return userData;
                }
            },
        }
    });
}
const $log = {
    debug: jest.fn(),
};

let response = {
    data: [{
        id: 100,
        firstName: "John",
        lastName: "Smith",
        middleName: "Hector",
        nickname: "Jonny",
        bio: "Likes long walks on the beach",
        email: "johnsmith99@gmail.com",
        dateOfBirth: "1999-04-27",
        phoneNumber: "+64 3 555 0129",
        homeAddress: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
        },
        created: "2020-07-14T14:32:00Z",
        role: "user",
        businessesAdministered: [
            {
                id: 100,
                administrators: [
                    "string"
                ],
                primaryAdministratorId: 20,
                name: "Lumbridge General Store",
                description: "A one-stop shop for all your adventuring needs",
                address: {
                    streetNumber: "3/24",
                    streetName: "Ilam Road",
                    city: "Christchurch",
                    region: "Canterbury",
                    country: "New Zealand",
                    postcode: "90210"
                },
                businessType: "Accommodation and Food Services",
                created: "2020-07-14T14:52:00Z"
            }
        ]
    }]
};

beforeEach(() => {
    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);
    localVue.use(mockUserAuthPlugin);
    localVue.use(Router);


    Api.getUser.mockResolvedValue(response);
    Api.getNotifications.mockResolvedValue({data: []});
    Api.getExpiredCards.mockResolvedValue({data: []});
    Api.deleteNotification = jest.fn();

    wrapper = shallowMount(homePage, {
        localVue,
        propsData: {},
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

describe('check-getUserInfo-API-function', () => {
    test('get-user-normal-data', async () => {
        await wrapper.vm.getUserInfo(0);
        expect(wrapper.vm.userData).toEqual(response.data);
    });
});


describe('check-that-expired-table-only-shows-when-necessary', () => {
    test('check-table-not-shown-with-zero-expired-cards', async () => {


        wrapper.vm.hasExpiredCards = false;
        await wrapper.vm.$forceUpdate();

        expect(wrapper.find(MarketplaceSection).exists()).toBeFalsy();
    })

    test('check-table-shown-with-many-expired-cards', async () => {


        wrapper.vm.hasExpiredCards = true;
        await wrapper.vm.$forceUpdate();

        expect(wrapper.find(MarketplaceSection).exists()).toBeTruthy();
    })

});

describe('check-filtered-notifications', () => {
    beforeEach(() => {
        Api.getNotifications.mockResolvedValue({data: [
                {
                    id: 1,
                    message: "HSDFSD",
                    subjectId: 2,
                    type: "Business Currency Changed"
                },
                {
                    id: 2,
                    message: "HSDFSD",
                    subjectId: 1,
                    type: "User Currency Changed"
                },
                {
                    id: 3,
                    message: "HSDFSD",
                    subjectId: 69,
                    type: "Listing Liked"
                },
                {
                    id: 4,
                    message: "HSDFSD",
                    subjectId: 420,
                    type: "Business Currency Changed"
                }
            ]});
    });

    test('Only shows user notifications when acting as user', async () => {
        wrapper.vm.$currentUser.currentlyActingAs = null;
        await wrapper.vm.updateNotifications();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.filteredNotifications.length).toBe(2);
        expect(wrapper.vm.filteredNotifications[0].id).toBe(2);
        expect(wrapper.vm.filteredNotifications[1].id).toBe(3);
    });

    test('Only shows business notifications of business acting as', async () => {
        wrapper.vm.$currentUser.currentlyActingAs = {
            id: 2,
            name: "Big Dave's Collars"
        };
        await wrapper.vm.updateNotifications();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.filteredNotifications.length).toBe(1);
        expect(wrapper.vm.filteredNotifications[0].id).toBe(1);
    });

    test('Shows different business notifications when change business acting as', async () => {
        wrapper.vm.$currentUser.currentlyActingAs = {
            id: 420,
            name: "Big Bob's Collars"
        };

        await wrapper.vm.updateNotifications();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.filteredNotifications.length).toBe(1);
        expect(wrapper.vm.filteredNotifications[0].id).toBe(4);
    });
});

describe('check-that-filterNotificationsByTag-sends-correct-api-request', () => {
    test('no-tags-selected-expect-call-with-empty-string', async () => {

        wrapper.vm.tagColors = {
            RED: false,
            ORANGE: false,
            YELLOW: false,
            GREEN: false,
            BLUE: false,
            PURPLE: false,
            BLACK: false,
        };

        await wrapper.vm.filterNotificationsByTag();
        expect(Api.getNotifications).toBeCalledWith("")
    });

    test('all-tags-selected-expect-call-with-all-tags', async () => {

        wrapper.vm.tagColors = {
            RED: true,
            ORANGE: true,
            YELLOW: true,
            GREEN: true,
            BLUE: true,
            PURPLE: true,
            BLACK: true,
        };

        await wrapper.vm.filterNotificationsByTag();
        expect(Api.getNotifications).toBeCalledWith("RED,ORANGE,YELLOW,GREEN,BLUE,PURPLE,BLACK")
    });

    test('some-tags-selected-expect-call-with-those-tags-only', async () => {

        wrapper.vm.tagColors = {
            RED: false,
            ORANGE: true,
            YELLOW: true,
            GREEN: false,
            BLUE: true,
            PURPLE: false,
            BLACK: true,
        };

        await wrapper.vm.filterNotificationsByTag();
        expect(Api.getNotifications).toBeCalledWith("ORANGE,YELLOW,BLUE,BLACK")
    });

});

describe('check-deleted-functionality', () => {
    test('if-no-pendingDeletedNotification-then-pendingDeletedNotification-is-not-deleted', async () => {
        wrapper.vm.pendingDeletedNotification = []
        await wrapper.vm.createDeleteToast(1);
        expect(Api.deleteNotification).toBeCalledTimes(0);
    });
});

describe('check-router-guard-functionality', () => {
    test('if-route-is-changed-while-no-pending-notifications-notification-is-not-deleted', async () => {
        wrapper.vm.pendingDeletedNotifications = []
        await wrapper.vm.$options.beforeRouteLeave[0].call(wrapper.vm, "toObj", "fromObj", jest.fn());
        expect(Api.deleteNotification).toBeCalledTimes(0);
    });
});


describe('test-toggleTagColorSelected-works-correctly', () => {
    test('toggle-red-tag-from-true-to-false', async () => {

        wrapper.vm.tagColors = {
            RED: true,
            ORANGE: false,
            YELLOW: false,
            GREEN: false,
            BLUE: false,
            PURPLE: false,
            BLACK: false,
        };

        await wrapper.vm.toggleTagColorSelected("RED");
        expect(wrapper.vm.tagColors.RED).toStrictEqual(false);
    });

    test('toggle-red-tag-from-false-to-true', async () => {

        wrapper.vm.tagColors = {
            RED: false,
            ORANGE: false,
            YELLOW: false,
            GREEN: false,
            BLUE: false,
            PURPLE: false,
            BLACK: false,
        };

        await wrapper.vm.toggleTagColorSelected("RED");
        expect(wrapper.vm.tagColors.RED).toStrictEqual(true);
    });

    test('toggle-black-and-blue-tags-from-false-to-true', async () => {

        wrapper.vm.tagColors = {
            RED: false,
            ORANGE: false,
            YELLOW: false,
            GREEN: false,
            BLUE: false,
            PURPLE: false,
            BLACK: false,
        };

        await wrapper.vm.toggleTagColorSelected("BLACK");
        await wrapper.vm.toggleTagColorSelected("BLUE");
        expect(wrapper.vm.tagColors.BLACK).toStrictEqual(true);
        expect(wrapper.vm.tagColors.BLUE).toStrictEqual(true);
    });

    test('toggle-black-and-blue-tags-from-true-to-false', async () => {

        wrapper.vm.tagColors = {
            RED: false,
            ORANGE: false,
            YELLOW: false,
            GREEN: false,
            BLUE: true,
            PURPLE: false,
            BLACK: true,
        };

        await wrapper.vm.toggleTagColorSelected("BLACK");
        await wrapper.vm.toggleTagColorSelected("BLUE");
        expect(wrapper.vm.tagColors.BLACK).toStrictEqual(false);
        expect(wrapper.vm.tagColors.BLUE).toStrictEqual(false);
    });
})

describe('test-toggle-archived-notifications', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })
    test('toggle-to-archived-notifications', async () => {
        wrapper.vm.isArchivedSelected = false;
        await wrapper.vm.toggleArchived();
        expect(Api.getNotifications).toHaveBeenCalledWith(null, true);
    });
    test('toggle-to-un-archived-notifications', async () => {
        wrapper.vm.isArchivedSelected = true;
        await wrapper.vm.toggleArchived();
        expect(Api.getNotifications).toHaveBeenCalledWith(null, false);
    });
})

describe('Test notification clicked', () => {

    const collarNotification = {
        id: 6,
        message: "A notification about Pink collars maybe - 69g can",
        subjectId: 1,
        type: "Some type",
        read: false
    }

    test('Notification set to read on click', async () => {
        await wrapper.vm.notificationClicked(collarNotification);
        expect(collarNotification.read).toBeTruthy();

    })

});

