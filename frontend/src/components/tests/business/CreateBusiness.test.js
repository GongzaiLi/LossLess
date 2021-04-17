import {mount, createLocalVue, config} from '@vue/test-utils';
import { BootstrapVue } from 'bootstrap-vue';
import CreateBusiness from '../../business/CreateBusiness';
import VueRouter from 'vue-router';


let userData = {
    id:1
}
const $log = {
    debug: jest.fn(),
};


// fake the localStorage to doing the testing.
const mockUserAuthPlugin = function install(Vue) {
    Vue.mixin({
        methods: {
            $getCurrentUser: () => userData
        }
    });
}


const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(VueRouter);
localVue.use(mockUserAuthPlugin);

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
        mocks: {$log}
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
            }
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

describe('Testing api post request and the response method with errors',  () => {
    const response401 = ["error", {response: {status: 401}, message: "Access token is missing or invalid"}];
    const responseNot401 = ["error", {response: {status: 500}, message: "Network Error"}];
    const responseNoError = ["success", {response: {status: 201}, message: "Business Registered"}];


    it('current user exists, should return success after api call', async () => {

        const mockEvent = {preventDefault: jest.fn()}
        const result = await wrapper.vm.createBusiness(mockEvent);
        expect(result).toBe("success");
    });

    it('current user doesnt exist, should return failed after api call', async () => {
        userData = null;
        const mockEvent = {preventDefault: jest.fn()}
        const result = await wrapper.vm.createBusiness(mockEvent);
        expect(result).toBe("failed");
    });

    it('should push correct errors if 401 error exists in api response', async () => {
        const pushedErrors = wrapper.vm.pushErrors(response401);
        expect(pushedErrors[0]).toBe(response401[1].message);
    });

    it('should push correct errors if non-401 error exists in api response', async () => {
        const pushedErrors = wrapper.vm.pushErrors(responseNot401);
        expect(pushedErrors[0]).toBe(responseNot401[1].message);
    });

    it('should return empty list if no errors exist in api response', async () => {
        const pushedErrors = wrapper.vm.pushErrors(responseNoError);
        expect(pushedErrors[0]).toBe(undefined);
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
        expect(wrapper.findAll("b").at(0).text()).toEqual(name);
    });

    test('Description label renders', () => {
        const description = "Description";
        expect(wrapper.findAll("b").at(1).text()).toEqual(description);
    });

    test('Business Address label renders', () => {
        const address = "Business Address *";
        expect(wrapper.findAll("b").at(2).text()).toEqual(address);
    });

    test('Business Type label renders', () => {
        const type = "Business Type *";
        expect(wrapper.findAll("b").at(3).text()).toEqual(type);
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
        expect(wrapper.find("button").text()).toEqual(buttonLabel);
    });
});
