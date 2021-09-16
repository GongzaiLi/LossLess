import {createLocalVue, mount} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import VueRouter from 'vue-router';
import ListingSearchPage from '../../components/listing/ListingSearchPage'; // name of your Vue component
import Auth from '../../auth';
import Api from '../../Api';
import {getToday} from "../../util";
import testCards from './testCards.json';

let wrapper;
let defaultFilters;

jest.mock('../../Api');
jest.mock('../../util');

beforeEach(() => {

  defaultFilters = {
    productName: "",
    sort: "inventoryItem.product.name,asc",
    businessName: "",
    selectedBusinessType: null,
    businessLocation: "",
    closesStartDate: "2021-08-12",
    closesEndDate: "",
    priceMin: "",
    priceMax: "",
    businessTypes: []
  }

  Api.searchListings.mockResolvedValue({data: testCards});
  getToday.mockReturnValue("2021-08-12");

  const localVue = createLocalVue();

  localVue.use(VueRouter);
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(Auth);

  const router = new VueRouter();

  wrapper = mount(ListingSearchPage, {
    localVue,
    router
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Testing api get request search Listing function', () => {
  test('check-api-request-get-searchListings', async () => {
    await Api.searchListings.mockResolvedValue({data: testCards});
    await wrapper.vm.getListings();
    await wrapper.vm.$forceUpdate();
    expect(wrapper.vm.listings).toBe(testCards.listings);
  });

});

describe('Testing watcher for current page change', () => {
  test('check-get-listings-is-called-when-current-page-updated', async () => {
    wrapper.vm.currentPage = 3
    await wrapper.vm.$nextTick();
    expect(Api.searchListings).toHaveBeenLastCalledWith('',"", "", "", [], "", "2021-08-12", "", ["inventoryItem.product.name,asc"], 9, 2);
  });

});


describe('Add business type ', () => {
  test('adds normal business type to business types', () => {
    wrapper.vm.search.selectedBusinessType = "Accommodation and Food Services";
    wrapper.vm.addBusinessType();
    expect(wrapper.vm.search.businessTypes).toStrictEqual(["Accommodation and Food Services"]);
  })

  test('clears business types when null selected', () => {
    wrapper.vm.search.businessTypes = ["Accommodation and Food Services"];
    wrapper.vm.search.selectedBusinessType = null;
    wrapper.vm.addBusinessType();
    expect(wrapper.vm.search.businessTypes).toStrictEqual([]);
  })
});


describe('Remove Tag ', () => {
  test('removes unselected tag from business types', () => {
    wrapper.vm.search.selectedBusinessType = "Accommodation and Food Services";
    wrapper.vm.search.businessTypes = ["Accommodation and Food Services", "Retail Trade"];
    wrapper.vm.removeTag("Retail Trade");
    expect(wrapper.vm.search.businessTypes).toStrictEqual(["Accommodation and Food Services"]);
  })

  test('clears selected tag when removed', () => {
    wrapper.vm.search.selectedBusinessType = "Accommodation and Food Services";
    wrapper.vm.search.businessTypes = ["Accommodation and Food Services", "Retail Trade"];
    wrapper.vm.removeTag("Accommodation and Food Services");
    expect(wrapper.vm.search.businessTypes).toStrictEqual(["Retail Trade"]);
    expect(wrapper.vm.search.selectedBusinessType).toStrictEqual("");
  })

  test('sets selected business type to null when all tags removed', () => {
    wrapper.vm.search.selectedBusinessType = "Accommodation and Food Services";
    wrapper.vm.search.businessTypes = ["Accommodation and Food Services"];
    wrapper.vm.removeTag("Accommodation and Food Services");
    expect(wrapper.vm.search.businessTypes).toStrictEqual([]);
    expect(wrapper.vm.search.selectedBusinessType).toStrictEqual(null);
  })
});


describe('Sort orders for API computed ', () => {
  test('Correct value for location ascending ',  async() => {
    wrapper.vm.search.sort = 'location,asc';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.sortOrdersForAPI).toStrictEqual(["business.address.country,asc", "business.address.city,asc", "business.address.suburb,asc"]);
  })

  test('Correct value for location descending',async () => {
    wrapper.vm.search.sort = 'location,desc';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.sortOrdersForAPI).toStrictEqual(["business.address.country,desc", "business.address.city,desc", "business.address.suburb,desc"]);
  })

  test('Correct value for normal location', async() => {
    wrapper.vm.search.sort = 'price,desc';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.sortOrdersForAPI).toStrictEqual(["price,desc"]);
  })
});

describe('Testing search filters clearing functionality', () => {
  test('Search filters clear when clear button is pressed', () => {
    wrapper.vm.search.productName = "ABC"
    wrapper.vm.search.sort = "inventoryItem.product.name,asc"
    wrapper.vm.search.businessName = "XYZ"
    wrapper.vm.search.selectedBusinessType = null
    wrapper.vm.search.businessLocation = "some business"
    wrapper.vm.search.closesStartDate = "2011-12-10"
    wrapper.vm.search.closesEndDate = ""
    wrapper.vm.search.priceMin = "5"
    wrapper.vm.search.priceMax = "10"
    wrapper.vm.search.businessTypes = []

    wrapper.vm.clearFilters()
    expect(wrapper.vm.search).toStrictEqual(defaultFilters);
  })

})

describe('Testing date range for valid and invalid case', () => {

  test('Date range invalid when closes start date is after closes end date', () => {
    wrapper.vm.search.closesStartDate = "2021-12-10"
    wrapper.vm.search.closesEndDate = "2020-12-10"
    expect(wrapper.vm.invalidDateRange).toBeTruthy();
  })

  test('Date range valid when closes start date is before closes end date', () => {
    wrapper.vm.search.closesStartDate = "2020-12-10"
    wrapper.vm.search.closesEndDate = "2021-12-10"
    expect(wrapper.vm.invalidDateRange).toBeFalsy();
  })

})

describe('Testing price range for valid and invalid case', () => {

  test('Price range invalid when price min is greater than price max', () => {
    wrapper.vm.search.priceMin = "3"
    wrapper.vm.search.priceMax = "2"
    expect(wrapper.vm.invalidPriceRange).toBeTruthy();
  })

  test('Price range valid when price max is greater than price min', () => {
    wrapper.vm.search.priceMin = "2"
    wrapper.vm.search.priceMax = "3"
    expect(wrapper.vm.invalidPriceRange).toBeFalsy();
  })

  test('Price range valid when there is a price min and price max is empty', () => {
    wrapper.vm.search.priceMin = "15"
    wrapper.vm.search.priceMax = ""
    expect(wrapper.vm.invalidPriceRange).toBeFalsy();
  })

})