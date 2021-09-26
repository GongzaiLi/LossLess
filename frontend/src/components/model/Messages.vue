<template>
  <div>
    <b-row no-gutters>
      <b-col lg="3" sm="12" v-if="isCardCreator">
        <b-list-group class="chat-list">
          <b-list-group-item class="chat-head" v-for="item in conversations" :key=item.userId @click="clickedChatHead($event, item.userId)">
            <b-img class="rounded-circle avatar" width="30" height="30" :alt="item.userName" :src="require('../../../public/profile-default.jpg')" />
            {{item.userName}}
          </b-list-group-item>
        </b-list-group>
      </b-col>
      <b-col :lg="isCardCreator?9:12">
        <b-card class="message-box">
        </b-card>
        <b-form v-if="sendToUserId && sendToUserId !== myId" @submit.prevent="sendMessage">
        <b-input-group >
          <b-form-textarea
              required
              maxlength=250 max-rows="2"
              no-resize
              type="text" class="messageInputBox"
              placeholder="Type Message..."
              v-model="messageText"> Enter message </b-form-textarea>
          <b-input-group-append>
            <b-button type="submit" variant="primary"> Send </b-button>
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
</style>

<script>

import Api from "../../Api";

export default {
  props: ['isCardCreator', 'cardId'],
  name: "Messages",
  data() {
    return {
      targetChatHead: null,
      messageText: '',
      sendToUserId: null,
      messageRequired: false,
      myId: null,
      conversations: [
        {
          userId: 0,
          userName: "James",
          possibleOtherUserInfo: "You decide these fields",
        },
        {
          userId: 1,
          userName: "Phil",
          possibleOtherUserInfo: "You decide these fields",
        },
        {
          userId: 2,
          userName: "Joseph",
          possibleOtherUserInfo: "You decide these fields",
        },
        {
          userId: 3,
          userName: "John",
          possibleOtherUserInfo: "You decide these fields",
        },
        {
          userId: 4,
          userName: "Chris",
          possibleOtherUserInfo: "You decide these fields",
        },
        {
          userId: 5,
          userName: "Mickey",
          possibleOtherUserInfo: "You decide these fields",
        },
      ]
    }
  },
  methods: {

    /**
     * When a chat-head is clicked it adds an active class to the clicked list-group-item and removes it from
     * the list group item that has the old active tag.
     *
     * https://stackoverflow.com/questions/40153194/how-to-remove-class-from-siblings-of-an-element-without-jquery
     * @param event
     */
    clickedChatHead(event, userId) {
      if (this.targetChatHead) {
        this.targetChatHead.classList.remove('active');
      }
      this.targetChatHead = event.currentTarget;
      this.targetChatHead.classList.add('active');
      this.sendToUserId = userId;
    },

    /**
     *  Creates a message object and sends as body in the api request when send button clicked.
     */
    sendMessage() {
      const message = { cardId: this.cardId,
                        receiverId: this.sendToUserId,
                        messageText: this.messageText
                      }
      Api.postMessage(message)
      .then(res => {
        this.$log.debug(res.data);
      })
      .catch(err => {
        this.$log.debug(err);
      })
    },

  },

  mounted() {
    this.myId = this.$currentUser.id;
  }
}
</script>
