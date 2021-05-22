<template>
  <div v-if="isCardFormat">
    <b-container>
      <b-row v-for="i in Math.ceil(cards.length / cardsPerRow)" v-bind:key="i">
        <b-col :cols="12/cardsPerRow" v-for="(cardInfo, index) in cards.slice((i - 1) * cardsPerRow, i * cardsPerRow)" v-bind:key="index">
          <marketplace-card
              :card-info="cardInfo"
              style="margin-top: 10px"
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
      <template #empty>
        <h3 class="no-results-overlay" >No results to display</h3>
      </template>
    </b-table>
    <pagination :per-page="perPage" :total-items="totalItems" v-model="currentPage" v-show="cards.length"/>
  </div>
</template>

<script>
import pagination from "../model/Pagination";
import MarketplaceCard from "@/components/marketplace/MarketplaceCard";

export default {
  name: "MarketplaceSection",
  components: {pagination, MarketplaceCard},
  props: ["cards", "isCardFormat"],
  data: function () {
    return {
      errors: [],
      cardsPerRow: 3, //Change this to change how many marketplace cards appear in each row.
      perPage: 10,
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
          key: 'tags',
        },
        {
          key: 'listerName',
          label: "Lister",
          sortable: true
        },
        {
          key: 'listerLocation',
          label: "Location",
          sortable: true
        },
      ];
    }
  }
}
</script>

<style scoped>

</style>
