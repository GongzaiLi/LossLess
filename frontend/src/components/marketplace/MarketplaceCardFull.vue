<template>
  <b-card>
    <div>
      <h1 align="left"><strong> {{fullCard.title}}  </strong></h1>
      <b-container>
        <h6 aligh="left"> Card Listed On: {{formatCreated}} </h6>
        <h6 aligh="Left"> Card Ends: {{formatExpiry}}</h6>
      </b-container>
      <br>

      <b-card no-body>
        <template #header>
          <b-card-text> {{fullCard.description}}</b-card-text>
        </template>
      </b-card>
      <br>

      <b-card no-body>
        <template #header>
          Tags:
          <b-badge v-for="keyword in fullCard.keywords" :key="keyword" class="ml-1">{{keyword}}</b-badge>
        </template>
      </b-card>
      <br>


      <b-input-group-text>
        <b-container>
          <h6 align="center"> <strong> Seller Info: </strong></h6>
          <label> Seller Name: {{fullCard.creator.firstName }} {{ fullCard.creator.lastName }}   </label>
          <br>
          <label> Seller Location: {{fullCard.creator.homeAddress.suburb ? fullCard.creator.homeAddress.suburb + "," : ""}} {{fullCard.creator.homeAddress.city}}</label>
        </b-container>
      </b-input-group-text>
      <br>
      <div>
        <b-button v-if="canDeleteOrExtend" style="float: left; margin-left: 1rem" variant="danger" @click="deleteSelectedCard"> Delete </b-button>
        <b-button v-if="canDeleteOrExtend" style="float: left; margin-left: 1rem" variant="success" @click="openExtendConfirmDialog"> Extend <b-icon-alarm/></b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="closeFullViewCardModal"> Close </b-button>
      </div>

      <b-modal ref="confirmExtendCardModal" size="sm" title="Extend Expiry" ok-variant="success" ok-title="Extend" @ok="confirmExtendExpiry">
        <h6>
          Are you sure you want to <strong>extend</strong> this card's expiry?
        </h6>
      </b-modal>

    </div>
  </b-card>
</template>

<script>
import api from "../../Api";
export default {
  name: "full-card",
  props: ["cardId", 'closeFullViewCardModal','deleteSelectedCard'],
  data() {
    return {
      fullCard: {
        creator: {
          firstName: '',
          homeAddress: {
            suburb: ""
          }

        }
      }
    }
  },
  mounted() {
    this.getCard()
  },
  methods: {

    getCard() {
      api.getFullCard(this.cardId)
        .then((resp) => {
          this.$log.debug("Data loaded: ", resp.data);
          this.fullCard = resp.data;
      }).catch((error) => {
          this.$log.debug(error);
      })
    },

    /**
     * Opens the dialog to confirm if the card can be extended
     */
    openExtendConfirmDialog: function() {
      this.$refs.confirmExtendCardModal.show();
    },


    /**
     * Emits an event `extendCard` to extend a card
     * that is listened to inside the marketplace
     */
    confirmExtendExpiry() {
      this.$emit('extendCard')
    }

  },

  computed: {
    /**
     * format Expiry date
     */
    formatExpiry: function () {
      return new Date(this.fullCard.displayPeriodEnd).toUTCString().split(" ").slice(0, 5).join(" ");
    },

    /**
     * format Created date
     */
    formatCreated: function() {
      return new Date(this.fullCard.created).toUTCString().split(" ").slice(0, 5).join(" ");
    },

    /**
     * Returns true if user is creator of the card or an Application admin
     * @returns {boolean}
     */
    canDeleteOrExtend: function(){
      return(this.fullCard.creator.id===this.$currentUser.id || this.$currentUser.role!=='user');
    }
  }
}
</script>

<style scoped>

</style>
