import {createLocalVue, mount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import VueRouter from 'vue-router';
import ListingSearchPage from '../../components/listing/ListingSearchPage'; // name of your Vue component
import Auth from '../../auth';
import Api from '../../Api';
import testCards from './testCards.json';

let wrapper;

jest.mock('../../Api');

beforeEach(() => {
  Api.searchListings.mockResolvedValue({data: testCards});

  const localVue = createLocalVue();

  localVue.use(VueRouter);
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(Auth);

  const router = new VueRouter();

  wrapper = mount(ListingSearchPage, {
    localVue,
    router
  })
});

afterEach(() => {
  wrapper.destroy();
});

describe('Route watcher', () => {
  test('re-queries data when route query changed', async () => {
    await wrapper.vm.$router.replace({path: `/listingSearch`, query: {searchQuery: 'blackwaternosugar'}});
    await wrapper.vm.$nextTick();
    expect(Api.searchListings).toHaveBeenLastCalledWith('blackwaternosugar');
  });

  test('re-queries all listings data when query not exists', async () => {
    await wrapper.vm.$router.replace({path: `/listingSearch`});
    await wrapper.vm.$nextTick();
    expect(Api.searchListings).toHaveBeenLastCalledWith('');
  });
});