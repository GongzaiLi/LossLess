<template>
  <div>
  <b-card class="shadow" style="max-width: 70rem">
      <h1 v-if="$currentUser.currentlyActingAs">{{$currentUser.currentlyActingAs.name + "'s Home Page"}}</h1>
      <h1 v-else>{{userData.firstName + "'s Home Page"}}</h1>
      <router-link v-if="$currentUser.currentlyActingAs" :to="{ name: 'business-profile', params: { id: $currentUser.currentlyActingAs.id }}">
      <h4>Profile page</h4>
    </router-link>
    <router-link v-else :to="{ name: 'user-profile', params: { id: $currentUser.id }}">
      <h4>Profile page</h4>
    </router-link>
  </b-card>

  <b-card style="margin-top: 30px; max-width: 70rem" v-if="hasExpiredCards && !$currentUser.currentlyActingAs" class="shadow">
    <h1><b-icon-clock/> Your recently closed cards </h1>
    <h6>These cards will be deleted within 24 hours of their closing date. You can either extend their display period or delete cards you no longer need.</h6>
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
        :is-card-format="isCardFormat"
        :cardsPerRow:="3"
        :perPage="5"
        section="homepage"
        v-on:cardCountChanged="checkExpiredCardsExist"
    />
  </b-card>
  </div>
</template>

<script>
import api from "../../Api";
import MarketplaceSection from "../marketplace/MarketplaceSection";
import Api from "../../Api";

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
      isCardFormat: true,
      hasExpiredCards: false,
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
            this.checkForExpiringCards();
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },

    async checkForExpiringCards () {
      await Api
        .getExpiringCards(this.$currentUser.id)
        .then((resp) => {
          if (resp.data.length > 0) {
            this.hasExpiredCards = true;
          }
        })
        .catch((err) => {
          console.log(err)
        })

    },

    checkExpiredCardsExist(cards) {
      this.hasExpiredCards = cards.length !== 0;
    }
  },
}
</script>

