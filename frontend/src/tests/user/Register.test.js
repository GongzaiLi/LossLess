import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Register from '../../components/user/Register';
import VueRouter from 'vue-router';
import Api from "../../Api";

const $log = {
  debug: jest.fn(),
};

jest.mock('../../Api');
jest.mock("../../../public/profile-default.jpg", ()=>{}) // mock image

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
  jest.mock('../../../public/profile-default.jpg');
  wrapper = mount(Register, {
    localVue,
    router,
    mocks: {$log}
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
    wrapper.vm.userData.dateOfBirth = "04/13/2021";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("You must be at least 13 years old");
  });

  test('Invalid if less than 13 years', async () => {
    wrapper.vm.userData.dateOfBirth = "04/13/2008";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("You must be at least 13 years old");
  });

  test('Valid if 13 years', async () => {
    wrapper.vm.userData.dateOfBirth = "04/12/2008";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("");
  });

  test('Valid if 119 years', async () => {
    wrapper.vm.userData.dateOfBirth = "04/13/1901";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("");
  });

  test('Invalid if 120 years', async () => {
    wrapper.vm.userData.dateOfBirth = "04/11/1900";
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthCustomValidity).toEqual("You cannot be older than 120 years");
  });
});

describe('Testing-api-post-register', () => {
  const event = {
    preventDefault: () => {
    }
  };
  it('Successful-register', async () => {
    Api.register.mockResolvedValue({
      data: {
        id: 0
      },
      status: 201
    });
    Api.getUser.mockResolvedValue({
        data: {
          id: 0
        }
      }
    )

    await wrapper.vm.register(event);
    expect(wrapper.vm.errors).toStrictEqual([]);
  });

  it('400-error-register-testing', async () => {
    Api.register.mockRejectedValue({response: {
      data:  {message: "Email address already in use"},
      status: 409
    }});
    await wrapper.vm.register(event);
    expect(wrapper.vm.errors).toStrictEqual(["Registration failed: Email address already in use"]);
  });

  it('409-error-register-testing', async () => {
    Api.register.mockRejectedValue({});
    await wrapper.vm.register(event);
    expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
  });
});

describe('Testing-password-validation-for-editing', () => {

  test('Invalid if no old password but new password present', async () => {
    wrapper.setProps({ isEditUser: true });
    wrapper.vm.userData.password = '';
    wrapper.vm.userData.newPassword = 'a password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordNewPasswordValidity).toEqual("Old Password required to change Password");
  });

  test('Valid if  old password and new password present', async () => {
    wrapper.setProps({ isEditUser: true });
    wrapper.vm.userData.password = 'old password';
    wrapper.vm.userData.newPassword = 'a password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordNewPasswordValidity).toEqual("");
  });


  test('Invalid if no old password but new email present', async () => {
    wrapper.setProps({ isEditUser: true });
    wrapper.vm.userData.password = '';
    wrapper.vm.userData.email = 'a new email';
    wrapper.vm.email = 'email'
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordEmailValidity).toEqual("Old Password required to change email");
  });

  test('Valid if old password and email not changed present', async () => {
    wrapper.setProps({ isEditUser: true });
    wrapper.vm.userData.password = 'old password';
    wrapper.vm.userData.email = 'email';
    wrapper.vm.email = 'email'
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordEmailValidity).toEqual("");
  });

  test('Invalid if password not match confirm password', async () => {
    wrapper.setProps({ isEditUser: true });
    wrapper.vm.userData.newPassword = 'bad password';
    wrapper.vm.userData.confirmPassword = 'other password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity).toEqual("Passwords do not match.");
  });

  test('Valid if password match confirm password', async () => {
    wrapper.setProps({ isEditUser: true });
    wrapper.vm.userData.newPassword = 'password';
    wrapper.vm.userData.confirmPassword = 'password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity).toEqual("");
  });

});

describe('Testing-password-validation-for-register', () => {

  test('valid if no old password but new password present', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.password = '';
    wrapper.vm.userData.newPassword = 'a password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordNewPasswordValidity).toEqual("");
  });

  test('Valid if  old password and new password present', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.password = 'old password';
    wrapper.vm.userData.newPassword = 'a password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordNewPasswordValidity).toEqual("");
  });


  test('valid if no old password but new email present', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.password = '';
    wrapper.vm.userData.email = 'a new email';
    wrapper.vm.email = 'email'
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordEmailValidity).toEqual("");
  });

  test('Valid if old password and email not changed present', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.password = 'old password';
    wrapper.vm.userData.email = 'email';
    wrapper.vm.email = 'email'
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordEmailValidity).toEqual("");
  });

  test('Invalid if password not match confirm password', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.newPassword = 'bad password';
    wrapper.vm.userData.confirmPassword = 'other password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity).toEqual("Passwords do not match.");
  });

  test('Valid if password match confirm password', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.newPassword = 'password';
    wrapper.vm.userData.confirmPassword = 'password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity).toEqual("");
  });
});