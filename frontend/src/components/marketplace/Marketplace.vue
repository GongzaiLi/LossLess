<!--
Page for users to input business information for registration of a Business
Date: 21/5/21
-->

<template>
  <div>
    <b-card class="shadow">
      <h1><b-icon-shop/> Market Place </h1>
      <b-button @click="openCreateCardModal" class="float-right">
        <b-icon-plus-square-fill animation="fade"/>
        Create
      </b-button>
      <b-input-group>
        <b-form-text style="margin-right: 7px">
          Table View
        </b-form-text>
        <b-form-checkbox v-model="isCardFormat" switch/>
        <b-form-text>
          Card View
        </b-form-text>
      </b-input-group>
      <b-tabs v-model=activeTabIndex align="center" fill>
        <b-tab title="For Sale" @click="getCardsFromSection(`ForSale`)">
          <marketplace-section
            :cards="marketplaceCards"
            :is-card-format="isCardFormat"
          />
        </b-tab>

        <b-tab title="Wanted" @click="getCardsFromSection(`Wanted`)">
          <marketplace-section
              :cards="marketplaceCards"
              :is-card-format="isCardFormat"
          />
        </b-tab>

        <b-tab title="Exchange" @click="getCardsFromSection(`Exchange`)">
          <marketplace-section
              :cards="marketplaceCards"
              :is-card-format="isCardFormat"
          />
        </b-tab>

      </b-tabs>
      <b-modal id="view-card" hide-header hide-footer>
        <MarketplaceCardFull :close-full-view-card="closeViewCardModal">  </MarketplaceCardFull>
      </b-modal>

      <b-modal id="create-card" hide-header hide-footer>
        <CreateCard @createAction="createCard($event)"
                    :cancelAction="closeCreateCardModal"
                    :showError="error"> </CreateCard>
      </b-modal>
    </b-card>
  </div>
</template>

<script>

import MarketplaceSection from "./MarketplaceSection";
import MarketplaceCardFull from "./MarketplaceCardFull";
import api from "../../Api";

import CreateCard from "./CreateCard";

export default {
  components: { MarketplaceSection, MarketplaceCardFull, CreateCard },
  data: function () {
    return {
      error: "",
      activeTabIndex: 0,
      isCardFormat: true,
      marketplaceCards: [],
      dismissSecs: 5,
      dismissCountDown: 0,
      showDismissibleAlert: false
    }
  },
  methods: {
    /**
     * Pushes errors to errors list to be displayed as response on the screen,
     * if there are any.
     */
    pushErrors(error) {
      this.errors.push(error.message);
    },

    /**
     * Opens the create card modal when create button pressed.
     */
    openViewCardModal() {
      this.$bvModal.show('view-card');
    },
    /**
     * Closes the create card modal when cancel button pressed.
     */
    closeViewCardModal() {
      this.$bvModal.hide('view-card');
    },

    /**
     * Opens the create card modal when create button pressed.
     */
    openCreateCardModal() {
      this.$bvModal.show('create-card');
    },

    /**
     * Closes the create card modal when cancel button pressed.
     */
    closeCreateCardModal() {
      this.$bvModal.hide('create-card');
    },

    /**
     * Sends an API request to get all cards determined by the current tab the user is on.
     * Uses section to differentiate the tabs
     * @param section This is the section of the tab to grab all cards from
     */
    getCardsFromSection(section) {
      api.getCardsBySection(section)
          .then((resp) => {
            this.marketplaceCards = resp.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },

    /**
     * Sends a post request with the card data when create card button is pressed.
     * @param cardData  The object that contains the card data.
     */
    createCard(cardData) {
      console.log("BBBBBBBBBBBBBB")
      api.createCard(cardData)
      .then(createCardResponse => {
        this.$log.debug("Card Created", createCardResponse);
        this.$bvModal.hide('create-card');
        this.error = '';
      })
      .catch(error => {
        this.$log.debug(error);
        this.error = this.getErrorMessageFromApiError(error);
      })
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown
    },

    /**
     * Given an error thrown by a rejected axios (api) request, returns a user-friendly string
     * describing that error. Only applies to POST requests for cards
     */
    getErrorMessageFromApiError(error) {
      if ((error.response && error.response.status === 400)) {
        let errorMessage = '';
        Object.keys(error.response.data).forEach(key => {
          errorMessage += `${key}: ${error.response.data[key]}\n`
        })
        return errorMessage;
      } else if ((error.response && error.response.status === 403)) {
        return "Forbidden. You are not an authorized administrator";
      } else if (error.request) {  // The request was made but no response was received, see https://github.com/axios/axios#handling-errors
        return "No Internet Connectivity";
      } else {
        return "Server error";
      }
    },
  },

}
</script>

