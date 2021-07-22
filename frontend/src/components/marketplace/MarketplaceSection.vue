<template>
  <div v-if="isCardFormat">
    <b-container>
      <b-row cols-lg="3">
        <b-col v-for="(cardInfo, index) in cards" v-bind:key="index">
          <marketplace-card
              :card-info="cardInfo"
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
             @row-clicked="rowClickHandler"
             class="overflow-auto"
             :fields="fields"
             :per-page="perPage"
             :items="cards"
             :current-page="currentPage"
    >
      <template v-slot:cell(title)="{ item }">
        <div v-b-tooltip="item.title">{{shortenText(item.title, 20)}}</div>
      </template>

      <template v-slot:cell(keywords)="{ item }">
        <div v-b-tooltip="formatTags(item.keywords)">{{shortenText(formatTags(item.keywords), 20)}}</div>
      </template>

      <template v-slot:cell(description)="{ item }">
        <div v-b-tooltip="item.description">{{shortenText(item.description, 20)}}</div>
      </template>

      <template v-slot:cell(creator)="{ item }">
        <div v-b-tooltip="item.creator.firstName">{{shortenText(item.creator.firstName + " " + item.creator.lastName, 15)}}</div>
      </template>

      <template v-slot:cell(location)="{ item }">
        <div v-b-tooltip="item.creator.homeAddress">{{shortenText(formatAddress(item.creator.homeAddress), 25)}}</div>
      </template>

      <template #empty>
        <h3 class="no-results-overlay" >No results to display</h3>
      </template>
    </b-table>
    <pagination :per-page="perPage" :total-items="totalItems" v-model="currentPage" v-show="cards.length"/>
  </div>
</template>

<script>
import pagination from "../model/Pagination";
import MarketplaceCard from "./MarketplaceCard";

export default {
  name: "MarketplaceSection",
  components: {pagination, MarketplaceCard},
  props: ["cards", "isCardFormat", "cardsPerRow", "perPage"],
  data: function () {
    return {
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
    rowClickHandler: function (record) {
      console.log("Clicked row: ", record);
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
     * Return tags in a joined format
     */
    formatTags(tags) {
      return tags.join(", ");
    },

    /**
     * Combine fields of address
     */
    formatAddress: function (address) {
      return `${address.streetNumber} ${address.streetName}, ${address.suburb}, ` +
          `${address.city} ${address.region} ${address.country} ${address.postcode}`;
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
      ];
    }
  }
}
</script>
