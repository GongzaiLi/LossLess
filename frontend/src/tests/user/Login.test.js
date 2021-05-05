import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import VueRouter from 'vue-router';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import login from '../../components/user/Login';
import Api from "../../Api";

let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings


const $log = {
  debug: jest.fn(),
};


jest.mock('../../Api');


beforeEach(() => {
  const localVue = createLocalVue();
  const router = new VueRouter();

  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);
  localVue.use(VueRouter);

  //Api.login.mockRejectedValue(new Error(''));

  wrapper = shallowMount(login, {
    localVue,
    router,
    propsData: {},
    mocks: {$log},
    stubs: {},
    methods: {},
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Login-page', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});

describe('Testing-api-post-makeLoginRequest', () => {

  it('Successful Login', async () => {
    Api.login.mockResolvedValue({
      data: {
        id: 0
      },
      status: 200
    });
    Api.getUser.mockResolvedValue({
        data: {
          id: 0
        }
      }
    )
    wrapper.vm.email = "a@a";
    wrapper.vm.password = "a";

    await wrapper.vm.makeLoginRequest();
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.errors).toStrictEqual([]);
  });

  it('Failed login attempt, email or password incorrect', async () => {
    Api.login.mockRejectedValue({ // rejected
      response: {status: 400}
    });
    wrapper.vm.email = "a";
    wrapper.vm.password = "a";

    await wrapper.vm.makeLoginRequest();
    expect(wrapper.vm.errors).toStrictEqual(["The given username or password is incorrect."]);
  });
});
