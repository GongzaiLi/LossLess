import {createLocalVue, shallowMount} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import AddressInput from '../AddressInput'; // name of your Vue component

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
  expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
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
  expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
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
  expect(wrapper.vm.getStringFromPhotonAddress(address)).toBe(null);
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


test('split-address-city-and-region', async () => {
  const address = {
    housenumber: "100",
    street: "Sesame Street",
    name: "A name",
    city: "Gotham City",
    country: "New Zealand",
    region: "canterbury",
    postcode: "1234"
  };

  const expectedAddress = {
    "streetNumber": "100",
    "streetName": "Sesame Street",
    "city": "Gotham City",
    "region": "canterbury",
    "country": "New Zealand",
    "postcode": "1234"
  }
  wrapper.vm.splitAddress(address);
  expect(wrapper.vm.homeAddress).toStrictEqual(expectedAddress);

});


test('split-address-name-and-streetname-and-number', async () => {
  const address = {
    housenumber: "100",
    street: "Sesame Street",
    name: "A name",
    city: "Gotham City",
    country: "New Zealand",
    region: "canterbury",
    postcode: "1234"
  };

  const expectedAddress = {
    "streetNumber": "100",
    "streetName": "Sesame Street",
    "city": "Gotham City",
    "region": "canterbury",
    "country": "New Zealand",
    "postcode": "1234"
  }
  wrapper.vm.splitAddress(address);
  expect(wrapper.vm.homeAddress).toStrictEqual(expectedAddress);

});

test('split-address-county-district-city', async () => {
  const address = {
    housenumber: "100",
    street: "Sesame Street",
    name: "A name",
    county: "a county",
    district: "a district",
    city: "Gotham City",
    country: "New Zealand",
    region: "canterbury",
    postcode: "1234"
  };

  const expectedAddress = {
    "streetNumber": "100",
    "streetName": "Sesame Street",
    "city": "a county",
    "region": "canterbury",
    "country": "New Zealand",
    "postcode": "1234"
  }
  wrapper.vm.splitAddress(address);
  expect(wrapper.vm.homeAddress).toStrictEqual(expectedAddress);

});


test('split-address-state-region', async () => {
  const address = {
    housenumber: "100",
    street: "Sesame Street",
    name: "A name",
    county: "a county",
    district: "a district",
    city: "Gotham City",
    country: "New Zealand",
    region: "canterbury",
    state: "a state",
    postcode: "1234"
  };

  const expectedAddress = {
    "streetNumber": "100",
    "streetName": "Sesame Street",
    "city": "a county",
    "region": "a state",
    "country": "New Zealand",
    "postcode": "1234"
  }
  wrapper.vm.splitAddress(address);
  expect(wrapper.vm.homeAddress).toStrictEqual(expectedAddress);

});


test('select-address-option, 1 address', async () => {
  const address = {
    housenumber: "100",
    street: "Sesame Street",
    name: "A name",
    city: "Gotham City",
    country: "New Zealand",
    region: "canterbury",
    postcode: "1234"

  };
  const address2 = {
    housenumber: "50",
    street: "jacks Street",
    name: "bob",
    city: "new york City",
    country: "usa",
    region: "South",
    postcode: "5678"
  };
  const addressString = "100 Sesame Street, Gotham City, New Zealand";
  const addressString2 = "50 jacks Street, new york City, usa";
  const expectedAddress = {
    "streetNumber": "100",
    "streetName": "Sesame Street",
    "city": "Gotham City",
    "region": "canterbury",
    "country": "New Zealand",
    "postcode": "1234"
  }
  wrapper.vm.addressResults.push(addressString);
  wrapper.vm.addressResults.push(addressString2);
  wrapper.vm.addressObject.push(address);
  wrapper.vm.addressObject.push(address2);
  wrapper.vm.selectAddressOption(addressString)
  expect(wrapper.vm.homeAddress).toStrictEqual(expectedAddress);
});

test('check-clearAddress-is-work', () => {
  const homeAddress = {
    streetNumber: "100",
    streetName: "A name",
    city: "A city",
    region: "A region",
    country: "A country",
    postcode: "A postcode"
  };
  const expectedAddress = {
    streetNumber: "",
    streetName: "",
    city: "",
    region: "",
    country: "",
    postcode: ""
  };
  wrapper.vm.homeAddress = homeAddress;
  wrapper.vm.clearAddress();
  expect(wrapper.vm.homeAddress).toStrictEqual(expectedAddress);
});