<template>
  <div>
    <div v-if="isCardFormat">
      <b-container>
        <b-row cols-lg="4">
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
               :items="cards"
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

        <template #empty>
          <h3 class="no-results-overlay" >No results to display</h3>
        </template>
      </b-table>
    </div>
    <pagination @input="pageChanged" :per-page="perPage" :total-items="totalItems" v-show="totalItems > 0"/>
  </div>
</template>

<script>
import pagination from "../model/Pagination";
import MarketplaceCard from "./MarketplaceCard";
import Api from "../../Api";

export default {
  name: "MarketplaceSection",
  components: {pagination, MarketplaceCard},
  props: ["isCardFormat", "cardsPerRow", "perPage", "section"],
  mounted() {
    this.refreshData();
  },
  data: function () {
    return {
      currentPage: 1,
      cards: [],
      totalItems: 0,
      fields: [
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
      ]
    }
  },
  methods: {
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
     * Handler when the current page is changed, eg. by the Pagination component.
     * Makes an API request to get cards with the new pagination data.
     * @param currentPage The new page the user has changed to (1-indexed)
     */
    async pageChanged(currentPage) {
      this.currentPage = currentPage;
      await this.refreshData();
    },

    async refreshData() {
      console.log("refreshed");
      const resp = await Api.getCardsBySection(this.section, this.currentPage - 1, this.perPage);
      this.cards = resp.data.results;
      this.totalItems = resp.data.totalItems;
    }
  },
}
</script>
