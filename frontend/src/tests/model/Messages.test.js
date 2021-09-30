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

  Api.postMessage.mockResolvedValue({data: { "messageId": 1}});
  Api.getMessages.mockResolvedValue({data: { cardId: 1, cardOwner: {}, otherUser: {}, messages: [] }});
  Api.getImage = (imageName) => {
    return `localhost:9499/images?filename=${imageName}`
  }
  window.HTMLElement.prototype.scrollIntoView = jest.fn();
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
    expect(wrapper.vm.currentConversation.messages).toEqual(["Test", "Data"])
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
    expect(wrapper.vm.currentConversation.messages).toEqual(["James", "Phil"])
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
    expect(wrapper.vm.currentConversation.messages).toEqual(["Scott", "Nitish"])
  })
});


describe('Check user thumbnails.', () => {
  test('Gets real image url if image exists', () => {
    expect(wrapper.vm.getUserThumbnailUrl({
      profileImage: {
        thumbnailFilename: "thumb.png",
        fileName: "file.png",
      }
    })).toEqual("localhost:9499/images?filename=thumb.png")
  });

  test('Gets default image url if no image exists', () => {
    expect(wrapper.vm.getUserThumbnailUrl({
      profileImage: null
    })).toEqual("profile-default.jpg")
  });
});


describe('Check user objects.', () => {
  beforeEach(() => {
    wrapper.vm.currentConversation = {
        otherUser: {
          id: 1,
          name: "Caleb"
        },
        cardOwner: {
          id: 4,
          name: "Nitish"
        },
        messages: ["Test", "Data"]
      };
  })

  test('Gets card owner if sender is owner', () => {
    expect(wrapper.vm.getUserObject(1)).toEqual({
      id: 1,
      name: "Caleb"
    });
  });

  test('Gets other user if sender is not card owner', () => {
    expect(wrapper.vm.getUserObject(4)).toEqual({
      id: 4,
      name: "Nitish"
    });
  });
});