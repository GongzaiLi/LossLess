import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import BusinessSearch from '../../components/search/BusinessSearch';
import Api from "../../Api";

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
    }
};


const response = {
    data: {
        businesses: [
            {
                id: 1,
                name: "B",
                businessType: "Accommodation and Food Services",
                description: "a",
                address: {
                    streetNumber: "3/24",
                    streetName: "Ilam Road",
                    suburb: "Upper Riccarton",
                    city: "Christchurch",
                    region: "Canterbury",
                    country: "New Zealand",
                    postcode: "90210"
                },
            }
        ],
        totalItems: 1
    }
    };

jest.mock('../../Api');

beforeEach(() => {
    Api.searchBusiness.mockResolvedValue({data: {
            results: [{id: 1}],
            totalItems: 1
        }})

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(BusinessSearch, {
        localVue,
        mocks: {$route, $log, $currentUser},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
});

describe('check-api-request-searchBusiness', () => {
    test('is-getting-called-twice-when-the-get-items-for-table-is-called',  async() => {
        Api.searchBusiness.mockResolvedValue(response);

        await wrapper.vm.searchBusinessApiRequest("B", "");
        await wrapper.vm.getItemsForTable();
        await wrapper.vm.$nextTick();

        expect(Api.searchBusiness).toHaveBeenCalledTimes(2);
    })

    test('check-api-request-get-searchBusiness-sets-wrapper-totalResults-to-the-totalItems-received', async () => {

        Api.searchBusiness.mockResolvedValue(response);
        await wrapper.vm.searchBusinessApiRequest("B", "");
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.totalResults).toBe(response.data.totalItems);
    })

  test('check-api-request-get-searchBusiness-with-type-sets-wrapper-totalResults-to-the-totalItems-received', async () => {

    Api.searchBusiness.mockResolvedValue(response);
    await wrapper.vm.searchBusinessApiRequest("B", "Accommodation and Food Services");
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.totalResults).toBe(response.data.totalItems);
  })


});

describe ("format-address", () => {
    it('normal address, no nulls',  async() => {
        let address = {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: "Upper Riccarton",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
        }
        expect(wrapper.vm.formatAddress(address)).toStrictEqual("Upper Riccarton, Christchurch, New Zealand");
    })
    it('null suburb',  async() => {
        let address = {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: null,
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
        }
        expect(wrapper.vm.formatAddress(address)).toStrictEqual("Christchurch, New Zealand");
    })
})

describe('check-formatDescription-function', () => {
    test('description-less-then-20-characters', () => {
        const description = "Chocolate Place";
        expect(wrapper.vm.formatDescription(description)).toEqual('Chocolate Place');
    });

    test('description-with-1-character', () => {
        const description = "C";
        expect(wrapper.vm.formatDescription(description)).toEqual('C');
    });

    test('description-more-than-20-characters', () => {
        const description = "We make the best chocolate in the world. Please buy some it is delicious.";
        expect(wrapper.vm.formatDescription(description)).toEqual('We make the best cho...');
    });
    test('description-with-one-space', () => {
        const description = " ";
        expect(wrapper.vm.formatDescription(description)).toEqual('');
    });
});

describe('check-formatName-function', () => {
    test('description-less-then-25-characters', () => {
        const name = "Chocolate Place";
        expect(wrapper.vm.formatName(name)).toEqual('Chocolate Place');
    });

    test('description-more-than-25-characters', () => {
        const name = "We make the best chocolate in the world. Please buy some it is delicious. This is a long name";
        expect(wrapper.vm.formatName(name)).toEqual('We make the best chocolat...');
    });
});