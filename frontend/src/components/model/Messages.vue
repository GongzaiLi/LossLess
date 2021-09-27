<template>
  <div>
    <b-row no-gutters>
      <b-col lg="3" sm="12" v-if="isCardCreator">
        <b-list-group class="chat-list">
          <b-list-group-item class="chat-head" v-for="conversation in conversations" :key=conversation.id
                             @click="clickedChatHead($event, conversation.otherUser.id)">
            <b-img class="rounded-circle avatar" width="30" height="30" :alt="conversation.otherUser.profileImage"
                   :src="require('../../../public/profile-default.jpg')"/>
            {{ conversation.otherUser.firstName }}
          </b-list-group-item>
        </b-list-group>
      </b-col>
      <b-col :lg="isCardCreator?9:12" :key="timesMessagesUpdates">
        <div class="message-box">
          <b-card v-for="message in current_displayed_messages" :key="message.id" class="message-card">
            <b-card-text :class="message.senderId === currentUserId ? 'speech-bubble-right': 'speech-bubble-left' ">
              {{ message.messageText }}
            </b-card-text>
          </b-card>

        </div>
        <b-form @submit.prevent="sendMessage">
          <b-input-group>
            <b-form-textarea
                required
                maxlength=250 max-rows="2"
                no-resize
                type="text" class="messageInputBox"
                placeholder="Type Message..."
                v-model="messageText"
            >
            </b-form-textarea>
            <b-input-group-append>
              <b-button type="submit" :disabled="targetChatHead == null && isCardCreator" variant="primary"> Send </b-button>
            </b-input-group-append>
          </b-input-group>
        </b-form>
      </b-col>
    </b-row>
  </div>
</template>

<style scoped>
.chat-list {
  height: 7rem;
  max-height: 15rem;
  overflow-y: auto;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.125);
}

.message-box {
  height: 11rem;
  overflow-y: auto;
}

.messageInputBox {
  height: 4rem;
  max-width: 100%;
  float: left;
  bottom: 0;
}

@media (min-width: 992px) {
  .chat-list {
    height: 15rem;
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

div.chat-head:last-child {
  border-bottom: none;
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


</style>

<script>

import Api from "../../Api";

export default {
  props: ['isCardCreator', 'cardId', 'cardCreatorId'],
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
      current_displayed_messages: []
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
      if (this.targetChatHead) {
        this.targetChatHead.classList.remove('active');
      }
      this.targetChatHead = event.currentTarget;
      this.targetChatHead.classList.add('active');

      this.sendToUserId = userId;
      this.otherUserId = userId;

      this.setCurrentMessages()
    },

    /**
     * Find the messages for the conversation with the current otherUser (selected chat head or card owner)
     */
    setCurrentMessages() {
      const correctConversation = this.conversations.find(c => this.otherUserId === c.otherUser.id);
      this.current_displayed_messages = correctConversation.messages;
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
          .then(async (res) => {
            this.$log.debug(res.data);
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
            } else {
              this.conversations = [];
              this.conversations.push(res.data);
              this.otherUserId = this.conversations[0].otherUser.id;
              this.setCurrentMessages();
            }
          }).catch(err => {
            console.error(err);
          });
    },
  },


  mounted() {
    this.currentUserId = this.$currentUser.id;
    this.getMessages()
  }
}
</script>
