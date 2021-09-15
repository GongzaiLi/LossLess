import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import UserDetailsModal from '../../components/user/UserDetailsModal';
import VueRouter from 'vue-router';
import Api from "../../Api";

const $log = {
  debug: jest.fn(),
};


jest.mock('../../Api');
jest.mock("../../../public/profile-default.jpg", () => {
}) // mock image

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
  wrapper = shallowMount(UserDetailsModal, {
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
    Api.register.mockRejectedValue({
      response: {
        data: {message: "Email address already in use"},
        status: 409
      }
    });
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

  test('Invalid if password not match confirm password', async () => {
    wrapper.setProps({isEditUser: true});
    wrapper.vm.userData.newPassword = 'bad password';
    wrapper.vm.userData.confirmPassword = 'other password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity()).toEqual("Passwords do not match.");
  });

  test('Valid if password match confirm password', async () => {
    wrapper.setProps({isEditUser: true});
    wrapper.vm.userData.newPassword = 'password';
    wrapper.vm.userData.confirmPassword = 'password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity()).toEqual("");
  });

});

describe('Testing-password-validation-for-register', () => {

  test('Invalid if password not match confirm password', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.newPassword = 'bad password';
    wrapper.vm.userData.confirmPassword = 'other password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity()).toEqual("Passwords do not match.");
  });

  test('Valid if password match confirm password', async () => {
    wrapper.vm.isEditUser = false;
    wrapper.vm.userData.newPassword = 'password';
    wrapper.vm.userData.confirmPassword = 'password';
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.passwordMatchValidity()).toEqual("");
  });
});

describe('Testing-api-put-updating-user', () => {
  const event = {
    preventDefault: () => {
    }
  };
  it('Successful-update-user', async () => {
    Api.modifyUser.mockResolvedValue({
      status: 200
    });
    Api.uploadProfileImage.mockResolvedValue(
      {
        status: 201
      })

    await wrapper.vm.updateUser(event);
    expect(wrapper.vm.errors).toStrictEqual([]);
  });

  it('409-error-update-user-testing', async () => {
    Api.modifyUser.mockRejectedValue({
      response: {
        data: {message: "Email address already in use"},
        status: 409
      }
    });
    await wrapper.vm.updateUser(event);
    expect(wrapper.vm.errors).toStrictEqual(["Updating user failed: Email address already in use"]);
  });

  it('500-error-update-user-testing', async () => {
    Api.modifyUser.mockRejectedValue({});
    await wrapper.vm.updateUser(event);
    expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
  });
});

describe('Testing-changing-password', () => {
  beforeEach(() =>
    wrapper.setProps({isEditUser: true}));

  test('newPassword is undefined if not changing password', async () => {
    wrapper.vm.userData.newPassword = 'password';
    wrapper.vm.userData.confirmPassword = 'password';
    wrapper.vm.changePassword = false;

    expect(wrapper.vm.getEditData().newPassword).toBeUndefined();
  });

  test('newPassword is set if changing password', async () => {
    wrapper.vm.userData.newPassword = 'password';
    wrapper.vm.userData.confirmPassword = 'password';
    wrapper.vm.changePassword = true;

    expect(wrapper.vm.getEditData().newPassword).toBe('password');
  });

});


describe('Testing-beforeMount-when-is-edit-user-modal', () => {

  let wrapperNew;

  const userDetail = {
    firstName: "Leonard",
    "lastName": "Frazier",
    "bio": "occaecat sunt irure non ut culpa sunt mollit ex et commodo do sit nostrud voluptate culpa occaecat duis est qui",
    "email": "leonard.frazier@essensia.com",
    "dateOfBirth": "2001-05-16",
    homeAddress: {
      "streetNumber": 481,
      "streetName": "Schroeders Avenue",
      "city": "Fairview",
      "region": "Nevada",
      "country": "Sierra Leone",
      "postcode": 4740
    }
  };

  beforeEach(() => {
    wrapperNew = shallowMount(UserDetailsModal, {
      localVue,
      router,
      propsData: {isEditUser: true, userDetails: userDetail},
      mocks: {$log}
    });
  })

  afterEach(() => {
    wrapperNew.destroy();
  });

  test('Testing-beforeMount-when-is-edit-user-modal-userDetails-set-up',  () => {

    expect(wrapperNew.vm.userData.firstName).toStrictEqual(userDetail.firstName);
    expect(wrapperNew.vm.userData.lastName).toStrictEqual(userDetail.lastName);
    expect(wrapperNew.vm.userData.bio).toStrictEqual(userDetail.bio);
    expect(wrapperNew.vm.userData.email).toStrictEqual(userDetail.email);
    expect(wrapperNew.vm.userData.dateOfBirth).toStrictEqual(userDetail.dateOfBirth);
    expect(wrapperNew.vm.userData.homeAddress).toStrictEqual(userDetail.homeAddress);
    expect(wrapperNew.vm.userData.oldPassword).toStrictEqual("");
    expect(wrapperNew.vm.userData.newPassword).toStrictEqual("");
    expect(wrapperNew.vm.userData.confirmPassword).toStrictEqual("");
  });



});