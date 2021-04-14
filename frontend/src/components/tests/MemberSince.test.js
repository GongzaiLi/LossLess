import { shallowMount, createLocalVue } from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import memberSince from '../MemberSince';

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

describe('Register', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

test('same-start-end', () => {
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2021-03-11T00:32:00Z"),
        new Date("2021-03-11T00:32:00Z")
    )).toEqual({years: 0, months: 0});
});

test('almost-a-month', () => {
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2021-03-10T00:32:00Z"),
        new Date("2021-04-09T00:32:01Z")
    )).toEqual({years: 0, months: 0});
});

test('one-month', () => {
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2021-05-10T00:32:00Z"),
        new Date("2021-06-10T00:32:01Z")
    )).toEqual({years: 0, months: 1});
});

test('almost-a-year', () => {
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2020-01-01T00:00:00Z"),
        new Date("2020-12-30T23:59:58Z")
    )).toEqual({years: 0, months: 11});
});

test('1-year-difference', () => {
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2020-05-10T00:32:00Z"),
        new Date("2021-05-10T00:32:01Z")
    )).toEqual({years: 1, months: 0});
});

test('5-years-3-months', () => {
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2016-05-10T00:32:00Z"),
        new Date("2021-10-11T00:32:01Z")
    )).toEqual({years: 5, months: 5});
});




test('only-first-and-last-name', async () => {
    wrapper.vm.userData.firstName = "First";
    wrapper.vm.userData.lastName = "Last";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.fullName).toEqual("First Last");
});

test('first-and-middle-and-last-name', async () => {
    wrapper.vm.userData.firstName = "First";
    wrapper.vm.userData.lastName = "Last";
    wrapper.vm.userData.middleName = "Middle";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.fullName).toEqual("First Middle Last");
});

test('first-and-middle-and-last-and-nick-name', async () => {
    wrapper.vm.userData.firstName = "First";
    wrapper.vm.userData.lastName = "Last";
    wrapper.vm.userData.middleName = "Middle";
    wrapper.vm.userData.nickName = "Nick";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.fullName).toEqual("First Middle Last (Nick)");
});

test('first-and-last-and-nick-name', async () => {
    wrapper.vm.userData.firstName = "First";
    wrapper.vm.userData.lastName = "Last";
    wrapper.vm.userData.nickName = "Nick";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.fullName).toEqual("First Last (Nick)");
});