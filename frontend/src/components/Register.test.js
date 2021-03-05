import { shallowMount } from '@vue/test-utils';
import Register from './Register'; // name of your Vue component

let wrapper;

beforeEach(() => {
    wrapper = shallowMount(Register, {
        propsData: {},
        mocks: {},
        stubs: {},
        methods: {},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Register', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

test('all-fields-empty', async () => {
    expect(wrapper.find('#error-txt').exists()).toBe(false)
    const regBtn = wrapper.find('#register-btn');
    await regBtn.trigger('click');
    expect(wrapper.find('#error-txt').text()).toBe("One or more mandatory fields are empty!");
});