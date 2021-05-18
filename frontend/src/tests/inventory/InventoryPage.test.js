import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import InventoryPage from '../../components/inventory/InventoryPage';
import Api from "../../Api";
import InventoryDetailCard from "../../components/inventory/InventoryDetailCard";

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
    Api.getUserCurrency.mockResolvedValue({
        symbol: '$',
        code: 'USD',
        name: 'United States Dollar'
    });

    wrapper = shallowMount(InventoryPage, {
        localVue,
        propsData: {},
        mocks: {$route, $log, $currentUser},
        stubs: {'another-component': true},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
});

describe('check-getBusiness-API-function', () => {
    test('get-normal-data', async () => {
        const productsResponse = {
            data: [{
                id: "WATT-420-BEANS",
                name: "Watties Baked Beans - 420g can",
                description: "Baked Beans as they should be.",
                recommendedRetailPrice: 2.2,
                created: "2021-04-14T13:01:58.660Z"
            }]
        };
        const inventoryResponse = {
            data: [{
                product: {
                    id: "WATT-420-BEANS",
                    name: "Watties Baked Beans - 420g can",
                    description: "Baked Beans as they should be.",
                    recommendedRetailPrice: 2.2,
                    created: "2021-04-14T13:01:58.660Z"
                },
                quantity: 4,
                pricePerItem: 6.5,
                totalPrice: 21.99,
                manufactured: "2021-05-14",
                sellBy: "2021-05-14",
                bestBefore: "2021-05-14",
                expires: "2021-05-14"
            }]
        }
        const businessResponse = {
            data: {
                "id": 100,
                "primaryAdministratorId": 20,
                "name": "Lumbridge General Store",
                "description": "A one-stop shop for all your adventuring needs",
                "address": {
                    "streetNumber": "3/24",
                    "streetName": "Ilam Road",
                    "city": "Christchurch",
                    "region": "Canterbury",
                    "country": "New Zealand",
                    "postcode": "90210"
                },
                "businessType": "Accommodation and Food Services",
                "created": "2020-07-14T14:52:00Z"
            }
        };

        const mockCurrencyData = {
            symbol: '$',
            code: 'NZD',
            name: 'New Zealand Dollar'
        };

        Api.getProducts.mockResolvedValue(productsResponse);
        Api.getBusiness.mockResolvedValue(businessResponse);
        Api.getInventory.mockResolvedValue(inventoryResponse)

        const userCurrencyMock = jest.fn();
        userCurrencyMock.mockResolvedValue(mockCurrencyData);
        Api.getUserCurrency = userCurrencyMock;

        await wrapper.vm.getBusinessInfo(0);
        expect(wrapper.vm.business.name).toEqual(businessResponse.data.name);
        expect(wrapper.vm.currency).toEqual(mockCurrencyData);
        expect(userCurrencyMock).toHaveBeenCalledWith('New Zealand');
    });
});

describe('check-setDate-function', () => {
    test('set-month-less-then-10-data', () => {
        const date = "2021-04-14T13:01:58.660Z";
        expect(wrapper.vm.setDate(date)).toEqual('14/04/2021');
    });

    test('set-month-more-then-9-data', () => {
        const date = "2021-10-14T13:01:58.660Z";
        expect(wrapper.vm.setDate(date)).toEqual('14/10/2021');
    });

    test('set-day-more-then-9-data', () => {
        const date = "2021-10-10T13:01:58.660Z";
        expect(wrapper.vm.setDate(date)).toEqual('10/10/2021');
    });

    test('set-day-less-then-10-data', () => {
        const date = "2021-10-02T13:01:58.660Z";
        expect(wrapper.vm.setDate(date)).toEqual('02/10/2021');
    });
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


describe('Testing currently acting as watcher ', () => {

    it('Does not load data if switch to normal user', async () => {
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', null);

        jest.spyOn(wrapper.vm, 'getBusinessInfo');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.getBusinessInfo).not.toBeCalled();
    });

    it('Does not load data if switch to other business', async () => {
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 1});

        jest.spyOn(wrapper.vm, 'getBusinessInfo');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.getBusinessInfo).not.toBeCalled();
    });

    it('Loads if switch to other business and acting as', async () => {
        wrapper.vm.$route = {
            params: {
                id: 2
            }
        }
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 2});

        jest.spyOn(wrapper.vm, 'getBusinessInfo');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.getBusinessInfo).toHaveBeenCalledWith(2);
    });


    it('Loads data if switch to other business but is admin', async () => {
        wrapper.vm.$set(wrapper.vm.$currentUser, 'role', 'globalApplicationAdmin');
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 1});

        jest.spyOn(wrapper.vm, 'getBusinessInfo');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.getBusinessInfo).toHaveBeenCalledWith(0);
    });

});

describe('check-modal-inventory-card-page', () => {
    test('check-inventory-detail-card-component-exists', async () => {
        const inventoryResponse = {
            data: [{
                product: {
                    id: "WATT-420-BEANS",
                    name: "Watties Baked Beans - 420g can",
                    description: "Baked Beans as they should be.",
                    recommendedRetailPrice: 2.2,
                    created: "2021-04-14T13:01:58.660Z"
                },
                quantity: 4,
                pricePerItem: 6.5,
                totalPrice: 21.99,
                manufactured: "2021-05-14",
                sellBy: "2021-05-14",
                bestBefore: "2021-05-14",
                expires: "2021-05-14"
            }]
        };
        Api.getProducts.mockResolvedValue(inventoryResponse);
        await wrapper.vm.getBusinessInfo(0);
        await wrapper.vm.openInventoryDetailModal(inventoryResponse.data[0]);

        await wrapper.vm.$forceUpdate();

        expect(wrapper.find(InventoryDetailCard).exists()).toBeTruthy()
    });

    test('check-inventory-detail-card-component-exists-when-click-create-button', async () => {
        await wrapper.vm.openCreateInventoryModal();

        await wrapper.vm.$forceUpdate();

        expect(wrapper.find(InventoryDetailCard).exists()).toBeTruthy()
    });

})

