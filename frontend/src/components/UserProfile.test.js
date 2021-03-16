import { shallowMount, createLocalVue } from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import UserProfile from './UserProfile'; // name of your Vue component

let wrapper;
let mockDateNow = '2019-05-14T11:01:58.135Z';
jest
    .spyOn(global.Date, 'now')
    .mockImplementationOnce(() =>
        new Date(mockDateNow).valueOf()
    );

const $route = {
    params: {
        id: 0
    }
}

beforeEach(() => {
    const localVue = createLocalVue()

    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(UserProfile, {
        localVue,
        propsData: {},
        mocks: {
            $route
        },
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
    wrapper.vm.userData.created = "2021-03-11T00:32:00Z";
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2021-03-11T00:32:00Z"),
        new Date("2021-03-11T00:32:00Z")
    )).toEqual({years: 0, months: 0});
});

test('almost-a-month', () => {
    wrapper.vm.userData.created = "2021-03-11T00:32:00Z";
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2021-03-10T00:32:00Z"),
        new Date("2021-04-09T00:32:01Z")
    )).toEqual({years: 0, months: 0});
});

test('one-month', () => {
    wrapper.vm.userData.created = "2021-03-11T00:32:00Z";
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2021-05-10T00:32:00Z"),
        new Date("2021-06-10T00:32:01Z")
    )).toEqual({years: 0, months: 1});
});

test('almost-a-year', () => {
    wrapper.vm.userData.created = "2021-03-11T00:32:00Z";
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2020-01-01T00:00:00Z"),
        new Date("2020-12-30T23:59:58Z")
    )).toEqual({years: 0, months: 11});
});

test('1-year-difference', () => {
    wrapper.vm.userData.created = "2021-03-11T00:32:00Z";
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2020-05-10T00:32:00Z"),
        new Date("2021-05-10T00:32:01Z")
    )).toEqual({years: 1, months: 0});
});

test('5-years-3-months', () => {
    wrapper.vm.userData.created = "2021-03-11T00:32:00Z";
    expect(wrapper.vm.getMonthsAndYearsBetween(
        new Date("2016-05-10T00:32:00Z"),
        new Date("2021-10-11T00:32:01Z")
    )).toEqual({years: 5, months: 5});
});