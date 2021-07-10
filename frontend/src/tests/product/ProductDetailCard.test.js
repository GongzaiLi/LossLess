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

  // Api.getBusiness.mockResolvedValue({data: {name: "Blah"}});

  const localVue = createLocalVue()
  localVue.use(BootstrapVue);
  localVue.use(BootstrapVueIcons);

  wrapper = shallowMount(productDetailCard, {
    localVue,
    propsData: {
      cancelAction: () => {
      },
      product: {
        images: []
      }
    },
    mocks: {$route, $log, $refs},
    methods: {},
  });
});

afterEach(() => {
  wrapper.destroy();
});


describe('Testing api delete image request', () => {

  it('Successfully delete a product image ', async () => {
    Api.deleteImage.mockResolvedValue({response: {status: 200}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.deleteImage(mockEvent);

    expect(wrapper.vm.imageError).toBe("");
    expect(Api.deleteImage).toHaveBeenCalled();
  });


  it('error test to delete a product image ', async () => {
    Api.deleteImage.mockRejectedValue({response: {status: 406, statusText: 'Access a resource by an ID that does not exist.'}});

    const mockEvent = {preventDefault: jest.fn()}
    await wrapper.vm.deleteImage(mockEvent);

    expect(wrapper.vm.imageError).toBe('Access a resource by an ID that does not exist.');
    expect(Api.deleteImage).toHaveBeenCalled();
  });


});
