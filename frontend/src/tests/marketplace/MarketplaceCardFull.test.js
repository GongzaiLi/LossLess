import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import MarketplaceCardFull from '../../components/marketplace/MarketplaceCardFull';
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

let cardInfo;


const response = {
    data: {
        "id": 500,
        "creator": {
            "id": 100,
            "firstName": "",
            "lastName": "Smith",
            "middleName": "Hector",
            "nickname": "Jonny",
            "bio": "Likes long walks on the beach",
            "email": "johnsmith99@gmail.com",
            "dateOfBirth": "1999-04-27",
            "phoneNumber": "+64 3 555 0129",
            "homeAddress": {
                "streetNumber": "3/24",
                "streetName": "Ilam Road",
                "suburb": "Upper Riccarton",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            },
            "created": "2020-07-14T14:32:00Z",
            "role": "user",
            "suburb": "",
            "businessesAdministered": [
                {
                    "id": 100,
                    "administrators": [
                        "string"
                    ],
                    "primaryAdministratorId": 20,
                    "name": "Lumbridge General Store",
                    "description": "A one-stop shop for all your adventuring needs",
                    "address": {
                        "streetNumber": "3/24",
                        "streetName": "Ilam Road",
                        "suburb": "Upper Riccarton",
                        "city": "Christchurch",
                        "region": "Canterbury",
                        "country": "New Zealand",
                        "postcode": "90210"
                    },
                    "businessType": "Accommodation and Food Services",
                    "created": "2020-07-14T14:52:00Z"
                }
            ]
        },
        "section": "ForSale",
        "created": "2021-07-15T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "1982 Lada Samara",
        "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
        "keywords": [
            {
                "id": 600,
                "name": "Vehicle",
                "created": "2021-07-15T05:10:00Z"
            }
        ],
    }
};

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
    Api.getFullCard.mockResolvedValue(null);

    wrapper = shallowMount(MarketplaceCardFull, {
        localVue,
        propsData: {closeFullViewCardModal: ()=>{}, cardId: 500},
        mocks: {$route, $log, $currentUser},
        methods: {},
    });

});

afterEach(() => {
    wrapper.destroy();
});


describe('check-api-request-get-full-card', () => {
    test('check-api-request-get-full-card-call-sets-wrapper-marketplaceCards-to-api-response', async () => {


        Api.getFullCard.mockResolvedValue(response);

        await wrapper.vm.getCard();

        expect(wrapper.vm.fullCard).toBe(response.data);
    })
});