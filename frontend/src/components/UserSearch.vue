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
      <b-col><!--responsive sticky-header="500px"-->
        <b-table striped hover
                 table-class="text-nowrap"
                 responsive="sm"
                 no-border-collapse
                 sticky-header="500px"
                 bordered
                 :fields="fields"
                 :items="items"
                 :per-page="perPage"
                 :current-page="currentPage"
                 style="table-layout: fixed">
        </b-table>
      </b-col>
    </b-row>
    <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage"></b-pagination>
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
      items: this.items = Array(this.perPage).fill({
        name: '-',
      }),
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
     * the function is search a user id the using api to find the user's detail
     * @param id user is id or name other details
     */
    displayResults: function (searchParameter) {
      api
        .getUser(searchParameter)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          if (searchParameter.trim().length) {
            this.items = this.getUserInfoIntoTable(response.data);  //Add functionality to return results based on query
          } else {
            this.items = Array(this.perPage).fill({
              name: '-',
            });
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
      for (let i = 0; i < items.length % this.perPage; i++) {
        tableHeader = {
          name: '-',
        }
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
    }
  }
}
</script>
