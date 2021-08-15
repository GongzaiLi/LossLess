import {createLocalVue, mount} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import VueRouter from 'vue-router';
import ListingSearchPage from '../../components/listing/ListingSearchPage'; // name of your Vue component
import Auth from '../../auth';
import Api from '../../Api';
import {getToday} from "../../util";
import testCards from './testCards.json';

let wrapper;

jest.mock('../../Api');
jest.mock('../../util');

beforeEach(() => {
  Api.searchListings.mockResolvedValue({data: testCards});
  //getToday.mockReturnValueOnce("2021-08-12");

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

describe('Route watcher', () => {
  test('re-queries data when route query changed', async () => {
    await wrapper.vm.$router.replace({path: `/listingSearch`, query: {searchQuery: 'blackwaternosugar'}});
    await wrapper.vm.$nextTick();
    expect(Api.searchListings).toHaveBeenLastCalledWith("blackwaternosugar", "", "", "", [], "", "", "", "inventoryItem.product.name,asc", 9, -1);
  });

  test('re-queries all listings data when query not exists', async () => {
    await wrapper.vm.$router.replace({path: `/listingSearch`});
    await wrapper.vm.$nextTick();
    expect(Api.searchListings).toHaveBeenLastCalledWith('',"", "", "", [], "", "", "", "inventoryItem.product.name,asc", 9, -1);
  });
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
    expect(Api.searchListings).toHaveBeenLastCalledWith('',"", "", "", [], "", "", "", "inventoryItem.product.name,asc", 9, 2);
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