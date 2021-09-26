import {shallowMount, createLocalVue, config} from '@vue/test-utils';
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue';
import Api from "../../Api";
import Messages from "../../components/model/Messages";

let wrapper;
config.showDeprecationWarnings = false  //to disable deprecation warnings

jest.mock('../../Api');

const $log = {
  debug: jest.fn(),
};

let $currentUser = {
  id: 1,
  dateOfBirth: '01/01/2001',
  currentlyActingAs: null
}
const localVue = createLocalVue()
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);


beforeEach(() => {

  Api.postMessage.mockResolvedValue({data: {}});

  wrapper = shallowMount(Messages, {
    localVue,
    propsData: {isCardCreator: false, cardId: 1},
    mocks: {$log, $currentUser},
    stubs: {},
    methods: {},
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('Messages', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });
});

describe('Check if message can be sent to selected user', () => {

  test('Id of user to send message to changes when selected from the list', () => {

    const targetChatHead = { classList: {remove: jest.fn(), add: jest.fn() } };
    wrapper.vm.targetChatHead = targetChatHead
    const mockEvent = {currentTarget: targetChatHead}
    wrapper.vm.clickedChatHead(mockEvent, 3);
    expect(wrapper.vm.sendToUserId).toEqual(3)

    wrapper.vm.clickedChatHead(mockEvent, 4);
    expect(wrapper.vm.sendToUserId).toEqual(4)
  })

  test('Api call is made when send message called', async () => {
    wrapper.vm.messageText = "something"
    wrapper.vm.sendToUserId = 2
    await wrapper.vm.sendMessage();
    expect(Api.postMessage).toHaveBeenCalledWith({cardId:1, messageText:"something", receiverId: 2})
  })

});
