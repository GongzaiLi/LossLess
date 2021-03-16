import {createLocalVue, shallowMount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import AddressInput from './AddressInput'; // name of your Vue component

let wrapper;

beforeEach(() => {
    const localVue = createLocalVue()

    localVue.use(BootstrapVue);
    localVue.use(BootstrapVueIcons);

    wrapper = shallowMount(AddressInput, {
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

describe('AddressInput', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

test('empty-address-object', async () => {
    const address = {};
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
});

test('street-no-houesnumber-or-name', async () => {
    const address = {
        street: "Sesame Street",
        city: "Gotham City",
        country: "New Zealand",
        district: "District 12",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
});

test('no-country', async () => {
    const address = {
        housenumber: "100",
        street: "Sesame Street",
        city: "Gotham City",
        district: "District 12",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
});

test('no-county-district-city', async () => {
    const address = {
        housenumber: "100",
        street: "Sesame Street",
        name: "A name",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
});

test('no-county', async () => {
    const address = {
        housenumber: "100",
        street: "Sesame Street",
        name: "A name",
        city: "Gotham City",
        country: "New Zealand",
        district: "District 12",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe("100 Sesame Street, District 12, New Zealand");
});

test('no-district-or-county', async () => {
    const address = {
        housenumber: "100",
        street: "Sesame Street",
        name: "A name",
        city: "Gotham City",
        country: "New Zealand",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe("100 Sesame Street, Gotham City, New Zealand");
});

test('only-name', async () => {
    const address = {
        name: "A name",
        city: "Gotham City",
        country: "New Zealand",
        district: "District 12",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe("A name, A County, New Zealand");
});

test('no-housenumber', async () => {
    const address = {
        name: "A name",
        street: "Sesame Street",
        city: "Gotham City",
        country: "New Zealand",
        district: "District 12",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe("A name, A County, New Zealand");
});

test('no-street', async () => {
    const address = {
        name: "A name",
        housenumber: "100",
        city: "Gotham City",
        country: "New Zealand",
        district: "District 12",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe("A name, A County, New Zealand");
});

test('all-fields', async () => {
    const address = {
        housenumber: "100",
        street: "Sesame Street",
        name: "A name",
        city: "Gotham City",
        country: "New Zealand",
        district: "District 12",
        county: "A County",
    };
    expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe("100 Sesame Street, A County, New Zealand");
});