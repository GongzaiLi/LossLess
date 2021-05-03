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
        <b-form-input v-model="searchQuery" @keyup.enter="displayResults(searchQuery)" type="search"
                      placeholder="Search"></b-form-input>
      </b-col>
      <b-col cols="0">
        <b-button @click="displayResults(searchQuery)"> Search</b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="12"><!--responsive sticky-header="500px"-->
        <b-table striped hover
         table-class="text-nowrap"
         responsive="lg"
         no-border-collapse
         bordered
         stacked="sm"
         show-empty
         @row-clicked="rowClickHandler"
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
    <pagination :per-page="perPage" :total-items="totalItems" v-model="currentPage" v-show="items.length"/>
  </div>
</template>

<style>
.no-results-overlay {
  margin-top:7em;
  margin-bottom:7em;
  text-align: center;
}
</style>

<script>
import api from "../../Api";
import pagination from "../model/Pagination";

export default {
  components: {
    pagination,
  },
  name: 'UserSearch', // DO NOT DELETE!!! The <keep-alive include="UserSearch"> in App.vue only matches component names so we register a name here.
  data: function () {
    return {
      searchQuery: "",
      totalResults: 0,
      perPage: 10,
      currentPage: 1,
      items: [],
    }
  },
  methods: {
    /**
     * When called changes page to the profile page based on the id of the user clicked
     */
    rowClickHandler: function (record) {
      this.$router.push({path: `/users/${record.id}`});
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
        if (this.$currentUser.role !== "user") {
          tableHeader.userType = `${this.getUserRoleString(user)}`;
        }
        tableHeader.location = `${user.homeAddress.city}, ${user.homeAddress.region}, ${user.homeAddress.country}`
        items.push(tableHeader);
      }
      return items;
    },
    /**
     * Given a user object, returns a string representation of the user's role
     * to be displayed in the table. Example values: 'USER', 'ADMIN'
     */
    getUserRoleString(user) {
      let roleLabel;
      if (user.role === "globalApplicationAdmin") {
        roleLabel = "ADMIN";
      } else if (user.role === "defaultGlobalApplicationAdmin") {
        roleLabel = "DEFAULT ADMIN";
      } else {
        roleLabel = "USER";
      }
      return roleLabel;
    }
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
      let fields = [
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
        },{
          key: 'location',
          sortable: true
        },
      ];
      if (this.$currentUser && this.$currentUser.role !== 'user') {
        fields.push({
          key: 'userType',
          sortable: true
        });
      }
      return fields;
    }
  }
}
</script>
