<!--
Page for users to input business information for registration of a Business
Authors: James
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
            v-on:cardClicked="openFullCardModal"
          />
        </b-tab>

        <b-tab title="Wanted" @click="getCardsFromSection(`Wanted`)">
          <marketplace-section
              :cards="marketplaceCards"
              :is-card-format="isCardFormat"
              v-on:cardClicked="openFullCardModal"
          />
        </b-tab>

        <b-tab title="Exchange" @click="getCardsFromSection(`Exchange`)">
          <marketplace-section
              :cards="marketplaceCards"
              :is-card-format="isCardFormat"
              v-on:cardClicked="openFullCardModal"
          />
        </b-tab>

      </b-tabs>
      <b-modal id="full-card" hide-header hide-footer>
        <MarketplaceCardFull
            :closeFullViewCardModal="closeFullCardModal"
            :cardId = "this.cardId"
            >  </MarketplaceCardFull>
      </b-modal>

      <b-modal id="create-card" hide-header hide-footer>
        <CreateCard :okAction="createCard"
                    :cancelAction="closeCreateCardModal"> </CreateCard>
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
      errors: [],
      cardId: 0,
      activeTabIndex: 0,
      isCardFormat: true,
      marketplaceCards: [],
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

    openFullCardModal(cardId) {
      this.cardId = cardId;
      this.$bvModal.show('full-card');
    },

    /**
     * Closes the create card modal when cancel button pressed.
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
  },
  computed: {
  }
}
</script>

