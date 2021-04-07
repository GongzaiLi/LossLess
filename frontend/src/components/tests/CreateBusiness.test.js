import {shallowMount} from '@vue/test-utils';
// import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import CreateBusiness from '../business/CreateBusiness';

let wrapper;



beforeEach(() => {

    wrapper = shallowMount(CreateBusiness, {
        data() {
            return {
                name: "",
                description: "",
                address: "",
                businessType: "",
            }
        }
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

        expect(wrapper.find("b-button").exists()).toBe(true);


        const createBtn = wrapper.find("b-button");

        const spy = jest.spyOn(CreateBusiness.methods, 'createBusiness');


        await createBtn.trigger('submit');

        expect(CreateBusiness.methods.createBusiness).toBeCalled();


    });
});

test('Create Business button records the filled by user and returns them.', async () => {



})











// HTML TESTING ----------------------------------------------------------------
describe('CreateBusiness', () => {
    test('Page heading renders', () => {
        const headingExpected = "Create a Business";
        expect(wrapper.find('h1').text()).toEqual(headingExpected);
    });
});


describe('CreateBusiness', () => {
    test('Business Name label renders', () => {
        const headingExpected = "Business name *";
        expect(wrapper.findAll("b").at(0).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('Description label renders', () => {
        const headingExpected = "Description";
        expect(wrapper.findAll("b").at(1).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('Business Address label renders', () => {
        const headingExpected = "Business Address *";
        expect(wrapper.findAll("b").at(2).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('Business Type label renders', () => {
        const headingExpected = "Business Type *";
        expect(wrapper.findAll("b").at(3).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down has default value Choose ...', () => {
        const headingExpected = "Choose ...";
        expect(wrapper.findAll("select option").at(0).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes Accommodation and Food Services', () => {
        const headingExpected = "Accommodation and Food Services";
        expect(wrapper.findAll("select option").at(1).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes  Retail Trade', () => {
        const headingExpected = "Retail Trade";
        expect(wrapper.findAll("select option").at(2).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes Charitable organisation', () => {
        const headingExpected = "Charitable organisation";
        expect(wrapper.findAll("select option").at(3).text()).toEqual(headingExpected);
    });
});

describe('CreateBusiness', () => {
    test('business types in drop down includes Non-profit organisation', () => {
        const headingExpected = "Non-profit organisation";
        expect(wrapper.findAll("select option").at(4).text()).toEqual(headingExpected);
    });
});


describe('CreateBusiness', () => {
    test('Create button renders', () => {
        const headingExpected = "Create";
        expect(wrapper.find("b-button").text()).toEqual(headingExpected);
    });
});
