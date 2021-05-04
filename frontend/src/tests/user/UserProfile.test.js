import { shallowMount, createLocalVue } from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import userProfile from '../../components/user/UserProfile';

const $route = {
  params: {
    id: 0
  }
};

const $log = {
  debug() {
  }
};

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

  wrapper = shallowMount(userProfile, {
    localVue,
    propsData: {},
    mocks: {$route, $log},
    stubs: {},
    methods: {},
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Profile', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
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