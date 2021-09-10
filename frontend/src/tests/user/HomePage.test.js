import {shallowMount, createLocalVue, config} from '@vue/test-utils';

import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import homePage from '../../components/user/HomePage';
import Api from "../../Api";
import Router from 'vue-router'
import MarketplaceSection from "../../components/marketplace/MarketplaceSection";

let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings


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
