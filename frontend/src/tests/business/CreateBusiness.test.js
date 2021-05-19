import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import CreateBusiness from '../../components/business/CreateBusiness';
import VueRouter from 'vue-router';
import Api from "../../Api";

jest.mock('../../Api');

let $currentUser = {
  id: 1,
  dateOfBirth: '01/01/2001',
  currentlyActingAs: null
}
const $log = {
  debug: jest.fn(),
};

config.showDeprecationWarnings = false  //to disable deprecation warnings

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(VueRouter);

config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;

const router = new VueRouter();


const name = "countdown"
const description = "Countdown is New Zealand’s leading supermarket brand, serving more than 3 million " +
  "customers every week.";
const type = "Retail Trade";
const address = "17 Chappie Place, Hornby, Christchurch 8042";


beforeEach(() => {
  wrapper = mount(CreateBusiness, {
    localVue,
    router,
    mocks: {$log, $currentUser : JSON.parse(JSON.stringify($currentUser))}
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('CreateBusiness Script Testing', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('Create button exists and gets called', async () => {
    const createBusiness = jest.fn();

    wrapper = mount(CreateBusiness, {
      localVue,
      methods: {
        createBusiness
      },
      mocks: {$log, $currentUser}
    });


    const button = wrapper.find("button");

    button.trigger("submit");
    expect(createBusiness).toHaveBeenCalledTimes(1);

    button.trigger("submit");
    expect(createBusiness).toHaveBeenCalledTimes(2);


  });

  test('getBusinessData returns correct number of attributes', async () => {
    const expectedAttributes = 4;
    expect(Object.keys(wrapper.vm.getBusinessData()).length).toBe(expectedAttributes);

  });

  test('Get business data returns correct values', () => {


    wrapper.vm.name = name;
    wrapper.vm.description = description;
    wrapper.vm.businessType = type;
    wrapper.vm.address = address;

    expect(wrapper.vm.getBusinessData().name).toBe(name);
    expect(wrapper.vm.getBusinessData().description).toBe(description);
    expect(wrapper.vm.getBusinessData().businessType).toBe(type);
    expect(wrapper.vm.getBusinessData().address).toBe(address);
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

  test('Create button renders', () => {
    const buttonLabel = "Create";
    expect(wrapper.findAll("button").at(1).text()).toEqual(buttonLabel);
  });


  test('Cant create if too young', async () => {
    wrapper.vm.$currentUser.dateOfBirth = '01/01/2015';

    await wrapper.vm.$nextTick();
    expect(wrapper.vm.canCreateBusiness).toBeFalsy();
  });

  test('Cant create if acting as business', async () => {
    wrapper.vm.$currentUser.currentlyActingAs = {'balh' : 'blah'};

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.canCreateBusiness).toBeFalsy();
    expect(wrapper.find("#create-business-locked-card").exists()).toBeTruthy();
  });
});
