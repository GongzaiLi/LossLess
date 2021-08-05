import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import MarketplaceSection from '../../components/search/BusinessSearch';

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


jest.mock('../../Api');

beforeEach(() => {

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(MarketplaceSection, {
        localVue,
        mocks: {$route, $log, $currentUser},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
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