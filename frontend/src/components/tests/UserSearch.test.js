import {createLocalVue, shallowMount} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import UserSearch from '../UserSearch'; // name of your Vue component

let wrapper;

let $currentUser = {
  role:"user"
}

beforeEach(() => {

  const localVue = createLocalVue()

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(UserSearch, {
    localVue,
    propsData: {},
    mocks: {$currentUser},
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