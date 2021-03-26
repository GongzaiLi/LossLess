import { shallowMount } from '@vue/test-utils';
import CreateBusiness from "@/components/business/CreateBusiness";

let wrapper;

beforeEach(() => {
    wrapper = shallowMount(CreateBusiness, {
        propsData: {},
        mocks: {},
        stubs: {},
        methods: {},
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