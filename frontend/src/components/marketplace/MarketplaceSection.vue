<template>
  <div class="p-3 marketplace-section">

    <div v-if="isCardFormat">
      <b-row v-if="section !== 'homepage'" class="pb-2">
        <b-col md="2"><h3 class="float-right">Sort By:</h3></b-col>
        <b-col md="2">
          <b-form-select v-model="sortBy" :options="sortByOptions" id="marketplaceSortBySelect"></b-form-select>
        </b-col>
        <b-col md="2">
          <b-form-select v-model="sortOrder" :options="sortOrderOptions" id="marketplaceSortOrderSelect"></b-form-select>
        </b-col>
        <b-col md="2"><b-button @click="refreshData">Sort</b-button></b-col>
      </b-row>
        <b-row :cols-lg="cardsPerRow" cols-sm="1">
          <b-col v-for="(cardInfo, index) in cards" v-bind:key="index">
            <marketplace-card
                :card-info="cardInfo"
                v-on:cardClicked="cardClickHandler"
            />
          </b-col>
        </b-row>
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
               class="overflow-auto"
               @row-clicked="cardClickHandler"
               @sort-changed="sortingChanged"
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
          <div>{{shortenText(getAddress(item.creator.homeAddress), 25)}}</div>
        </template>

        <template #empty>
          <h3 class="no-results-overlay" >No results to display</h3>
        </template>
      </b-table>
    </div>
    <pagination @input="pageChanged" :per-page="perPage" :total-items="totalItems" v-show="totalItems > 0"/>
    <b-modal :id="`full-card-${this.section}`" hide-header hide-footer>
      <MarketplaceCardFull
          :cardId = "cardId"
          v-on:cardChanged="refreshData"
          v-on:closeModal="closeFullCardModal">
        >  </MarketplaceCardFull>
    </b-modal>
  </div>
</template>

<style scoped>
.marketplace-section {
  border-bottom: 1px solid #dee2e6;
  border-left: 1px solid #dee2e6;
  border-right: 1px solid #dee2e6;

  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
}
</style>

<script>
import pagination from "../model/Pagination";
import MarketplaceCard from "./MarketplaceCard";
import MarketplaceCardFull from "./MarketplaceCardFull";
import Api from "../../Api";
import {formatAddress} from "../../util";

export default {
  name: "MarketplaceSection",
  components: {pagination, MarketplaceCard, MarketplaceCardFull},
  props: ["isCardFormat", "cardsPerRow", "perPage", "section"],
  mounted() {
    this.refreshData();
  },
  data: function () {
    return {
      sortBy: "created",
      sortByOptions: [
        { value: 'created', text: 'Date created' },
        { value: 'title', text: 'Title' },
        { value: 'location', text: 'Location'},
      ],
      sortOrder: "desc",
      sortOrderOptions: [
        { value: 'asc', text: 'Ascending' },
        { value: 'desc', text: 'Descending' },
      ],
      cardId: null,
      errors: [],
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
        },
        {
          key: 'keywords',
        },
        {
          key: 'creator',
          label: "Creator",
        },
        {
          key: 'location',
          label: "Location",
          sortable: true
        },
        {
          key: 'created',
          label: "Created",
          sortable: true,
          formatter: (value) => {
            const date = new Date(value);
            return `${date.getMonth()}/${date.getDate()}/${date.getFullYear()} ${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
          },
        }

      ]
    }
  },
  methods: {
    /**
     * When called do currently undetermined action
     */
    cardClickHandler: function (card) {
      this.cardId = card.id;
      this.$bvModal.show(`full-card-${this.section}`);  // Open only our modal
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
     * Formats the address using util function and appropriate privacy level.
     *
     * @param address the address of the card creator
     * @return address formatted
     */
    getAddress: function (address) {
      return formatAddress(address, 4);
    },

    /**
     * Closes the full card modal when cancel button pressed.
     */
    closeFullCardModal() {
      this.$bvModal.hide(`full-card-${this.section}`);
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

    /**
     * Queries for card data from API using the pagination and sorting data fields.
     */
    async refreshData() {
      if (this.section !== "homepage") {
        const resp = await Api.getCardsBySection(this.section, this.currentPage - 1, this.perPage, this.sortBy, this.sortOrder);
        this.cards = resp.data.results;
        this.totalItems = resp.data.totalItems;
      } else {
        const resp = await Api.getExpiredCards(this.$currentUser.id);
        this.cards = resp.data;
        this.$emit('cardCountChanged', this.cards);
      }
    },

    /**
     * Event handler when the sort order is changed in the table.
     * Will re-fresh data from the API.
     */
    async sortingChanged(ctx) {
      this.sortBy = ctx.sortBy;
      this.sortOrder = ctx.sortDesc ? 'desc' : 'asc';
      await this.refreshData();
    },
  },
}
</script>
