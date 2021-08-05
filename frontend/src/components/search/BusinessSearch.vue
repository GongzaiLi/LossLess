<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim, Gongzai Li
Date: 7/3/2021
-->
<template>
  <b-card style="max-width: 1200px">
    <b-row style="height: 50px">
      <b-col cols="7">
        <b-form-input v-model="searchQuery" type="search"
                      placeholder="Search businesses"></b-form-input>
      </b-col>
      <b-col cols="0">
        <b-button> Search </b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="12">
        <b-table striped hover
                 ref="searchTable"
                 table-class="text-nowrap"
                 responsive="lg"
                 no-border-collapse
                 bordered
                 no-local-sorting
                 stacked="sm"
                 show-empty
                 :fields="fields"
                 :per-page="perPage"
                 :items="items"
                 :current-page="currentPage">
          <template #empty>
            <h3 class="no-results-overlay" >No results to display</h3>
          </template>
        </b-table>
      </b-col>
    </b-row>
    <pagination ref="tablePag" :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="totalResults"/>
  </b-card>
</template>

<style scoped>
.no-results-overlay {
  margin-top:7em;
  margin-bottom:7em;
  text-align: center;
}
</style>

<script>
import pagination from "../model/Pagination";

export default {
  components: {
    pagination,
  },
  data: function () {
    return {
      searchQuery: "",
      setQuery:"",
      totalResults: 0,
      sortDesc: false,
      sortBy: "",
      perPage: 10,
      currentPage: 1,
      items: [
        {
          name: "Willy Wonka Chocolate",
          description: "We make the best chocolate in the world. Please buy some it is delicious.",
          businessType: "Charitable organisation",
          address: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
          },
        },
        {
          name: "Willy Wonka Candy",
          description: "We make the best candy in the world. Please buy some it is yummy.",
          businessType: "Accommodation and Food Services",
          address: {
            streetNumber: "3/24",
            streetName: "Ilam Road",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand",
            postcode: "90210"
          },
        },
      ],
    }
  },

  methods: {
    /**
     * format the description so that it fits within the column in the table properly.
     * It keeps the first 20 characters and then adds ...
     *
     * @param description
     * @return formattedDescription
     */
    formatDescription(description) {
      if (description === "" || description.length <= 20) {
        return description.trim();
      }
      const showTenWordDescription = description.slice(0, 20).trim();
      return `${showTenWordDescription}${showTenWordDescription.endsWith('.') ? '..' : '...'}`;
    },

    /**
     * Formats the address to show the suburb if one exists, followed by the city and the country
     * which will always exist. These are combined into a string separeted by commas.
     *
     * @param address
     * @return formattedAddress
     */
    formatAddress: function (address) {
      return `${address.suburb ? address.suburb + ', ' : ''}${address.city}, ${address.country}`;
    },
  },

  computed: {
    /**
     * The rows function just computed how many pages in the search table.
     * @returns {number}
     */
    totalItems() {
      return this.items.length;
    },

    fields() {
      return [
        {
          key: 'name',
          //label: 'F name',
          sortable: true
        },
        {
          key: 'description',
          sortable: true,
          formatter: 'formatDescription',
        },
        {
          key: 'businessType',
          sortable: true,
        },{
          key: 'address',
          sortable: false,
          formatter: "formatAddress"
        }]
    }
  }
}
</script>
