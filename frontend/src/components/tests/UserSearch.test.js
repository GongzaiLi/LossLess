import {createLocalVue, shallowMount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import UserSearch from '../UserSearch'; // name of your Vue component

let wrapper;

let userData = {
  role:"user"
}
// fake the localStorage to doing the testing.
const mockUserAuthPlugin = function install(Vue) {
  Vue.mixin({
    methods: {
      $getCurrentUser: () => userData
    }
  });
}

beforeEach(() => {

  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(mockUserAuthPlugin);

  wrapper = shallowMount(UserSearch, {
    localVue,
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {},
    computed: {},
  })
});

afterEach(() => {
  wrapper.destroy();
});

describe('UserSearch', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});