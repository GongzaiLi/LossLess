<template>
  <div>
      <b-card class=profile-card>
        <div>
          <h1><strong> {{ fullCard.title }} </strong>
            <b-icon-x class="float-right close" @click="closeFullViewCardModal">
            </b-icon-x></h1>
          <b-container>
            <h6> Card Listed On: {{ formatCreated }} </h6>
            <h6> Card Ends: {{ formatExpiry }}</h6>
          </b-container>
          <br>

          <b-card no-body>
            <template #header>
              <b-card-text>{{ fullCard.description !== "" ? fullCard.description : "No Description" }}</b-card-text>
            </template>
          </b-card>
          <br>

          <b-card no-body>
            <template #header>
              Tags:
              <b-badge v-for="keyword in fullCard.keywords" :key="keyword" class="ml-1">{{ keyword }}</b-badge>
            </template>
          </b-card>
          <br>

          <b-container class="creator-info">
            <h6><strong> Creator Info: </strong></h6>
            <label> Name: {{ fullCard.creator.firstName }} {{ fullCard.creator.lastName }} </label>
            <br>
            <label> Location: {{ getAddress }}</label>
          </b-container>
          <br>
          <div>
            <b-button v-if="canDeleteOrExtend" class="button-left" variant="danger"
                      @click="openDeleteConfirmDialog" title="Delete Card">
              <b-icon-trash-fill/>
            </b-button>
            <b-button v-if="canDeleteOrExtend && cardWithinExtendPeriod()" class="button-left"
                      variant="success" @click="openExtendConfirmDialog"> Extend
              <b-icon-alarm/>
            </b-button>
            <b-button v-if="!messageVisible" class="button-right" variant="primary" @click="messageVisible = true" title="Open Messages">
              <b-icon-chat-quote-fill/> Open Messages
            </b-button>
            <b-button v-else class="button-right" variant="primary"  @click="messageVisible = false" title="Close Messages">
              <b-icon-chat-quote-fill/> Close Messages
            </b-button>

          </div>


          <b-modal ref="confirmDeleteCardModal" size="sm" title="Delete Card" ok-variant="danger" ok-title="Delete"
                   @ok="confirmDeleteCard">
            <h6>
              Are you sure you want to <strong>permanently</strong> delete this card?
            </h6>
          </b-modal>

          <b-modal ref="confirmExtendCardModal" size="sm" title="Extend Expiry" ok-variant="success" ok-title="Extend"
                   @ok="confirmExtendExpiry">
            <h6>
              Are you sure you want to <strong>extend</strong> this card's expiry?
            </h6>
          </b-modal>

        </div>
      </b-card>

      <b-collapse v-model="messageVisible" id="messageBox">
        <messages :cardCreatorId="fullCard.creator.id" :cardId="cardId" :is-card-creator="isCardCreator" :notification-sender-id="openedFromNotifications"></messages>
      </b-collapse>
  </div>
</template>

<style scoped>

.button-left{
  float: left;
  margin-left: 1rem;
}

.button-right{
  float: right;
  margin-right: 1rem;
}

.creator-info{
  width: 100%;
  text-align: center;
  background-color: #e9ecef;
  padding: 0.6rem;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  color: #495057
}

#messageBox{
  margin-top: 1rem
}

label{
  word-wrap: break-word;
}

</style>

<script>
import Api from "../../Api";
import {formatAddress} from "../../util";
import Messages from "../../components/model/Messages"

export default {
  name: "full-card",
  components: {Messages},
  props: ['cardId','openedFromNotifications'],
  data() {
    return {
      fullCard: {
        creator: {
          firstName: '',
          homeAddress: {
            suburb: ""
          }

        }
      },
      messageText: '',
      messageVisible: false,
    }
  },
  beforeMount() {
    this.getCard()
    if (this.openedFromNotifications){
      this.messageVisible = true;
    }
  },
  methods: {

    /**
     * Calls the API request to get the full details of a card
     * determined by the given cardId.
     */
    getCard() {
      Api.getFullCard(this.cardId)
          .then((resp) => {
            this.fullCard = resp.data;
          }).catch((error) => {
        this.$log.debug(error);
      })
    },

    /**
     * Returns true if card within 48 hours of expiring, or afterwards. False otherwise
     */
    cardWithinExtendPeriod: function () {
      const millisecondsIn48Hours = 172800000;

      const fortyEightHoursFromNow = new Date((new Date().getTime() + millisecondsIn48Hours));
      const expiry = new Date(this.fullCard.displayPeriodEnd);

      return (fortyEightHoursFromNow.getTime() >= expiry.getTime());
    },

    /**
     * Opens the dialog to confirm if the image with given id should be deleted.
     * Stores the given id in this.imageIdToDelete
     */
    openDeleteConfirmDialog: function () {
      this.$refs.confirmDeleteCardModal.show();
    },

    /**
     * Sends an API request to delete a card determined given the cardId
     * and deletes the card from the marketplaceCards list using filter
     */
    async confirmDeleteCard() {
      await Api.deleteCard(this.cardId);
      this.$emit('cardChanged', this.fullCard);
      this.$emit('closeModal', this.fullCard);
    },

    /**
     * Opens the dialog to confirm if the card can be extended
     */
    openExtendConfirmDialog: function () {
      this.$refs.confirmExtendCardModal.show();
    },

    /**
     * Sends an api request with the card's id that is to have it's expiry extended.
     */
    async confirmExtendExpiry() {
      await Api.extendCardExpiry(this.cardId)
      this.getCard();
      this.$emit('cardChanged', this.fullCard);
      this.$emit('closeModal', this.fullCard);
    },

    closeFullViewCardModal() {
      this.$emit('closeModal', this.fullCard);
    },
  },

  computed: {
    /**
     * Formats the address using util function and appropriate privacy level.
     *
     * @return address formatted
     */
    getAddress: function () {
      return formatAddress(this.fullCard.creator.homeAddress, 3);
    },
    /**
     * format Expiry date
     */
    formatExpiry: function () {
      return new Date(this.fullCard.displayPeriodEnd).toString().split(" ").slice(0, 5).join(" ");
    },

    /**
     * format Created date
     */
    formatCreated: function () {
      return new Date(this.fullCard.created).toString().split(" ").slice(0, 5).join(" ").toString();
    },

    /**
     * Returns true if user is creator of the card or an Application admin
     * @returns {boolean}
     */
    canDeleteOrExtend: function () {
      return (this.fullCard.creator.id === this.$currentUser.id || this.$currentUser.role !== 'user');
    },

    /**
     * Returns true if user is creator of the card
     * @returns {boolean}
     */
    isCardCreator: function () {
      return (this.fullCard.creator.id === this.$currentUser.id);
    }
  }
}
</script>
