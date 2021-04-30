import {mount, createLocalVue, config} from '@vue/test-utils';
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue';
import Register from '../../components/user/Register';
import VueRouter from 'vue-router';



const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(VueRouter);
const router = new VueRouter()

config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;

beforeEach(() => {
  jest
    .spyOn(global.Date, 'now')
    .mockImplementationOnce(() =>
      new Date('04/12/2021').valueOf()
    );
  wrapper = mount(Register, {
    localVue,
    router
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

describe('Date Validation', () => {
  test('Invalid if no date entered', async () => {
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("Please enter a valid birthdate");
  });

  test('Invalid if date in future', async () => {
    wrapper.vm.dateOfBirth = "04/13/2021";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("You must be at least 13 years old");
  });

  test('Invalid if less than 13 years', async () => {
    wrapper.vm.dateOfBirth = "04/13/2008";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("You must be at least 13 years old");
  });

  test('Valid if 13 years', async () => {
    wrapper.vm.dateOfBirth = "04/12/2008";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("");
  });

  test('Valid if 130 years', async () => {
    wrapper.vm.dateOfBirth = "04/13/1890";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("");
  });

  test('Invalid if 130 years', async () => {
    wrapper.vm.dateOfBirth = "04/11/1890";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("Please enter a valid birthdate");
  });
});
