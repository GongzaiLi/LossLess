<template>
  <b-card class="notification-card" @click="markRead">
    <b-card-body class="notification-body">
      <div v-if="updatedNotification.tag" :key="updatedNotification.tag" class="tag-bar"> <!-- Key makes div refresh on tag color change -->
        <NotificationTag :tag-color=updatedNotification.tag style="height: 100%"/>
      </div>
      <div class="notification">
        <div v-if="!updatedNotification.read">
          <b-row>
            <b-col>
             <span class="unreadLabel">
             <b-icon-envelope-fill> </b-icon-envelope-fill> New Notification</span>
            </b-col>
          </b-row>
          <hr class="unreadHr">
        </div>
        <b-row>
        <b-col class="pt-1" cols="auto">
          <h6>
          <b-icon-star-fill class="mr-2 star-icon "  v-if="updatedNotification.starred"/>
          <b-icon-exclamation-triangle v-if="updatedNotification.type==='Liked Listing Purchased'"/>
          <b-icon-heart v-else-if="updatedNotification.type==='Liked Listing'"/>
          <b-icon-x-circle v-else-if="updatedNotification.type==='Unliked Listing'" />
          <b-icon-clock-fill v-else-if="updatedNotification.type==='Marketplace Card About To Expire'"/>
          <b-icon-clock-history v-else-if="updatedNotification.type==='Expired Marketplace Card'"/>
          <b-icon-cart  v-else-if="updatedNotification.type==='Purchased listing'"/>
          <b-icon-globe v-else-if="updatedNotification.type==='User Currency Changed'"/>
          <b-icon-globe v-else-if="updatedNotification.type==='Business Currency Changed'"/>
          <b-icon-chat-square-dots-fill v-else-if="updatedNotification.type==='Message Received'"/>
            {{updatedNotification.type}}
          </h6>
        </b-col>
          <b-col v-if="updatedNotification.type==='Message Received'" cols="3" class="pt-1">
            <b-button v-if="updatedNotification.type==='Message Received'" size="sm" variant="primary" @click="replyToMessage">
              <b-icon-chat-quote-fill/> Reply
            </b-button>
          </b-col>
        <b-col v-else-if="updatedNotification.price" cols="4" class="pt-1">
          <h6> {{updatedNotification.price}} </h6>
        </b-col>
        <b-col cols="1" class="ml-auto">
          <b-dropdown variant="none" right no-caret class="float-right" v-if="!this.inNavbar && !this.archivedSelected">
            <template #button-content>
              <b-icon-tag-fill title="Add tag to notification"
                               @mouseover="isHoveringTagButton = true"
                               @mouseout="isHoveringTagButton = false"
                               :class="{hoveringTagButton: isHoveringTagButton}"
                               class="tag-button float-right" />
            </template>
            <b-dropdown-header>
              <h6>Tag Notification</h6>
            </b-dropdown-header>
            <b-dropdown-item v-for="tagColor in tagColors" :key="tagColor" @click="setNotificationTagColor(tagColor)">
              <NotificationTag :tag-color=tagColor class="tag" :tag-style-prop="{height: '1.5rem', width: '100%'}"></NotificationTag>
            </b-dropdown-item>
            <b-dropdown-item v-if="updatedNotification.tag" @click="setNotificationTagColor('remove')">
              <P><b-icon-x-circle-fill class="remove-tag"/> Remove Tag</p>
            </b-dropdown-item>
          </b-dropdown>
        </b-col>
        <b-col cols="1">
            <b-dropdown right no-caret variant="link" class="float-right" v-if="!this.inNavbar">
              <template #button-content>
                <div>
                  <b-icon-three-dots-vertical class="three-dot float-right " ></b-icon-three-dots-vertical>
                </div>
              </template>
              <b-dropdown-item v-if="!archivedSelected" @click="starNotification">
                <p ><b-icon-star-fill v-if="updatedNotification.starred" title="Mark this notification as Important" class="star-icon"></b-icon-star-fill>
                <b-icon-star title="Remove this notification as Important" class="star-icon"  v-else></b-icon-star>   Important</p>
              </b-dropdown-item>
              <b-dropdown-item @click="confirmArchive">
                <p v-if="!archivedSelected">
                  <b-icon-archive  class="archive-button" variant="outline-success" title="Archive this notification"></b-icon-archive>  Archive </p>
                <p v-else>
                  <b-icon-archive  class="archive-button" variant="outline-success" title="Un-Archive this notification"></b-icon-archive>  Un-Archive </p>
              </b-dropdown-item>
              <b-dropdown-item @click="confirmDelete">
                <p><b-icon-trash class="delete-button" title="Delete this notification"></b-icon-trash>  Delete</p>
              </b-dropdown-item>
            </b-dropdown>
        </b-col>
      </b-row>
      <hr class="mt-1 mb-2">
        <div v-if="updatedNotification.type === 'Liked Listing' || updatedNotification.type === 'Unliked Listing'">
          <a @click="goToListing" class="listing-link">{{ updatedNotification.message }}</a>
        </div>
        <div v-else>
          <span >{{ updatedNotification.message }}</span>
        </div>

            <b-row>
              <b-col cols="11">
                <h6 v-if="updatedNotification.location"> Location: {{updatedNotification.location}} </h6>

                <div class="mt-2" v-else-if="updatedNotification.type==='Marketplace Card About To Expire'">
                  <b-button variant="primary" @click="replyToMessage">Open Marketplace Card</b-button>
                </div>
              </b-col>
              <b-col v-if="updatedNotification.read"  cols="1">
                <span class="readLabel">
                 <b-icon-check2-all> </b-icon-check2-all> </span>
              </b-col>
            </b-row>

          <b-modal ref="confirmDeleteModal" size="sm"
                   title="Delete Notification"
                   ok-variant="danger"
                   ok-title="Delete"
                   @ok="notificationDeleted">
            Are you sure you want to <strong>delete</strong> this notification?
          </b-modal>

        <b-modal ref="confirmArchiveModal" size="sm"
                 :title="this.archivedSelected ? 'Un-Archive Notification' :'Archive Notification'"
                 ok-variant="success"
                 :ok-title="this.archivedSelected ? 'Un-Archive' :'Archive'"
                 @ok="archiveNotification">
          Are you sure you want to <strong>{{this.archivedSelected ? 'un-archive' :'archive'}}</strong> this notification?
        </b-modal>
        <b-modal :id="`full-card-${this.updatedNotification.id}`" size="lg" hide-header hide-footer>
          <MarketplaceCardFull
              :cardId = "updatedNotification.subjectId"
              :openedFromNotifications = "notification.senderId"
              v-on:closeModal="closeFullCardModal"
          />
      </b-modal>
      </div>
    </b-card-body>
  </b-card>
