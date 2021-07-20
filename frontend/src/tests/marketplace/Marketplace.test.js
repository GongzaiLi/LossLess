import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Marketplace from '../../components/marketplace/Marketplace';
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

const $event = {
  creatorId: 1,
  section: 'ForSale',
  title: '1982 Lada Samara',
  description: "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
  keywords: [ "car", "vehicle"],
}


jest.mock('../../Api');

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = mount(Marketplace, {
    localVue,
    propsData: {},
    mocks: {$route, $log, $currentUser, $event},
    methods: {},
  });

});

afterEach(() => {
  wrapper.destroy();
});

describe('check-api-request-get-all-cards-by-section', () => {
  test('check-api-request-get-all-cards-by-section-call-sets-wrapper-marketplaceCards-to-api-response', async () => {
    const request = "Wanted";
    const response = {
      data: [
        {
          "id": 500,
          "creator": {
            "id": 100,
            "firstName": "John",
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

    Api.getCardsBySection.mockResolvedValue(response);

    await wrapper.vm.getCardsFromSection(request);

    expect(wrapper.vm.marketplaceCards).toBe(response.data);
  })
});

describe('test-api-request-post-card', () => {
  it('should return response with status code 201 and card id', async function () {
    const response = {
      data: {"cardId": 1},
      response: {status: 201}
    }
    Api.createCard.mockResolvedValue(response);
    await wrapper.vm.createCard($event);
    expect(Api.createCard).toHaveBeenCalled();
    expect(wrapper.vm.error).toBe('');

  });
  it('should return response with status code 400 and error mesaage', async function () {
    const response = {
      response: {status: 400, data: {
        message: "Bad request"
        }}
    }
    Api.createCard.mockRejectedValue(response);
    await wrapper.vm.createCard($event);
    await wrapper.vm.$forceUpdate();
    expect(Api.createCard).toHaveBeenCalled();

    expect(wrapper.vm.error).toBe('message: Bad request\n');

  });

  it('should return response with status code 403 and error mesaage', async function () {
    const response = {
      response: {status: 403}
    }
    Api.createCard.mockRejectedValue(response);
    await wrapper.vm.createCard($event);
    await wrapper.vm.$forceUpdate();
    expect(Api.createCard).toHaveBeenCalled();
    expect(wrapper.vm.error).toBe("Forbidden. You are not an authorized administrator");

  });
})