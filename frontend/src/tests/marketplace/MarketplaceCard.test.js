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
    },
    businessesAdministered: [
        {id: 0, name: "blah"},
        {id: 1, name: "blah1"},
        {id: 2, name: "blah2"}
    ]
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
        keywords: []
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

describe ("format-tag", () => {
    it('check-format-tag-joins-a-list',  async() => {
        cardInfo.keywords = ["key1", "key2", "key3"]
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.formatTags).toStrictEqual("key1, key2, key3")
    })
})