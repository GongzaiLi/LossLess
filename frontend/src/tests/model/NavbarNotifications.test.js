import {createLocalVue, mount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import NotificationDropdown from '../../components/model/NotificationDropdown'; // name of your Vue component
import Auth from '../../auth'
import Api from '../../Api'
import EventBus from "../../util/event-bus";

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
  {
    id: 4,
    message: "Some message - 420g can",
    subjectId: 1,
    type: "Some type",
    read: true
  },
  {
    id: 5,
    message: "5 x Something else - 123g can",
    price: "$123.99 NZD",
    subjectId: 1,
    type: "Some type",
    read: false
  },

  {
    id: 6,
    message: "A notification about Pink collars maybe - 69g can",
    subjectId: 1,
    type: "Some type",
    read: false
  }
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
  Api.getNotifications.mockResolvedValue({data: notifications});
  Api.patchNotification.mockResolvedValue({data: ""});

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
    expect(wrapper.vm.numberOfNotifications).toBe(4);
  })


  describe('Get expiring cards', () => {
    test('cards expiring within 24 hours get added to notifications', async () => {
      await wrapper.vm.updateNotifications();
      await wrapper.vm.$nextTick();
      expect(wrapper.vm.numberOfNotifications).toBe(4);
    })
  });

});


describe('Get unread notifications', () => {

  test('Unread notifications from the list of all the notifications', async () => {
    await wrapper.vm.updateNotifications();
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.numberOfNotifications).toBe(4);
  })

});

