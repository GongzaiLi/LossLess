import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import CreateCard from "../../components/marketplace/CreateCard";
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
  id: 1,
  lastName: "sadsad",
  firstName: "firstName"
};
const createCardForm = {
  creatorId: $currentUser.id,
  section: '',
  title: '',
  description: "",
  keywords: [],
}

const showError = "This is an error message"


const $event = {
  creatorId: 1,
  section: 'ForSale',
  title: '1982 Lada Samara',
  description: "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
  keywords: ["car", "vehicle"],
}

jest.mock('../../Api');

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  Api.getUser.mockResolvedValue({data: {firstName: "John",lastName: "Smith", homeAddress:{suburb: "Riccarton", city: "Christchurch"}}});
  wrapper = mount(CreateCard, {
    localVue,
    propsData: {showError: showError},
    mocks: {$route, $log, $event, createCardForm, $currentUser},
    methods: {},
  });

});

afterEach(() => {
  wrapper.destroy();
});

describe('check-create-card-form-user-entered-data', () => {
  test('check-the-form-user-entered-data-is-correct', () => {

    expect(wrapper.vm.createCardForm).toStrictEqual(createCardForm);
  })

});

describe('check-create-card-form-autofill-data', () => {

  test('check-the-form-autofill-data-is-correct', () => {
    const cardInfo = {fullName: "John Smith", location: "Riccarton, Christchurch"};
    expect(wrapper.vm.cardInfo.fullName).toStrictEqual(cardInfo.fullName);
    expect(wrapper.vm.cardInfo.location).toStrictEqual(cardInfo.location);
  })

});

describe('check-create_card-function', () => {
  test('when-create-card-button-pressed', async () => {

    wrapper.vm.createCardForm = createCardForm;
    wrapper.vm.createAction();
    await wrapper.vm.$forceUpdate();

    const [[emitted]] = wrapper.emitted().createAction;
    expect(emitted).toStrictEqual(createCardForm);
  });
});


