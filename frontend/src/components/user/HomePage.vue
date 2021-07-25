<template>
  <b-card border-variant="secondary" header-border-variant="secondary">
      <h1 v-if="$currentUser.currentlyActingAs">{{$currentUser.currentlyActingAs.name + "'s Home Page"}}</h1>
      <h1 v-else>{{userData.firstName + "'s Home Page"}}</h1>
      <router-link v-if="$currentUser.currentlyActingAs" :to="{ name: 'business-profile', params: { id: $currentUser.currentlyActingAs.id }}">
      <h4>Profile page</h4>
    </router-link>
    <router-link v-else :to="{ name: 'user-profile', params: { id: $currentUser.id }}">
      <h4>Profile page</h4>
    </router-link>

    <b-card style="margin-top: 30px" v-if="expiringCardsExist" class="shadow">
      <h1><b-icon-clock/> Your Cards Closing Soon </h1>
      <b-input-group>
        <b-form-text style="margin-right: 7px">
          Table View
        </b-form-text>
        <b-form-checkbox v-model="isCardFormat" switch/>
        <b-form-text>
          Card View
        </b-form-text>
      </b-input-group>
      <marketplace-section
          :cards="expiringCards"
          :is-card-format="isCardFormat"
          :cardsPerRow:="3"
          :perPage="5"
      />
      </b-card>
  </b-card>
</template>

<script>
import api from "../../Api";
import MarketplaceSection from "../marketplace/MarketplaceSection";

export default {
  components: {MarketplaceSection},
  data: function () {
    return {
      userData: {
        id: "",
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: "",
      },
      errors: [],
      activeTabIndex: 0,
      isCardFormat: false,
      expiringCards: [],
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);
  },

  methods: {
    /**
     * this is a get api which can take Specific user to display on the page
     * The function id means user's id, if the serve find -
     * -the user's id will response the data and keep the data into this.userData
     */
    getUserInfo: function (id) {
      api
          .getUser(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.userData = response.data;
            this.getUserExpiredCards(id);
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },

    /**
     * this is a get api which can take Specific user to and display it's expiring cards
     * The function id means user's id, if the server finds
     * the user's expiring cards will response the data and keep the data into this.expiringCards
     */
    getUserExpiredCards: function (id) {
      api
          .getExpiringCards(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.expiringCards = response.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },
  },
  computed: {
    /**
     * The rows function just computed how many pages in the search table.
     * @returns {boolean}
     */
    expiringCardsExist() {
      return this.expiringCards.length > 0;
    },
  }
}
</script>

