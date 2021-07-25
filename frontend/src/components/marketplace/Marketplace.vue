<!--
Page for users to input business information for registration of a Business
Date: 21/5/21
-->

<template>
  <div>
    <b-card class="shadow" style="max-width: 85%">
      <div class="row">
        <h1 class="col-10"><b-icon-shop/> Marketplace </h1>
        <div class="col-2">
          <b-button class="float-right" @click="openCreateCardModal">
            <b-icon-plus-square-fill animation="fade"/>
            Create
          </b-button>
        </div>
      </div>
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
        <b-tab v-for="[section, sectionName] in sections" :key=section :title=sectionName>
          <marketplace-section
            :cards="marketplaceCards"
            :is-card-format="isCardFormat"
            v-on:cardClicked="openFullCardModal"
            :cardsPerRow:="3"
            :perPage="8"
            :section="section"
            :ref="section"
          />
        </b-tab>
      </b-tabs>
      <b-modal id="full-card" hide-header hide-footer>
        <MarketplaceCardFull
            :closeFullViewCardModal="closeFullCardModal"
            :deleteSelectedCard="deleteSelectedCard"
            :cardId = "this.cardId"
            >  </MarketplaceCardFull>
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
      sections: [['ForSale', 'For Sale'], ['Wanted', 'Wanted'], ['Exchange', 'Exchange']],
      error: "",
      cardId: 0,
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
     * opens the Full card modal.
     */
    openFullCardModal(cardId) {
      this.cardId = cardId;
      this.$bvModal.show('full-card');
    },

    /**
     * Closes the full card modal when cancel button pressed.
     */
    closeFullCardModal() {
      this.$bvModal.hide('full-card');
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

    deleteSelectedCard(){
      api.deleteCard(this.cardId);
      this.closeFullCardModal();
    },

    /**
     * Sends a post request with the card data when create card button is pressed.
     * @param cardData  The object that contains the card data.
     */
    createCard(cardData) {
      api.createCard(cardData)
      .then(createCardResponse => {
        this.$log.debug("Card Created", createCardResponse);
        this.$bvModal.hide('create-card');
        this.error = '';

        // Not the most elegant way to do it, but we have to make the marketplace section refresh
        // The way this is done is by giving each section component a unique ref, with the same name as the section
        // Then, we just get the component using the ref and call the refresh function
        // Also, you have to index into the ref because... reasons: https://forum.vuejs.org/t/this-refs-theid-returns-an-array/31995/10
        this.$refs[cardData.section][0].refreshData();
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

