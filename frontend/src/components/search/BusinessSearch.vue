<!--
Page that stores table and search bar to search for businesses
-->
<template>
  <b-card style="max-width: 1200px">
    <b-row style="height: 50px">
      <b-col cols="7">
        <b-form-input v-model="searchQuery" @keyup.enter="searchBusinessApiRequest(searchQuery)" type="search"
                      placeholder="Search businesses"></b-form-input>
      </b-col>
      <b-col cols="0">
        <b-button  @click="searchBusinessApiRequest(searchQuery)"> Search </b-button>
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
                 :sort-by.sync="sortBy"
                 :sort-desc.sync="sortDesc"
                 stacked="sm"
                 show-empty
                 :fields="fields"
                 :per-page="perPage"
                 :items="myProvider"
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
import api from "../../Api";

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
      items: [],
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
      const shortenedDescription = description.slice(0, 20).trim();
      return `${shortenedDescription}${shortenedDescription.endsWith('.') ? '..' : '...'}`;
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

    /**
     * Uses Api.js to send a get request with the searchParameter.
     * This is used to make the initial search using the searchQuery from the form-input and is used to
     * refresh the table.
     *
     * @param searchParameter is the inputted search
     */
    searchBusinessApiRequest: function (searchParameter) {
      api
          .searchBusiness(searchParameter)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.totalResults = response.data.totalItems;
            this.setQuery=this.searchQuery;
            this.$refs.searchTable.refresh();
            this.currentPage=1;
            this.$refs.tablePag.currentPage=1;
          })
          .catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to load business data";
          })

    },

    /**
     * This sends a new api get request when the page is changed either by sorting or changing of pages.
     * This gets the new table data back with the correct amount of
     * businesses with the page and size.
     *
     * @returns list of Businesses
     */
    myProvider: async function () {
      if(this.totalResults) {
        try {
          const response = await api
              .searchBusiness(this.searchQuery, this.perPage, this.currentPage - 1, this.sortBy, ((this.sortDesc) ? "DESC" : "ASC"))
          return response.data.businesses
        } catch (error) {
          this.$log.debug(error);
          this.error = "Failed to load business data";
          return []
        }
      }
      return [];
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
