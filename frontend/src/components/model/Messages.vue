<template>
  <div>
    <b-row no-gutters>
      <b-button v-if="!refreshingMessages" variant="primary" @click="refreshMessages" title="Refresh Messages" style="margin-bottom: 5px">
        <b-icon-arrow-counterclockwise/> Refresh Messages
      </b-button>
      <b-button v-else variant="primary"  title="Refreshing Messages" style="margin-bottom: 5px">
        <b-icon-arrow-counterclockwise animation="spin-reverse"/> Refreshing...
      </b-button>
    </b-row>
    <b-row no-gutters>
      <b-col lg="3" sm="12" v-if="isCardCreator">
        <b-list-group class="chat-list">
          <b-list-group-item class="chat-head" v-for="conversation in conversations" :key=conversation.id :active="otherUserId===conversation.otherUser.id"
                             @click="clickedChatHead($event, conversation.otherUser.id)">
            <b-img class="rounded-circle avatar" width="30" height="30" :alt="conversation.otherUser.profileImage"
                   :src="require('../../../public/user-profile-default.png')"/>
            {{ conversation.otherUser.firstName }}
          </b-list-group-item>
        </b-list-group>
      </b-col>
      <b-col :lg="isCardCreator?9:12" :key="timesMessagesUpdates">
        <div class="message-box" ref="container">
          <b-card v-for="message in current_displayed_messages" :key="message.id" class="message-card">
            <b-card-text :class="message.senderId === currentUserId ? 'speech-bubble-right': 'speech-bubble-left'">
              {{ message.messageText }}
            </b-card-text>
          </b-card>
          <div ref="below-messages" />
        </div>
        <b-form>
        <b-input-group>
            <b-form-textarea required no-resize maxlength="250" max-rows="2"
                             type="text" class="messageInputBox sendMessageGroup"
                             placeholder="Type Message..." v-model="messageText">
            </b-form-textarea>

            <b-input-group-append>
              <b-button v-if="messageText"  class="clearButton" @click="messageText=''">
                <b-icon icon="X" variant="dark"/>
              </b-button>
              <b-button v-else disabled class="clearButton">
                <b-icon icon="X" variant="dark"/>
              </b-button>
            </b-input-group-append>
            <b-input-group-append class="ml-2">
              <b-button v-if="messageText" class="sendMessageGroup" variant="primary" @click="sendMessage"> Send</b-button>
              <b-button v-else disabled class="sendMessageGroup" variant="primary"> Send</b-button>
            </b-input-group-append>
          </b-input-group>
        </b-form>
      </b-col>
    </b-row>
  </div>
</template>

<style scoped>

.clearButton {
  background-color: lightgrey;
  border-color: lightgrey;
}

.chat-list {
  height: 7rem;
  max-height: 18rem;
  overflow-y: auto;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.125);
}

.message-box {
  height: 14rem;
  border-bottom-left-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
  border-bottom: 0;
  overflow-y: auto;
}

.sendMessageGroup {
  border-top-left-radius: 0 !important;
  border-top-right-radius: 0 !important;
}


.messageInputBox {
  height: 4rem;
  max-width: 100%;
  float: left;
  bottom: 0;
  border-top-left-radius: 0 !important;
  border-top-right-radius: 0 !important;
}

@media (min-width: 992px) {
  .chat-list {
    height: 18rem;
  }
}

.avatar {
  vertical-align: middle;
}

.chat-head:hover {
  background-color: rgba(0, 0, 255, 1);
  color: white;
}

div.chat-head {
  border-left: none;
}

div.chat-head:first-child {
  border-top: none;
}


.speech-bubble-left {
  float: left;
  padding: 10px 20px;
  background: pink;
  border-radius: 6px;
  text-align: left;
  max-width: 60%;
  margin-bottom: 0;
}

.speech-bubble-right {
  float: right;
  padding: 10px 20px;
  background: #00c8e2;
  border-radius: 6px;
  text-align: right;
  max-width: 60%;
  margin-bottom: 0;
}

.message-card {
  border: none;
  padding: 0;
  margin: -20px 0;
}

