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
        await wrapper.vm.openInventoryDetailModal(inventoryResponse.data[0]);
        await wrapper.vm.setUpInventoryPage(0);
        await wrapper.vm.$forceUpdate();

        expect(wrapper.find(InventoryDetailCard).exists()).toBeTruthy()
    });

    test('check-inventory-detail-card-component-exists-when-click-create-button', async () => {
        await wrapper.vm.openCreateInventoryModal();

        await wrapper.vm.$forceUpdate();

        expect(wrapper.find(InventoryDetailCard).exists()).toBeTruthy()
    });

})

describe('Testing currently acting as watcher ', () => {

    it('Does not load data if switch to normal user', async () => {
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', null);

        jest.spyOn(wrapper.vm, 'setUpInventoryPage');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.setUpInventoryPage).not.toBeCalled();
    });

    it('Does not load data if switch to other business', async () => {
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 1});

        jest.spyOn(wrapper.vm, 'setUpInventoryPage');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.setUpInventoryPage).not.toBeCalled();
    });

    it('Loads if switch to other business and acting as', async () => {
        wrapper.vm.$route = {
            params: {
                id: 2
            }
        }
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 2});

        jest.spyOn(wrapper.vm, 'setUpInventoryPage');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.setUpInventoryPage).toHaveBeenCalledWith(2);
    });


    it('Loads data if switch to other business but is admin', async () => {
        wrapper.vm.$set(wrapper.vm.$currentUser, 'role', 'globalApplicationAdmin');
        wrapper.vm.$set(wrapper.vm.$currentUser, 'currentlyActingAs', {id: 1});

        jest.spyOn(wrapper.vm, 'setUpInventoryPage');
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.setUpInventoryPage).toHaveBeenCalledWith(0);
    });

});