</template>


<style scoped>

.notification-card {
  padding: 0 !important;
}

.notification-body {
  padding: 0 !important;
  display: flex;
}

.tag-bar {
  flex: 1;
}

.notification {
  flex: 10;
}

.readLabel {
  float: right;
  color: green;

}

hr.unreadHr {
  margin-top: 0.3rem;
  margin-bottom: 0.5rem;
  border-top: 1px solid orangered;
}

span.unreadLabel {
  float: left;
  color: orangered;
}

.star-icon {
  color: dodgerblue;
}

.archive-button {
  color: green;
}

.delete-button {
  color: red;
}

.tag-button {
  color: black;
}

.tag {
  padding-top: 2px;
  padding-bottom: -2px;
}

.remove-tag {
  color: red;
}

.three-dot {
  margin-right: -10px;
  padding-right: 0;
  color: black;
}

.listing-link:hover {
  color: dodgerblue;
  text-decoration: underline;
  cursor: pointer;
}

.hoveringTagButton{
  color: red;
}

@media (max-width: 500px) {
  .notification-card>div {
    padding: 0.5rem;
  }
}


</style>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus";
import {formatAddress} from "../../util";
import NotificationTag from "../../components/model/NotificationTag";
import MarketplaceCardFull from "../marketplace/MarketplaceCardFull";

