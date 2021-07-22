import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import MarketplaceCard from '../../components/marketplace/MarketplaceCard';

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

let cardInfo;

jest.mock('../../Api');

beforeEach(() => {
    cardInfo = {
        creator: {
            homeAddress: {
                streetNumber: "3/24",
                    streetName: "Ilam Road",
                    suburb: "Upper Riccarton",
                    city: "Christchurch",
                    region: "Canterbury",
                    country: "New Zealand",
                    postcode: "90210"
            },
        },
        keywords: [],
        created: "",
    };

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(MarketplaceCard, {
        localVue,
        propsData: {cardInfo},
        mocks: {$route, $log, $currentUser},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
});

describe ("format-address", () => {
    it('normal address, no nulls',  async() => {

        expect(wrapper.vm.formatAddress).toStrictEqual("Upper Riccarton, Christchurch");
    })
    it('null suburb',  async() => {
        cardInfo.creator.homeAddress = {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            suburb: null,
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
        }
        expect(wrapper.vm.formatAddress).toStrictEqual("Christchurch");
    })
})

describe ("format-created-date", () => {
    it('check-format-created-date',  async() => {
        cardInfo.created = "2021-07-21";
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.formatCreated).toStrictEqual("Wed, 21 Jul 2021")
    })
})