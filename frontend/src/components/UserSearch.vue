<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim, Gongzai Li
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>
    <b-row style="height: 50px">
      <b-col cols="7">
        <b-form-input v-model="searchQuery" @keyup.enter="displayResults(searchQuery)" type="search" placeholder="Search"></b-form-input>
      </b-col>
      <b-col cols="0">
        <b-button @click="displayResults(searchQuery)"> Search</b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col><!--responsive sticky-header="500px"-->
          <h3 v-if="items.length === 0" class="no-results-overlay" >No results to display</h3>
        <b-table striped hover
         table-class="text-nowrap"
         responsive="sm"
         no-border-collapse
         bordered
         @row-clicked="rowClickHandler"
         :fields="fields"
         :per-page="perPage"
         :items="items"
         :current-page="currentPage"
         class="user-search-table">
        </b-table>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage"></b-pagination>
      </b-col>
      <b-col style="text-align: right; margin-top: 6px">
        Displaying {{ itemsRangeMin }} - {{ itemsRangeMax }} of total {{ totalResults }} results.
      </b-col>
    </b-row>
  </div>
</template>

<style>
.user-search-table {
  min-height: 36em;
  table-layout: fixed;
  border-top: 1px solid #dee2e6;
  border-left: 3px solid #dee2e6;
  border-right: 3px solid #dee2e6;
  border-bottom: 3px solid #dee2e6;
}
.no-results-overlay {
  position:absolute;
  width:100%;
  margin-top:9em;
  height:100%;
  z-index:999;
  text-align: center;
}
</style>

<script>
import api from "../Api";

export default {
  data: function () {
    return {
      searchQuery: "",
      totalResults: 0,
      perPage: 10,
      currentPage: 1,
      items: [],
      fields: [
        {
          key: 'name',
          //label: 'F name',
          sortable: true
        },
        {
          key: 'nickname',
          sortable: true
        },
        {
          key: 'email',
          sortable: true
        },
        {
          key: 'homeAddress',
          sortable: true
        }
      ]
    }
  },
  methods: {
    /**
     * When called changes page to the profile page based on the id of the user clicked
     */
    rowClickHandler: function(record){
      this.$router.push({path: `/user/${record.id}`});
    },
    /**
     * the function is search a user id the using api to find the user's detail
     * @param searchParameter id user is id or name other details
     */
    displayResults: function (searchParameter) {
      api
        .searchUser(searchParameter)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          if (searchParameter.trim().length) {
            this.items = this.getUserInfoIntoTable(response.data);  //Add functionality to return results based on query
            this.totalResults = response.data.length;
            this.resultsReturned = true;
          } else {
            this.items = this.getUserInfoIntoTable([]); // if the searchParameter is empty then the response.date will be []
            this.totalResults = 0;
          }
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to load user data";
        })
    },

    /**
     * The getUserInfoIntoTable function to read the data from the APi response.Date then put a array (item)
     * The Array hold the an object {name: 'name', nickname: 'nickName', email: 'Email', homeAddress: 'address'}.
     * @returns [{name: 'name', nickname: 'nickName', email: 'Email', homeAddress: 'address'}, .......]
     */
    getUserInfoIntoTable: function (data) {
      let items = [];
      let tableHeader = {
        name: '',
        nickname: '',
        email: '',
        homeAddress: ''
      };
      for (const user of data) {
        tableHeader = user;
        tableHeader.name = `${user.firstName} ${user.middleName} ${user.lastName}`;
        items.push(tableHeader);
      }
      return items;
    }
  },

  computed: {
    /**
     * The rows function just computed how many pages in the search table.
     * @returns {number}
     */
    rows() {
      return this.items.length;
    },

    /**
     * Author: Nitish Singh
     * Computes current number of items displaying on the table.
     * @returns {number}
     */
    getCurrentPageItems() {
      let numPages = Math.ceil(this.totalResults / this.perPage); //Max number of pages

      if (this.currentPage === numPages) {
        return this.totalResults % this.perPage;
      } else if (this.totalResults === 0) {
        return 0;
      } else {
        return this.perPage;
      }
    },

    /**
     * Author: Nitish Singh
     * Computes the lower range of items displaying on the table.
     * Ranges (indexes) starts from 1, so plus 1
     * @returns {number}
     */
    itemsRangeMin() {
      let minRange = this.perPage * (this.currentPage - 1);
      if (this.totalResults > 0) {
        minRange++;
      }
      return minRange;
    },
    /**
     * Author: Nitish Singh
     * Computes the upper range of items displaying on the table.
     * @returns {number}
     */

    itemsRangeMax() {// 1000 t 1000 p
      let numPages = Math.ceil(this.totalResults / this.perPage); //Max number of pages

      if (this.currentPage === numPages) {
        if (this.totalResults !== this.perPage) {
          return this.perPage * (this.currentPage - 1) + (this.totalResults % this.perPage);
        }
        else {
          return this.totalResults;
        }
      }
      else {
        return this.perPage * (this.currentPage - 1) + (this.getCurrentPageItems);
      }
    }
  }
}
</script>
