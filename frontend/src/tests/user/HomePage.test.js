import {shallowMount, createLocalVue, config} from '@vue/test-utils';

import { BootstrapVue } from 'bootstrap-vue';
import homePage from '../../components/user/HomePage';
import Api from "../../Api";
import Router from 'vue-router'
import MarketplaceSection from "../../components/marketplace/MarketplaceSection";

let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings

let userData = {
  id: 1,
  currentlyActingAs: null,
}

jest.mock('../../Api');

const mockUserAuthPlugin = function install(Vue) {
  Vue.mixin({
    computed: {
      $currentUser: {
        get: function () {
          return userData;
        }
      },
    }
  });
}
const $log = {
  debug: jest.fn(),
};


beforeEach(() => {
  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(mockUserAuthPlugin);
  localVue.use(Router);

  Api.getUser.mockRejectedValue(new Error(''));

  wrapper = shallowMount(homePage, {
    localVue,
    propsData: {},
    mocks: {$log},
    stubs: {},
    methods: {},
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Home-page', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});

describe('check-getUserInfo-API-function', () => {
  test('get-user-normal-data', async () => {
    const response = {
      data: [{
        id: 100,
        firstName: "John",
        lastName: "Smith",
        middleName: "Hector",
        nickname: "Jonny",
        bio: "Likes long walks on the beach",
        email: "johnsmith99@gmail.com",
        dateOfBirth: "1999-04-27",
        phoneNumber: "+64 3 555 0129",
        homeAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        created: "2020-07-14T14:32:00Z",
        role: "user",
        businessesAdministered: [
          {
            id: 100,
            administrators: [
              "string"
            ],
            primaryAdministratorId: 20,
            name: "Lumbridge General Store",
            description: "A one-stop shop for all your adventuring needs",
            address: {
              streetNumber: "3/24",
              streetName: "Ilam Road",
              city: "Christchurch",
              region: "Canterbury",
              country: "New Zealand",
              postcode: "90210"
            },
            businessType: "Accommodation and Food Services",
            created: "2020-07-14T14:52:00Z"
          }
        ]
      }]
    };
    Api.getUser.mockResolvedValue(response);
    await wrapper.vm.getUserInfo(0);
    expect(wrapper.vm.userData).toEqual(response.data);
  });
});

describe('check-api-request-get-expired-cards', () => {
  test('check-api-request-get-expired-cards-call-sets-wrapper-expiredCards-to-api-response', async () => {
    const request = 1;
    const response = {
      data: [
        {
          "id": 10,
          "creator": {
            "id": 20,
            "firstName": "Jane",
            "lastName": "Doe",
            "middleName": "Hector",
            "nickname": "Jonny",
            "bio": "Likes long walks on the beach",
            "email": "janedoe@gmail.com",
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
          ]
        }
      ]
    };
    Api.getExpiringCards.mockResolvedValue(response);
    await wrapper.vm.getUserExpiredCards(request);
    await wrapper.vm.$forceUpdate();

    expect(wrapper.vm.expiringCards.length).toBe(1);
    expect(wrapper.vm.expiringCards[0]).toBe(response.data[0]);
  })
});

describe('check-table-interaction-functionality', () => {
  test('check-shortened-description-view', async () => {
    const description = "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.";

    const shortenedDescription = MarketplaceSection.methods.shortenText(description, 20);
    expect(shortenedDescription).toEqual("Beige, suitable for...");
  })

  test('check-format-tags-view', async () => {
    const tags = ["tag1", "tag2", "tag3"];

    const tagFormat = MarketplaceSection.methods.formatTags(tags);
    expect(tagFormat).toEqual("tag1, tag2, tag3");
  })

  test('check-format-address-view', async () => {
    const address = {
      "streetNumber": "3/24",
      "streetName": "Ilam Road",
      "suburb": "Upper Riccarton",
      "city": "Christchurch",
      "region": "Canterbury",
      "country": "New Zealand",
      "postcode": "90210"
    }

    const addressFormat = MarketplaceSection.methods.formatAddress(address);
    expect(addressFormat).toEqual("3/24 Ilam Road, Upper Riccarton, Christchurch Canterbury New Zealand 90210");
  })

});

describe('check-that-expired-table-only-shows-when-necessary', () => {
  test('check-table-not-shown-with-zero-expired-cards', async () => {


    wrapper.vm.expiringCards = [];
    await wrapper.vm.$forceUpdate();

    expect(wrapper.find(MarketplaceSection).exists()).toBeFalsy();
  })

});



