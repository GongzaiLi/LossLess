import {mount, createLocalVue, config} from '@vue/test-utils';
import { BootstrapVue } from 'bootstrap-vue';
import CreateBusiness from '../business/CreateBusiness';

const localVue = createLocalVue();
localVue.use(BootstrapVue);



config.showDeprecationWarnings = false  //to disable deprecation warnings


let wrapper;
let createBusiness;
// let getBusinessData;




beforeEach(() => {
    createBusiness = jest.fn();


    wrapper = mount(CreateBusiness, {
        localVue,
        data() {
            return {
                name: "",
                description: "",
                address: "",
                businessType: "",
            }
        }, methods: {
            createBusiness,
        },
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('CreateBusiness', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

describe('CreateBusiness', () => {
    test('Create button exists and gets called', async () => {


        const button = wrapper.find("button");

        button.trigger("submit");

        expect(createBusiness).toHaveBeenCalledTimes(1);


    });
});

describe('CreateBusiness', () => {
    test('getBusinessData returns correct number of attributes', async () => {
        const expectedAttributes = 4;
        expect(Object.keys(wrapper.vm.getBusinessData()).length).toBe(expectedAttributes);

    });
});


describe('CreateBusiness', () => {
    test('Get business data returns correct values', async () => {

        const name = "countdown"
        const description = "Countdown is New Zealand’s leading supermarket brand, serving more than 3 million " +
            "customers every week.";
        const type = "Retail Trade";
        const address = "17 Chappie Place, Hornby, Christchurch 8042";
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













// HTML TESTING ----------------------------------------------------------------
describe('CreateBusiness', () => {
    test('Page heading renders', () => {
        const headingExpected = "Create a Business";
        expect(wrapper.find('h1').text()).toEqual(headingExpected);
    });
});


describe('CreateBusiness', () => {
    test('Business Name label renders', () => {
        const name = "Business name *";
        expect(wrapper.findAll("b").at(0).text()).toEqual(name);
    });
});

describe('CreateBusiness', () => {
    test('Description label renders', () => {
        const description = "Description";
        expect(wrapper.findAll("b").at(1).text()).toEqual(description);
    });
});

describe('CreateBusiness', () => {
    test('Business Address label renders', () => {
        const address = "Business Address *";
        expect(wrapper.findAll("b").at(2).text()).toEqual(address);
    });
});

describe('CreateBusiness', () => {
    test('Business Type label renders', () => {
        const type = "Business Type *";
        expect(wrapper.findAll("b").at(3).text()).toEqual(type);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down has default value Choose ...', () => {
        const noOption = "Choose ...";
        expect(wrapper.findAll("select option").at(0).text()).toEqual(noOption);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes Accommodation and Food Services', () => {
        const option1 = "Accommodation and Food Services";
        expect(wrapper.findAll("select option").at(1).text()).toEqual(option1);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes  Retail Trade', () => {
        const option2 = "Retail Trade";
        expect(wrapper.findAll("select option").at(2).text()).toEqual(option2);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes Charitable organisation', () => {
        const option3 = "Charitable organisation";
        expect(wrapper.findAll("select option").at(3).text()).toEqual(option3);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes Non-profit organisation', () => {
        const option4 = "Non-profit organisation";
        expect(wrapper.findAll("select option").at(4).text()).toEqual(option4);
    });
});


describe('CreateBusiness', () => {
    test('Create button renders', () => {
        const buttonLabel = "Create";
        expect(wrapper.find("button").text()).toEqual(buttonLabel);
    });
});