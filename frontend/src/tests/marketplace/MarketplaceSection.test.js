import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import MarketplaceSection from '../../components/marketplace/MarketplaceSection';
import Api from '../../Api'

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
    Api.getCardsBySection.mockResolvedValue({data: {
        results: [{id: 1}],
        totalItems: 1
    }})

    cardInfo = [{
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
    }];

    const localVue = createLocalVue()
    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(MarketplaceSection, {
        localVue,
        propsData: {section: "ForSale", perPage: 10},
        mocks: {$route, $log, $currentUser},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
});

describe ("shorten-text", () => {
    it('shorten-text-length_less_than_limit',  async() => {
        let text = "text"

        expect(wrapper.vm.shortenText(text, 5)).toStrictEqual(text)
    })

    it('shorten-text-length_equals_limit',  async() => {
        let text = "text"

        expect(wrapper.vm.shortenText(text, 4)).toStrictEqual(text)
    })

    it('shorten-text-length_greater_than_limit',  async() => {
        let text = "text"

        expect(wrapper.vm.shortenText(text, 2)).toStrictEqual("te...")
    })

    it('shorten-text-empty_string',  async() => {
        let text = ""

        expect(wrapper.vm.shortenText(text, 5)).toStrictEqual("")
    })

    it('shorten-text-slice length = 0',  async() => {
        let text = "text"
        expect(wrapper.vm.shortenText(text, 0)).toStrictEqual("...")
    })

    it('shorten-text-slice length = 0 and empty string',  async() => {
        let text = ""
        expect(wrapper.vm.shortenText(text, 0)).toStrictEqual("")
    })

    it('shorten-text-text ends with .',  async() => {
        let text = "text."
        expect(wrapper.vm.shortenText(text, 4)).toStrictEqual("text...")
    })
})

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
        expect(wrapper.vm.formatAddress(address)).toStrictEqual("Upper Riccarton, Christchurch");
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
        expect(wrapper.vm.formatAddress(address)).toStrictEqual("Christchurch");
    })
})

describe ("Page Change handler", () => {
    it('causes page refresh when page changed to 2',  async() => {
        Api.getCardsBySection.mockResolvedValue({data: {
            results: [{id: 1}],
            totalItems: 11
        }});
        await wrapper.vm.pageChanged(2);

        expect(Api.getCardsBySection).toHaveBeenCalledWith("ForSale", 1, 10, "created", "asc");
        expect(wrapper.vm.totalItems).toBe(11);
    })
})