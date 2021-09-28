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
    expect(wrapper.vm.otherUserId).toEqual(3);

    wrapper.vm.clickedChatHead(mockEvent, 4);
    expect(wrapper.vm.sendToUserId).toEqual(4)
    expect(wrapper.vm.otherUserId).toEqual(4);
  })

  test('Api call is made when send message called', async () => {
    wrapper.vm.messageText = "something";
    wrapper.vm.sendToUserId = 2;
    await wrapper.vm.sendMessage();
    expect(Api.postMessage).toHaveBeenCalledWith({cardId:1, messageText:"something", receiverId: 2});
  })

});

describe('Check if setCurrentMessages works as intended.', () => {
  test('Test that setCurrentMessages works with only one conversation', () => {
    wrapper.vm.conversations = [
      {
        otherUser: {
          id: 1,
        },
        messages: ["Test", "Data"]
      },
    ];
    wrapper.vm.otherUserId = 1;
    wrapper.vm.setCurrentMessages();
    expect(wrapper.vm.current_displayed_messages).toEqual(["Test", "Data"])
  });

  test('Test that setCurrentMessages works with multiple conversations', () => {
    wrapper.vm.conversations = [
      {
        otherUser: {
          id: 1,
        },
        messages: ["Test", "Data"]
      },
      {
        otherUser: {
          id: 2,
        },
        messages: ["James", "Phil"]
      },
      {
        otherUser: {
          id: 3,
        },
        messages: ["Scott", "Nitish"]
      },
      {
        otherUser: {
          id: 10,
        },
        messages: ["Eric", "Caleb"]
      },
      {
        otherUser: {
          id: 12,
        },
        messages: ["Oliver", "Arish"]
      },
    ];
    wrapper.vm.otherUserId = 2;
    wrapper.vm.setCurrentMessages();
    expect(wrapper.vm.current_displayed_messages).toEqual(["James", "Phil"])
  });

  test('Test that setCurrentMessages works with multiple same ids (shouldnt occur in the wild) and returns first matching messages', () => {
    wrapper.vm.conversations = [
      {
        otherUser: {
          id: 1,
        },
        messages: ["Test", "Data"]
      },
      {
        otherUser: {
          id: 2,
        },
        messages: ["James", "Phil"]
      },
      {
        otherUser: {
          id: 3,
        },
        messages: ["Scott", "Nitish"]
      },
      {
        otherUser: {
          id: 3,
        },
        messages: ["Eric", "Caleb"]
      },
      {
        otherUser: {
          id: 12,
        },
        messages: ["Oliver", "Arish"]
      },
    ];
    wrapper.vm.otherUserId = 3;
    wrapper.vm.setCurrentMessages();
    expect(wrapper.vm.current_displayed_messages).toEqual(["Scott", "Nitish"])
  })
});
