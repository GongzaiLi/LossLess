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

describe ("Page Change handler", () => {
    it('causes page refresh when page changed to 2',  async() => {
        Api.getCardsBySection.mockResolvedValue({data: {
            results: [{id: 1}],
            totalItems: 11
        }});
        await wrapper.vm.pageChanged(2);

        expect(Api.getCardsBySection).toHaveBeenCalledWith("ForSale", 1, 10, "created", "desc");
        expect(wrapper.vm.totalItems).toBe(11);
    })
})

describe ("Sort Change handler", () => {
    Api.getCardsBySection.mockResolvedValue({data: {
            results: [{id: 1}],
            totalItems: 11
    }});

    it('causes page refresh with correct sorting details when sorting ascending',  async() => {
        await wrapper.vm.sortingChanged({
            sortBy: 'title',
            sortDesc: false
        });

        expect(Api.getCardsBySection).toHaveBeenLastCalledWith("ForSale", 0, 10, "title", "asc");
    });

    it('causes page refresh with correct sorting details when sorting descending',  async() => {
        await wrapper.vm.sortingChanged({
            sortBy: 'location',
            sortDesc: true
        });

        expect(Api.getCardsBySection).toHaveBeenLastCalledWith("ForSale", 0, 10, "location", "desc");
    });
})

describe('check-open-Full-Card-modal', () => {
    test('check-cardId-set', async () => {
        let card = {
            id: 50
        }
        wrapper.vm.cardClickHandler(card);
        expect(wrapper.vm.cardId).toBe(card.id);
    })
});