export default {
  name: "Notification",
  components: {NotificationTag, MarketplaceCardFull},
  props: ['notification', 'inNavbar', 'archivedSelected','deleted'],
  data() {
    return {
      updatedNotification: {message:"", type:"", read: this.notification.read},
      tagColors: ["RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "PURPLE", "BLACK"],
      isHoveringTagButton: false,
    }
  },

  methods: {
    /**
     * Update the
     * @param tagColor  The tag color to set the tag to, or the string 'remove' to remove a tag.
     */
    async setNotificationTagColor(tagColor) {
      if (tagColor === "remove") {
        this.updatedNotification.tag = null;
        await Api.patchNotification(this.updatedNotification.id, {"tag": null})
      } else {
        this.updatedNotification.tag = tagColor;
        await Api.patchNotification(this.updatedNotification.id, {"tag": tagColor})
      }
      this.$emit('tagColorChanged');
    },

    /**
     * Closes the full card modal when cancel button pressed.
     */
    closeFullCardModal() {
      this.$bvModal.hide(`full-card-${this.updatedNotification.id}`);
    },

    /**
     * Updates the purchase listing notification with the product data
     * @param notification the purchase listing notification
     * @return notification the same notification updated
     */
    async updatePurchasedNotifications(notification) {
      const purchasedListing = (await Api.getPurchaseListing(notification.subjectId)).data
      const address = purchasedListing.business.address;
      notification.location = formatAddress(address, 2);
      const currency = await Api.getUserCurrency(address.country);
      notification.price = currency.symbol + purchasedListing.price + " " + currency.code
      notification.message = `${purchasedListing.quantity} x ${purchasedListing.product.name}`
      return notification
    },

    /**
     * when called it checks if the notification is currently liked
     * then sends a toggled api request based on this
     */
    async starNotification() {
      if(this.updatedNotification.starred) {
        this.updatedNotification.starred = false
        await Api.patchNotification(this.updatedNotification.id, {"starred": false})
      } else {
        this.updatedNotification.starred = true
        await Api.patchNotification(this.updatedNotification.id, {"starred": true})
      }
      EventBus.$emit("notificationUpdate")
    },

    /**
     * Performs an action based on the notification that has been clicked.
     * When a liked or unliked listing is clicked it routes you to that listing
     */
    goToListing() {
      this.markRead();  // For some reason, when we click the link and redirect to a different page, it's not always marked as read so this gaurantees it.
      if (this.updatedNotification.type === 'Liked Listing' || this.updatedNotification.type === 'Unliked Listing') {
        if (this.$route.fullPath !== '/listings/' + this.updatedNotification.subjectId) {
          this.$router.push('/listings/' + this.updatedNotification.subjectId);
        }
      }
    },

    /**
     * Shows the modal of the card related to the clicked notification.
     * This also sends the information from the notification to open the chat up to who sent the message     *
     */
    replyToMessage(){
        this.$bvModal.show(`full-card-${this.updatedNotification.id}`);
    },

    /**
     * Emits to parent deleteNotification with Id of selected notification
     */
    notificationDeleted(){
      this.$emit('deleteNotification',this.updatedNotification.id,this.updatedNotification.type)
    },

    /**
     * Shows a dialog to confirm archiving the notification.
     * USES REFS NOT ID TO PREVENT DUPLICATION!
     */
    async confirmArchive() {
      this.$refs.confirmArchiveModal.show();
    },

    /**
     * Calls API archiveNotification patch request
     * and using an EventBus that emits notificationUpdate so that
     * other components are refreshed.
     */
    async archiveNotification() {
      await Api.patchNotification(this.updatedNotification.id, {"archived": !this.archivedSelected})
      EventBus.$emit("notificationUpdate")
    },

    /**
     * Shows a dialog to confirm deleting the notification.
     * USES REFS NOT ID TO PREVENT DUPLICATION!
     */
    async confirmDelete() {
      this.$refs.confirmDeleteModal.show();
    },

    /**
     * Marks a notification as read and makes th api call.
     * @param notification the notification that has been clicked
     */
    async markRead() {
      if (!this.updatedNotification.read) {
        this.updatedNotification.read = true;
        await Api.patchNotification(this.updatedNotification.id, {"read": true});

        EventBus.$emit('notificationClicked', this.updatedNotification);
      }
    },
  },
  async mounted() {
    if (this.notification.type === "Purchased listing") {
      this.updatedNotification = await this.updatePurchasedNotifications(this.notification)
    } else {
      this.updatedNotification = this.notification
    }

    if (this.updatedNotification.type==="Message Received"){
      let subjectArray = this.updatedNotification.subjectId.split(",");
      this.updatedNotification.subjectId=subjectArray[0];
      this.updatedNotification.senderId=subjectArray[1];
    }
  },
}
</script>
