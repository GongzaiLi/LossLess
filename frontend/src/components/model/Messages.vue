<template>
  <div>
    <b-row no-gutters>
      <b-col lg="3" sm="12">
        <b-list-group class="chat-list">
          <b-list-group-item class="chat-head" v-for="item in conversations" :key=item.userId @click="clickedChatHead">
            <b-img class="rounded-circle avatar" width="30" height="30" :alt="item.userName" :src="require('../../../public/profile-default.jpg')" />
            {{item.userName}}
          </b-list-group-item>
        </b-list-group>
      </b-col>
      <b-col lg="9">
        <b-card class="message-box"></b-card>
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
  height: 15rem;
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

export default {
  props: [],
  name: "Messages.vue",
  data() {
    return {
      targetChatHead: null,
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
    clickedChatHead(event) {
      if (this.targetChatHead) {
        this.targetChatHead.classList.remove('active')
      }
      this.targetChatHead = event.currentTarget
      this.targetChatHead.classList.add('active')
    },
  },
}
</script>