import {createLocalVue, mount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import NotificationDropdown from '../../components/model/NotificationDropdown'; // name of your Vue component
import Auth from '../../auth'
import Api from '../../Api'

let wrapper;

let $route;

let $router;

let userData = {
  "id": 100,
  "firstName": "John",
  "lastName": "Smith",
  "middleName": "Hector",
  "nickname": "Jonny",
  "bio": "Likes long walks on the beach",
  "email": "johnsmith99@gmail.com",
  "dateOfBirth": "1999-04-27",
  "phoneNumber": "+64 3 555 0129",
  currentlyActingAs: null,
  "homeAddress": {
    "streetNumber": "3/24",
    "streetName": "Ilam Road",
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
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
      },
      "businessType": "Accommodation and Food Services",
      "created": "2020-07-14T14:52:00Z"
    }
  ]
}

let $currentUser = userData;

const date = new Date();
const currentDateWithin24 = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'T'+(date.getHours()+2)+':'+date.getMinutes()+':'+date.getUTCSeconds()+'Z'


const expiringCards = [
  {
    id: 2,
    title: 'card 2 that expires within next 24 hours',
    displayPeriodEnd: currentDateWithin24
  },
  {
    id: 3,
    title: 'card 2 that expires within next 24 hours',
    displayPeriodEnd: currentDateWithin24
  }
]
const notifications = [
  {"id":40,"userId":1,"type":"Expired Marketplace Card","message":"Your card: Chocolate Mixer has expired","subjectId":42},
  {"id":41,"userId":1,"type":"Expired Marketplace Card","message":"Your card: Bean Roaster has expired","subjectId":43},
  {"id":42,"userId":1,"type":"Expired Marketplace Card","message":"Your card: Candy Drop Machine has expired","subjectId":44},
  {"id":43,"userId":1,"type":"Liked Listing","message":"You have liked listing: Dark Chocolate Bar. This listing closes at 2022-03-27","subjectId":1},
  {"id":44,"userId":1,"type":"Unliked Listing","message":"You have unliked listing: Dark Chocolate Bar","subjectId":1}
]

const $log = {
  debug() {
  }
};

jest.mock('../../Api');

beforeEach(() => {
  $route = {
    name: "users",
    params: {
      id: 0
    },
    query: {}
  };

  $router = {
    push: jest.fn(),
    replace: jest.fn(),
    go: jest.fn(),
  }

  const localVue = createLocalVue();

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(Auth);

  Api.getExpiredCards.mockResolvedValue({data: expiringCards});
  Api.clearHasCardsExpired.mockResolvedValue({response: {status: 200}});
  Api.getNotifications.mockResolvedValue({data: []});
  Api.readNotification.mockResolvedValue({data: ""});

  wrapper = mount(NotificationDropdown, {
    localVue,
    propsData: {},
    mocks: {$route, $router, $currentUser, $log, displayPeriodEnd: { split: 'asd'  }},
    stubs: ['router-link'],
    methods: {},
    computed: {},
  })
});

afterEach(() => {
  wrapper.destroy();
});

describe('NotificationDropdown', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});


describe('Get expiring cards', () => {
  test('cards expiring within 24 hours get added to notifications', async () => {
    await wrapper.vm.updateNotifications();
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.numberOfNotifications).toBe(2);
  })

  describe('Route based on clicked notification', () => {

    test('Routes to correct address', async () => {
      await wrapper.vm.notificationClicked(notifications[3])
      //await wrapper.vm.$nextTick();
      expect($router.push).toHaveBeenCalledWith('/listings/1');
    })

    test('Routes to correct address', async () => {
      await wrapper.vm.notificationClicked(notifications[1])
      //await wrapper.vm.$nextTick();
      expect($router.push).toHaveBeenCalledTimes(0);
    })
  });

  describe('Get expiring cards', () => {
    test('cards expiring within 24 hours get added to notifications', async () => {
      await wrapper.vm.updateNotifications();
      await wrapper.vm.$nextTick();
      expect(wrapper.vm.numberOfNotifications).toBe(2);
    })
  });

});
