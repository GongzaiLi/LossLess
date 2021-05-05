import { shallowMount, createLocalVue } from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import memberSince from '../../components/model/MemberSince';

let wrapper;
let mockDateNow = '2019-05-14T11:01:58.135Z';
jest
    .spyOn(global.Date, 'now')
    .mockImplementationOnce(() =>
        new Date(mockDateNow).valueOf()
    );


beforeEach(() => {
    const localVue = createLocalVue()

    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(memberSince, {
        localVue,
        propsData: {},
        mocks: {},
        stubs: {},
        methods: {},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('MemberSince', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

describe('MemberSince', () => {

    test('correct if 0 years and months', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2016-05-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2016-05-10T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('10 May 2016 (0 Months)')
    });

    test('correct if 0 years and 1 month', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2016-05-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2016-04-09T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('9 April 2016 (1 Month)')
    });

    test('correct if 9 months', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2016-11-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2016-02-09T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('9 February 2016 (9 Months)')
    });

    test('correct if 1 year and 0 months', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2016-05-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2015-05-09T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('9 May 2015 (1 Year)')
    });

    test('correct if 1 year and 1 month', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2016-05-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2015-04-09T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('9 April 2015 (1 Year and 1 Month)')
    });

    test('correct if 4 years and 9 months', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2019-10-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2015-01-09T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('9 January 2015 (4 Years and 9 Months)')
    });

    test('correct if 5 years', async () => {
        jest
          .spyOn(global.Date, 'now')
          .mockImplementationOnce(() =>
            new Date('2016-05-10T00:32:00Z').valueOf()
          );

        await wrapper.setProps({ date: '2011-05-09T00:32:00Z' });

        expect(wrapper.vm.memberSince).toBe('9 May 2011 (5 Years)')
    });
});