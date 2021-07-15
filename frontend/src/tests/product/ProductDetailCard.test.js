import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import productDetailCard from "../../components/product/ProductDetailCard";

config.showDeprecationWarnings = false  //to disable deprecation warnings

let wrapper;

const $route = {
  params: {
    id: 0
  }
};

const $log = {
  debug() {
  }
};

const $refs = {
  filepicker: {
    click() {
    }
  }
}

jest.mock('../../Api');

beforeEach(() => {

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(productDetailCard, {
    localVue,
    propsData: {
      cancelAction: () => {
      },
      product: {
        images: [],
        created: "blah"
      }
    },
    mocks: {$route, $log, $refs},
    methods: {},
    stubs: {
      // Stub out modal component, as the actual component doesn't play nice with vue test utils
      'b-modal': {
        render: () => {},
        methods: {
          show: () => {}
        }
      }
    }
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Testing delete image when creating product', () => {

  it('Successfully delete a product image when only one exists', async () => {
    wrapper.vm.productCard.created = null;  // So the product 'doesn't exist'

    wrapper.vm.productCard.images = [{id: 1, fileName: 'blah'}]
    await wrapper.vm.openDeleteConfirmDialog(1);
    await wrapper.vm.confirmDeleteImage();

    expect(wrapper.vm.productCard.images).toStrictEqual([]);
    expect(Api.deleteImage).not.toHaveBeenCalled();
  });

  it('Successfully delete a product image when multiple exists', async () => {
    wrapper.vm.productCard.created = null;  // So the product 'doesn't exist'

    wrapper.vm.productCard.images = [{id: 1, fileName: 'blah'}, {imageId: 2, fileName: 'blah2'}]
    await wrapper.vm.openDeleteConfirmDialog(1);
    await wrapper.vm.confirmDeleteImage();

    expect(wrapper.vm.productCard.images).toStrictEqual([{imageId: 2, fileName: 'blah2'}]);
    expect(Api.deleteImage).not.toHaveBeenCalled();
  });

});


describe('Testing upload image when product already exists', () => {

  it('Successfully create a product image', async () => {
    await wrapper.vm.onFileChange({target: {files: [{filename: 'blah'}]}});

    expect(Api.uploadProductImage).toHaveBeenCalled();
    expect(wrapper.vm.imageError).toBe("");
  });

  it('Create a product image but error returned', async () => {
    Api.uploadProductImage.mockRejectedValue({response: {status: 419, data: {message: "The file that you tried to upload is too large. Files must be 5MB in size or less."}}});
    await wrapper.vm.onFileChange({target: {files: [{filename: 'blah'}]}});

    expect(wrapper.vm.imageError).toBe("The file that you tried to upload is too large. Files must be 5MB in size or less.");
  });

});


describe('Testing confirmation box', () => {

  it('Does not delete a product image when not confirmed', async () => {

    wrapper.vm.productCard.images = [{imageId: 1, fileName: 'blah'}]
    await wrapper.vm.openDeleteConfirmDialog(1);

    expect(wrapper.vm.productCard.images).toStrictEqual([{imageId: 1, fileName: 'blah'}]);
    expect(Api.deleteImage).not.toHaveBeenCalled();
  });

});

describe('Testing api call delete image request', () => {

  it('Successfully delete a product image ', async () => {
    Api.deleteImage.mockResolvedValue({response: {status: 200}});

    await wrapper.vm.openDeleteConfirmDialog(1);
    await wrapper.vm.confirmDeleteImage();

    expect(wrapper.vm.imageError).toStrictEqual("");
    expect(Api.deleteImage).toHaveBeenCalled();
  });


  it('error test to delete a product image ', async () => {
    Api.deleteImage.mockRejectedValue({response: {status: 406, statusText: 'Access a resource by an ID that does not exist.'}});

    await wrapper.vm.openDeleteConfirmDialog(1);
    await wrapper.vm.confirmDeleteImage();

    expect(wrapper.vm.imageError).toStrictEqual('Access a resource by an ID that does not exist.');
    expect(Api.deleteImage).toHaveBeenCalled();
  });

});

describe('Set primary image', () => {

  it('Successfully sets primary image for product to be created', async () => {
    wrapper.vm.productCard.created = null;  // So the product 'doesn't exist'
    await wrapper.vm.setPrimaryImage({id: 'blah', filename: 'blah'});

    expect(Api.setPrimaryImage).not.toHaveBeenCalled();
    expect(wrapper.emitted()).not.toHaveProperty('imageChange');
    expect(wrapper.vm.productCard.primaryImage).toStrictEqual({id: 'blah', filename: 'blah'});
  });

  it('Successfully sets primary image for product that already exists', async () => {
    await wrapper.vm.setPrimaryImage({id: 'blah', filename: 'blah'});

    expect(Api.setPrimaryImage).toHaveBeenCalled();
    expect(wrapper.emitted().imageChange).toBeTruthy();
    expect(wrapper.vm.productCard.primaryImage).toStrictEqual({id: 'blah', filename: 'blah'});
  });
});