.refresh-div {
  background-color: #00c8e2;
  border-radius: 20px;
}


</style>

<script>

import Api from "../../Api";

export default {
  props: ['isCardCreator', 'cardId', 'cardCreatorId', 'notificationSenderId'],
  name: "Messages",
  data() {
    return {
      targetChatHead: null,
      messageText: '',
      sendToUserId: null,
      otherUserId: null,
      currentUserId: null,
      conversations: [],
      timesMessagesUpdates: 0,
      current_displayed_messages: [],
      InitialLoad:true,
      snapToBottom: true,
      refreshingMessages: false
    }
  },
  methods: {

    /**
     * When a chat-head is clicked it adds an active class to the clicked list-group-item and removes it from
     * the list group item that has the old active tag.
     *
     * https://stackoverflow.com/questions/40153194/how-to-remove-class-from-siblings-of-an-element-without-jquery
     * @param event
     * @param userId
     */
    clickedChatHead(event, userId) {
      this.sendToUserId = userId;
      this.otherUserId = userId;
      this.setCurrentMessages();
      this.$nextTick(() => {
        this.snapToBottom = true;
        this.scrollToBottomOfMessages();
      });
    },

    /**
     * Find the messages for the conversation with the current otherUser (selected chat head or card owner)
     */
    setCurrentMessages() {
      const correctConversation = this.conversations.find(c => this.otherUserId === c.otherUser.id);
      if(correctConversation) {
        this.current_displayed_messages = correctConversation.messages;
      }
    },

    /**
     *  Creates a message object and sends as body in the api request when send button clicked.
     */
    async sendMessage() {
      if (!this.sendToUserId) {
        this.sendToUserId = this.cardCreatorId
      }

      const message = {
        cardId: this.cardId,
        receiverId: this.sendToUserId,
        messageText: this.messageText
      }
      await Api.postMessage(message)
          .then(async () => {
            this.messageText = '';
            await this.getMessages();
            this.setCurrentMessages();
          })
          .catch(err => {
            this.$log.debug(err);
          })
    },

    /**
     * Get messages from the backend for a user
     * Set conversation data depending on whether user owns card or not.
     */
    async getMessages() {
      await Api.getMessages(this.cardId)
          .then(res => {
            if (this.isCardCreator) {
              this.conversations = res.data
              if (this.notificationSenderId&&this.InitialLoad){
                this.otherUserId = parseInt(this.notificationSenderId)
                this.sendToUserId = parseInt(this.notificationSenderId)
                this.InitialLoad=false
              }
              else if (this.conversations.length>0&&this.InitialLoad) {
                this.otherUserId = this.conversations[0].messages[0].senderId
                this.sendToUserId = this.conversations[0].messages[0].senderId
                this.InitialLoad=false
              }
              this.setCurrentMessages();
            } else {
              this.conversations = [];
              this.conversations.push(res.data);
              this.otherUserId = this.conversations[0].otherUser.id;
              this.sendToUserId =  this.cardCreatorId
              this.setCurrentMessages();
            }
          }).catch(err => {
            console.error(err);
          });
      this.scrollToBottomOfMessages();
    },

    /**
     * Used for when refreshing messages button is clicked.
     * Get messages and set refreshing messages so the animation can play.
     */
    async refreshMessages() {
      this.refreshingMessages = true;
      setTimeout(() => {
        this.refreshingMessages = false;
      }, 2000);
      await this.getMessages();
    },

    /**
     * Scrolls the view to the bottom of the scroll bar
     * Used in messages so the last message is always shown
     *
     * If snapToBottom is true, snaps instantly. (Initial load and when changing chat heads)
     */
    scrollToBottomOfMessages() {
      if (this.snapToBottom) {
        const container = this.$refs.container;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
        this.snapToBottom = false
      } else {
        const container = this.$refs['below-messages'];
        if (container) {
          container.scrollIntoView({behavior: "smooth"});
        }
      }
    }
  },

  async mounted() {
    this.currentUserId = this.$currentUser.id;
    await this.getMessages();
  }
}
</script>
