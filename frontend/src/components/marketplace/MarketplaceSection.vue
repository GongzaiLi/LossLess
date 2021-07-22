<template>
  <div v-if="isCardFormat">
    <b-container>
      <b-row cols-lg="3">
        <b-col v-for="(cardInfo, index) in cards" v-bind:key="index">
          <marketplace-card
              :card-info="cardInfo"
              v-on:cardClicked="cardClickHandler"
          />
        </b-col>
      </b-row>
    </b-container>
    <div v-if="cards.length === 0">
      <h3> No cards to display </h3>
    </div>
  </div>

  <div v-else>
    <b-table striped hover
             table-class="text-nowrap"
             responsive="lg"
             no-border-collapse
             bordered
             stacked="sm"
             show-empty
             @row-clicked="cardClickHandler"
             class="overflow-auto"
             :fields="fields"
             :per-page="perPage"
             :items="cards"
             :current-page="currentPage"
    >
      <template v-slot:cell(title)="{ item }">
        <div>{{shortenText(item.title, 20)}}</div>
      </template>

      <template v-slot:cell(keywords)="{ item }">
        <div><b-badge v-for="keyword in item.keywords" :key="keyword" class="ml-1">{{keyword}}</b-badge></div>
      </template>

      <template v-slot:cell(description)="{ item }">
        <div>{{shortenText(item.description, 20)}}</div>
      </template>

      <template v-slot:cell(creator)="{ item }">
        <div>{{shortenText(item.creator.firstName + " " + item.creator.lastName, 15)}}</div>
      </template>

      <template v-slot:cell(location)="{ item }">
        <div>{{shortenText(formatAddress(item.creator.homeAddress), 25)}}</div>
      </template>

      <template v-slot:cell(actions)="{ item }">
        <div v-if="item.creator.id === $currentUser.id"><b-button @click="openExtendConfirmDialog(item.id)" size="sm" style="margin-top: -10px; margin-bottom: -8px"> Extend <b-icon-alarm/></b-button></div>
      </template>

      <template #empty>
        <h3 class="no-results-overlay" >No results to display</h3>
      </template>
    </b-table>
    <pagination :per-page="perPage" :total-items="totalItems" v-model="currentPage" v-show="cards.length"/>

    <b-modal ref="confirmExtendCardModal" size="sm" title="Extend Expiry" ok-variant="success" ok-title="Extend" @ok="confirmExtendExpiry">
      <h6>
        Are you sure you want to <strong>extend</strong> this card's expiry?
      </h6>
    </b-modal>
  </div>
</template>

<script>
import pagination from "../model/Pagination";
import MarketplaceCard from "./MarketplaceCard";
import api from "../../Api";

export default {
  name: "MarketplaceSection",
  components: {pagination, MarketplaceCard},
  props: ["cards", "isCardFormat", "cardsPerRow", "perPage", "refresh"],
  data: function () {
    return {
      cardToBeExtended: "",
      errors: [],
      currentPage: 1,
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
     * When called do currently undetermined action
     */
    cardClickHandler: function (card) {
      this.$emit('cardClicked', card.id)
    },

    /**
     * Return given in shortened format
     */
    shortenText(description, sliceLength) {
      if (description === "" || description.length <= sliceLength) {
        return description.trim();
      }
      const shortenedText = description.slice(0, sliceLength).trim();
      return `${shortenedText}${shortenedText.endsWith('.') ? '..' : '...'}`;
    },

    /**
     * Combine fields of address
     */
    formatAddress: function (address) {
      return `${address.suburb ? address.suburb + ', ' : ''}${address.city}`;
    },

    /**
     * Opens the dialog to confirm if the card with given id should be extended.
     */
    openExtendConfirmDialog: function(cardId) {
      this.cardToBeExtended = cardId;
      this.$refs.confirmExtendCardModal.show();
    },

    /**
     * Sends an api request with the card's id that is to have it's expiry extended.
     */
    confirmExtendExpiry: function() {
      api
          .extendCardExpiry(this.cardToBeExtended)
          .then(() => {
            this.refresh(this.$currentUser.id);
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    }

  },

  computed: {
    /**
     * The rows function just computed how many pages in the search table.
     * @returns {number}
     */
    totalItems() {
      return this.cards.length;
    },



    /**
     * Fields of table format
     */
    fields() {
      return [
        {
          key: 'title',
          sortable: true
        },
        {
          key: 'description',
          sortable: true,
        },
        {
          key: 'keywords',
        },
        {
          key: 'creator',
          label: "Creator",
          sortable: true
        },
        {
          key: 'location',
          label: "Location",
          sortable: true
        },
        {
          key: 'actions',
          label: "Actions",
        },
      ]

    }
  }
}
</script>
