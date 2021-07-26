import {shallowMount, createLocalVue, config, mount} from '@vue/test-utils';
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

  Api.getCardsBySection.mockResolvedValue(null);

  wrapper = shallowMount(Marketplace, {
    localVue,
    propsData: {},
    mocks: {$route, $log, $currentUser, $event},
    methods: {},
    stubs: {
      'marketplace-section': {
        render: jest.fn(),
        methods: {
          refreshData: jest.fn(),
        }
      }
    }
  });

});

afterEach(() => {
  wrapper.destroy();
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

describe('check-open-Full-Card-modal', () => {
    test('check-cardId-set', async () => {
        let cardId = 50
        wrapper.vm.openFullCardModal(cardId);
        expect(wrapper.vm.cardId).toBe(cardId);
    })
});

describe('check-api-request-to-delete-cards', () => {
    test('check-api-request-to-delete-cards-success', async () => {
      Api.deleteCard.mockResolvedValue({response: {status: 200}});
      await wrapper.vm.deleteSelectedCard({id: 0, section: 'ForSale'});
      expect(Api.deleteCard).toHaveBeenCalled();
    })
});