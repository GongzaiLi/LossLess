<template>
  <b-card>
    <div>
      <h1 align="left"><strong> {{fullCard.title}} </strong></h1>
      <b-container>
        <h6 align="left"> Listed On {{fullCard.created}}</h6>
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
        <b-button v-if="canDelete" style="float: left; margin-left: 1rem" variant="danger" @click="openDeleteConfirmDialog"> Delete </b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="closeFullViewCardModal"> Close </b-button>
      </div>

      <b-modal ref="confirmDeleteCardModal" size="sm" title="Delete Card" ok-variant="danger" ok-title="Delete" @ok="confirmDeleteCard">
        <h6>
          Are you sure you want to <strong>permanently</strong> delete this card?
        </h6>
      </b-modal>

    </div>
  </b-card>
</template>

<script>
import api from "../../Api";
export default {
  name: "full-card",
  props: ["cardId", 'closeFullViewCardModal'],
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
    }
  },
  mounted() {
    this.getCard()
  },
  methods: {

    /**
     * A
     */
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
     * Opens the dialog to confirm if the image with given id should be deleted.
     * Stores the given id in this.imageIdToDelete
     */
    openDeleteConfirmDialog: function() {
      this.$refs.confirmDeleteCardModal.show();
    },

    /**
     * Emits an event `deleteCard` to delete a card
     * that is listened to inside the marketplace
     */
    confirmDeleteCard() {
      this.$emit('deleteCard')
    }


  },
  computed: {
    /**
     * Returns true if user is creator of the card or an Application admin
     * @returns {boolean}
     */
    canDelete: function(){
      return(this.fullCard.creator.id===this.$currentUser.id || this.$currentUser.role!=='user');
    }
  }
}
</script>

<style scoped>

</style>
