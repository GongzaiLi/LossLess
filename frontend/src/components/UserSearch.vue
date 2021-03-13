<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim, Gongzai Li
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>
    <b-row style="height: 50px">
      <b-col>
        <form class="form-inline">
          <div class="form-group">
            <b-form-input v-model="searchQuery" type="search" placeholder="Search"></b-form-input>
            <b-button @click="displayResults(searchQuery)" style="margin-left:15px;"> Search</b-button>
          </div>
        </form>
      </b-col>
    </b-row>
    <b-row>
      <b-col><!--responsive-->
        <b-table striped hover
                 table-class="text-nowrap"
                 responsive="sm"
                 no-border-collapse
                 bordered
                 :fields="fields"
                 :items="items"
                 :per-page="perPage"
                 :current-page="currentPage"
                 style="table-layout: fixed; table-layout: fixed">
        </b-table>
        <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage"></b-pagination>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import api from "../Api";

export default {
  data: function () {
    return {
      searchQuery: "",
      perPage: 10,
      currentPage: 1,
      items: Array(10).fill({}),
      fields: [
        {
          key: 'firstName',
          //label: 'F name',
          sortable: true
        },
        {
          key: 'lastName',
          sortable: true
        },
        {
          key: 'email',
          sortable: true
        },
        {
          key: 'homeAddress',
          sortable: true
        },
        {
          key: 'role',
          sortable: true
        }
      ]
    }
  },

  methods: {
    /**
     * the function is search a user id the using api to find the user's detail
     * @param id user is id or name other details
     */
    displayResults: function (searchParameter) {
        api
          .getUser(searchParameter)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            if (searchParameter.trim().length) {
              this.items = response.data;  //Add functionality to return results based on query
            } else {
              this.items = Array(10).fill({});
            }

          })
          .catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to load user data";
          })
      }
  },
  computed: {
    /**
     * The rows function just computed how many pages in the search table.
     * @returns {number}
     */
    rows() {
      return this.items.length;
    }
  }
}
</script>

<style>
</style>
