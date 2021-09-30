import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import ProfileImage from '../../components/model/ProfileImage';
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

const details = {
    id: 1,
    filename: "image.png",
    thumbnail: "image_thumbnail.png"
}

let $currentUser = {
    id: 1,
    currentlyActingAs: null,
}

beforeEach(() => {
    jest.mock('../../../public/profile-default.jpg');
    wrapper = mount(ProfileImage, {
        localVue,
        router,
        props: {details},
        mocks: {$log, $currentUser, }
    });
    wrapper.setData({confirmed:true, canSave: false, imageURL: null})
});

afterEach(() => {
    wrapper.destroy();
});

describe('Testing delete image when modifying user', () => {
    test('Successfully delete a user image when one exists', async () => {
        $currentUser.currentlyActingAs = null;
        wrapper.vm.profileImage = details;
        wrapper.vm.uploaded = false;

        await Api.deleteUserProfileImage.mockResolvedValue(
            {
                status: 201
            })

        await wrapper.vm.confirmDeleteImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.profileImage).toStrictEqual(null)
        expect(Api.deleteUserProfileImage).toHaveBeenCalled();
    });

    test('4xx-error-delete-user-image', async () => {
        $currentUser.currentlyActingAs = null;
        Api.deleteUserProfileImage.mockRejectedValue({
            response: {
                data: {message: "User given does not have a profile image"},
                status: 400
            }
        });

        wrapper.vm.profileImage = details;
        await wrapper.vm.confirmDeleteImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Deleting image failed: User given does not have a profile image"]);
    });

    test('500-error-delete-user-image', async () => {
        $currentUser.currentlyActingAs = null;
        Api.deleteUserProfileImage.mockRejectedValue({})

        wrapper.vm.profileImage = details;
        await wrapper.vm.confirmDeleteImage();

        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
    });
});

describe('Testing-api-post-upload-image-for-user', () => {
    global.URL.createObjectURL = jest.fn();
    it('Successful-upload-image', async () => {
        $currentUser.currentlyActingAs = null;
        Api.uploadProfileImage.mockResolvedValue(
            {
                status: 201
            })

        await wrapper.vm.uploadImageRequest();
        expect(wrapper.vm.errors).toStrictEqual([]);
    });

    it('413-error-upload-image', async () => {
        $currentUser.currentlyActingAs = null;
        Api.uploadProfileImage.mockRejectedValue({
            response: {
                data: {message: "The image you tried to upload is too large. Images must be less than 1MB in size."},
                status: 413
            }
        });
        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["The image you tried to upload is too large. Images must be less than 1MB in size."]);
    });

    it('4xx-error-upload-image', async () => {
        $currentUser.currentlyActingAs = null;
        Api.uploadProfileImage.mockRejectedValue({
            response: {
                data: {message: "Error Uploading"},
                status: 400
            }
        });
        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Uploading image failed: Error Uploading"]);
    });

    it('500-error-upload-image', async () => {
        $currentUser.currentlyActingAs = null;
        Api.uploadProfileImage.mockRejectedValue({});

        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
    });
});

describe('Testing delete business image when modifying business', () => {

    test('Successfully delete a business image when one exists', async () => {
        $currentUser.currentlyActingAs = 1;
        await Api.deleteBusinessProfileImage.mockResolvedValue(
            {
                status: 201
            })

        wrapper.vm.profileImage = details;
        wrapper.vm.uploaded = false;

        await wrapper.vm.confirmDeleteImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.profileImage).toStrictEqual(null)
        expect(Api.deleteBusinessProfileImage).toHaveBeenCalled();
    });

    test('4xx-error-delete-business-image', async () => {
        $currentUser.currentlyActingAs = 1;

        Api.deleteBusinessProfileImage.mockRejectedValue({
            response: {
                data: {message: "Some message"},
                status: 400
            }
        });

        wrapper.vm.profileImage = details;
        await wrapper.vm.confirmDeleteImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Deleting image failed: Some message"]);
    });

    test('500-error-delete-business-image', async () => {
        $currentUser.currentlyActingAs = 1;

        await Api.deleteBusinessProfileImage.mockRejectedValue({})

        wrapper.vm.profileImage = details;
        await wrapper.vm.confirmDeleteImage();

        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
    });
});

describe('Testing-api-post-upload-image-for-business', () => {
    global.URL.createObjectURL = jest.fn();
    it('Successful-upload-image-for-business', async () => {
        $currentUser.currentlyActingAs = 1;

        Api.uploadBusinessProfileImage.mockResolvedValue(
            {
                status: 201
            })

        await wrapper.vm.uploadImageRequest();
        expect(wrapper.vm.errors).toStrictEqual([]);
    });

    it('413-error-upload-image-for-business', async () => {
        $currentUser.currentlyActingAs = 1;

        Api.uploadBusinessProfileImage.mockRejectedValue({
            response: {
                data: {message: "The image you tried to upload is too large. Images must be less than 1MB in size."},
                status: 413
            }
        });
        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["The image you tried to upload is too large. Images must be less than 1MB in size."]);
    });

    it('4xx-error-upload-image-for-business', async () => {
        $currentUser.currentlyActingAs = 1;

        Api.uploadBusinessProfileImage.mockRejectedValue({
            response: {
                data: {message: "Error Uploading"},
                status: 400
            }
        });
        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Uploading image failed: Error Uploading"]);
    });

    it('500-error-upload-image-for-business', async () => {
        $currentUser.currentlyActingAs = 1;

        Api.uploadBusinessProfileImage.mockRejectedValue({});

        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
    });
});

describe('Testing-cancel-and-save-methods', () => {

    it('save-sets-variables-and-makes-an-api-request', async () => {
        wrapper.vm.uploadImageRequest = jest.fn();
        await wrapper.vm.save();
        expect(wrapper.vm.confirmed).toBeTruthy();
        expect(wrapper.vm.uploadImageRequest).toHaveBeenCalled();
        expect(wrapper.vm.canSave).toBeFalsy();
    });

});

