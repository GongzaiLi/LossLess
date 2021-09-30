import {config, createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
import Api from "../../Api";
import SalesReportPage from "../../components/sales-report/SalesReportPage";

config.showDeprecationWarnings = false  //to disable deprecation warnings
let wrapper;

const $log = {
    debug() {
    }
};

const $route = {
    params: {
        id: 0
    }
};

const $currentUser = {
    role: 'user',
    currentlyActingAs: {
        id: 0
    },
    businessesAdministered: [
        {id: 0, name: "blah"},
    ]
};

// This 'mocks' out the html canvas as it's not implemented by the jsdom by default
// CALEB IS A GENIUS
document.getElementById = () => document.createElement('canvas');

jest.mock('../../Api');

beforeEach(() => {

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    Api.getBusiness.mockResolvedValue({data: {"address": {"country": "New Zealand"}}});
    Api.getUserCurrency.mockResolvedValue({
        symbol: '$',
        code: 'USD',
        name: 'United States Dollar'
    });

    wrapper = shallowMount(SalesReportPage, { //shallowMount and mount has different work
        localVue,
        propsData: {},
        mocks: {$route, $currentUser, $log},
        stubs: {'another-component': true},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
});


describe('businessNameIfAdminOfThisBusiness', () => {

    it('Works if user admins business', async () => {
        expect(wrapper.vm.businessNameIfAdminOfThisBusiness).toBe("blah");
    });

    it('Works if user not admins business', async () => {
        wrapper.vm.$route = {
            params: {
                id: 3
            }
        }

        expect(wrapper.vm.businessNameIfAdminOfThisBusiness).toBe(null);
    });

})