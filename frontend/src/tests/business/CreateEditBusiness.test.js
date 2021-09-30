import {mount, createLocalVue, config, shallowMount} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import CreateEditBusiness from '../../components/business/CreateEditBusiness';
import VueRouter from 'vue-router';
import Api from "../../Api";

jest.mock('../../Api');
jest.mock("../../../public/business-profile-default.jpeg", () => {
}) // mock image

let $currentUser = {
  id: 1,
  dateOfBirth: '01/01/2001',
  currentlyActingAs: null
}

const $log = {
  debug: jest.fn(),
};

const businessDetails = {
  name: "Wonka Water",
  description: "Water yum water",
  type: "Retail trade",
  address: {
    streetNumber: 481,
    streetName: "Schroeders Avenue",
    city: "Fairview",
    region: "Nevada",
    country: "Sierra Leone",
    postcode: 4740
  }
};

config.showDeprecationWarnings = false  //to disable deprecation warnings

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(VueRouter);

config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;

const router = new VueRouter();

beforeEach(() => {
  wrapper = mount(CreateEditBusiness, {
    localVue,
    router,
    props: {
      value: {}
    },
    mocks: {$log, $currentUser: JSON.parse(JSON.stringify($currentUser))}
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('CreateEditBusiness Script Testing', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('Submit button exists, and not edit business then createBusiness method is called', async () => {
    const createBusiness = jest.fn();
    const updateBusiness = jest.fn();

    wrapper = mount(CreateEditBusiness, {
      localVue,
      propsData: {
        isEditBusiness: false
      },
      methods: {
        createBusiness,
        updateBusiness
      },
      mocks: {$log, $currentUser}
    });

    const button = wrapper.find("#submit-button");

    button.trigger("submit");
    expect(createBusiness).toHaveBeenCalledTimes(1);

    button.trigger("submit");
    expect(createBusiness).toHaveBeenCalledTimes(2);
  });

  test('Submit button exists, and is edit business then updateBusiness method is called', async () => {
    const createBusiness = jest.fn();
    const updateBusiness = jest.fn();

    wrapper = mount(CreateEditBusiness, {
      localVue,
      propsData: {
        isEditBusiness: true, businessDetails: businessDetails
      },
      methods: {
        createBusiness,
        updateBusiness
      },
      mocks: {$log, $currentUser}
    });

    const button = wrapper.find("#submit-button");

    button.trigger("submit");
    expect(updateBusiness).toHaveBeenCalledTimes(1);

    button.trigger("submit");
    expect(updateBusiness).toHaveBeenCalledTimes(2);
  });
});

describe('Testing-beforeMount-when-is-edit-business-modal', () => {
  let wrapperNew;

  beforeEach(() => {
    wrapperNew = shallowMount(CreateEditBusiness, {
      localVue,
      router,
      propsData: {isEditBusiness: true, businessDetails: businessDetails},
      mocks: {$log, $currentUser: JSON.parse(JSON.stringify($currentUser))}
    });
  })

  afterEach(() => {
    wrapperNew.destroy();
  });

  test('Testing-beforeMount-when-is-edit-business-modal-businessData-is-set-up',  () => {
    expect(wrapperNew.vm.businessData).toStrictEqual(businessDetails);
  });
});

describe('Testing api post request and the response method with errors', () => {
  const response401 = {response: {status: 401}, message: "Access token is missing or invalid"};
  const responseNot401 = {response: {status: 500}, message: "Network Error"};

  const mockUser = {
    "id": 100,
    "firstName": "John",
    "lastName": "Smith",
    "email": "johnsmith99@gmail.com",
    "dateOfBirth": "1999-04-27",
    "homeAddress": {
      "streetNumber": "3/24",
      "streetName": "Ilam Road",
      "suburb": "a suburb",
      "city": "Christchurch",
      "region": "Canterbury",
      "country": "New Zealand",
      "postcode": "90210"
    },
    "role": "user",
    "businessesAdministered": []
  };

  it('blue sky scenario', async () => {

    const mockEvent = {preventDefault: jest.fn()}
    Api.postBusiness.mockResolvedValue({
      data: {
        businessId: 100,
      }
    });
    Api.getUser.mockResolvedValue({data: mockUser})

    wrapper.vm.$router.push = jest.fn();

    await wrapper.vm.createBusiness(mockEvent);
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.$currentUser).toBe(mockUser);
    expect(wrapper.vm.$router.push).toHaveBeenCalledWith({'path': `/businesses/100`})
    expect(wrapper.vm.errors).toStrictEqual([]);
  });

  it('current user doesnt exist, should return failed after api call', async () => {
    const mockEvent = {preventDefault: jest.fn()}
    Api.postBusiness.mockResolvedValue({
      data: {
        businessId: 100,
      }
    });
    Api.getUser.mockRejectedValue(response401);
    await wrapper.vm.createBusiness(mockEvent);
    expect(wrapper.vm.errors).toStrictEqual(['Access token is missing or invalid']);
  });


  it('create product failed, should return failed after api call', async () => {
    const mockEvent = {preventDefault: jest.fn()}
    Api.postBusiness.mockRejectedValue(responseNot401);
    Api.getUser.mockResolvedValue({data: mockUser});
    await wrapper.vm.createBusiness(mockEvent);
    expect(wrapper.vm.errors).toStrictEqual(['Network Error']);
  });

});

describe('Testing-api-put-business', () => {

  it('Successful-update-business', async () => {
    Api.modifyBusiness.mockResolvedValue({
      status: 200
    });

    await wrapper.vm.updateBusiness();
    expect(wrapper.vm.errors).toStrictEqual([]);
  });

  it('400-error-update-user-testing', async () => {
    Api.modifyBusiness.mockRejectedValue({
      response: {
        data: {message: "Bad request"},
        status: 400
      }
    });
    await wrapper.vm.updateBusiness();
    expect(wrapper.vm.errors).toStrictEqual(["Updating business failed: Bad request"]);
  });

  it('500-error-update-user-testing', async () => {
    Api.modifyBusiness.mockRejectedValue({});
    await wrapper.vm.updateBusiness();
    expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
  });
});



// HTML TESTING ----------------------------------------------------------------
describe('CreateBusiness HTML testing', () => {
  test('Page heading renders', () => {
    const headingExpected = "Create a Business";
    expect(wrapper.find('h1').text()).toEqual(headingExpected);
  });

  test('Business Name label renders', () => {
    const name = "Business name *";
    expect(wrapper.findAll("strong").at(0).text()).toEqual(name);
  });

  test('Description label renders', () => {
    const description = "Description";
    expect(wrapper.findAll("strong").at(1).text()).toEqual(description);
  });

  test('Business Address label renders', () => {
    const address = "Business Address *";
    expect(wrapper.findAll("strong").at(2).text()).toEqual(address);
  });

  test('Business Type label renders', () => {
    const type = "Business Type *";
    expect(wrapper.findAll("strong").at(9).text()).toEqual(type);
  });

  test('business types in drop down has default value Choose ...', () => {
    const noOption = "Choose ...";
    expect(wrapper.findAll("select option").at(0).text()).toEqual(noOption);
  });

  test('business types in drop down includes Accommodation and Food Services', () => {
    const option1 = "Accommodation and Food Services";
    expect(wrapper.findAll("select option").at(1).text()).toEqual(option1);
  });

  test('business types in drop down includes  Retail Trade', () => {
    const option2 = "Retail Trade";
    expect(wrapper.findAll("select option").at(2).text()).toEqual(option2);
  });

  test('business types in drop down includes Charitable organisation', () => {
    const option3 = "Charitable organisation";
    expect(wrapper.findAll("select option").at(3).text()).toEqual(option3);
  });

  test('business types in drop down includes Non-profit organisation', () => {
    const option4 = "Non-profit organisation";
    expect(wrapper.findAll("select option").at(4).text()).toEqual(option4);
  });

  test('Cancel button renders', () => {
    const buttonLabel = "Cancel";
    expect(wrapper.findAll("button").at(1).text()).toEqual(buttonLabel);
  });


  test('Cant create if too young', async () => {
    wrapper.vm.$currentUser.dateOfBirth = '01/01/2015';

    await wrapper.vm.$nextTick();
    expect(wrapper.vm.canCreateBusiness).toBeFalsy();
  });

  test('Cant create if acting as business', async () => {
    wrapper.vm.$currentUser.currentlyActingAs = {'balh': 'blah'};

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.canCreateBusiness).toBeFalsy();
    expect(wrapper.find("#create-business-locked-card").exists()).toBeTruthy();
  });
});