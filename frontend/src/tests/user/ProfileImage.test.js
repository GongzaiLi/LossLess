import {mount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import ProfileImage from '../../components/user/ProfileImage';
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

beforeEach(() => {
    jest.mock('../../../public/profile-default.jpg');
    wrapper = mount(ProfileImage, {
        localVue,
        router,
        props: {details},
        mocks: {$log}
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Testing delete image when modifying user', () => {
    test('Successfully delete a user image when one exists', async () => {
        Api.deleteUserProfileImage.mockResolvedValue(
            {
                status: 201
            })

        wrapper.vm.profileImage = details;
        wrapper.vm.uploaded = false;

        await wrapper.vm.confirmDeleteImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.profileImage).toStrictEqual(null)
        expect(Api.deleteUserProfileImage).toHaveBeenCalled();
    });

    test('4xx-error-delete-user-image', async () => {
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
        Api.deleteUserProfileImage.mockRejectedValue({})

        wrapper.vm.profileImage = details;
        wrapper.vm.confirmDeleteImage();

        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
    });
});

describe('Testing-api-post-upload-image-for-user', () => {
    it('Successful-upload-image', async () => {
        Api.uploadProfileImage.mockResolvedValue(
            {
                status: 201
            })

        await wrapper.vm.uploadImageRequest();
        expect(wrapper.vm.errors).toStrictEqual([]);
    });

    it('413-error-upload-image', async () => {
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
        Api.uploadProfileImage.mockRejectedValue({});

        await wrapper.vm.uploadImageRequest();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.errors).toStrictEqual(["Sorry, we couldn't reach the server. Check your internet connection"]);
    });
});
