<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim, Gongzai Li
Date: 7/3/2021
-->
<template>
  <b-card style="max-width: 1200px">
    <h2 v-bind:hidden=isMakeAdmin>Search For a User</h2>
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
         @row-clicked="rowClickHandler"
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
import api from "../../Api";
import pagination from "../model/Pagination";

export default {
  components: {
    pagination,
  },
  props: {isMakeAdmin: {default: false, type: Boolean}},
  name: 'UserSearch', // DO NOT DELETE!!! The <keep-alive include="UserSearch"> in App.vue only matches component names so we register a name here.
  data: function () {
    return {
      searchQuery: "",
      setQuery:"",
      totalResults: 0,
      sortDesc: "",
      sortBy: "",
      perPage: 10,
      currentPage: 1,
      items: [],
      isMakeAdminModal: false,
    }
  },
  mounted(){
    this.isMakeAdminModal = this.isMakeAdmin
  },
  methods: {
    /**
     * When called changes page to the profile page based on the id of the user clicked
     */
    rowClickHandler: function (record) {
      if (this.isMakeAdminModal) {
        this.$emit('rowSelect', record);
      } else {
      this.$router.push({path: `/users/${record.id}`});
      }
    },
    /**
     * the function is search a user id the using api to find the user's detail
     * this is used to make the initial search from the search bar, and resets the table to the first page
     * @param searchParameter id user is id or name other details
     */
    displayResults: function (searchParameter) {
      api
        .searchUser(searchParameter)
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
          this.error = "Failed to load user data";
        })

    },

    /**
     * the function sends a new api call when the page is changed to get a new table back with the correct amount of
     * users with the offset and count
     * @returns [] or [users]
     */
    myProvider: async function (ctx) {
      if(this.totalResults) {
        try {

          let sortDirectionString = "ASC"
          if (this.sortDesc) {
            sortDirectionString = "DESC"
          }

          let sortByParam;
          switch (this.sortBy) {
            case "name":
              sortByParam = "NAME";
              break;
            case "nickName":
              sortByParam = "NICKNAME";
              break;

            case "email":
              sortByParam = "EMAIL";
              break;

            case "role":
              sortByParam = "ROLE";
              break;

            default:
              sortByParam = "NONE";
          }

          const response = await api
              .searchUser(this.searchQuery, ctx.perPage, ctx.currentPage - 1, sortByParam, sortDirectionString)
          return this.getUserInfoIntoTable(response.data.results);
        } catch (error) {
          this.$log.debug(error);
          this.error = "Failed to load user data";
          return []
        }
      }
      return [];
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
        nickName: '',
        email: '',
        homeAddress: ''
      };
      for (const user of data) {
        tableHeader = user;
        tableHeader.name = `${user.firstName} ${user.middleName || ''} ${user.lastName}`;
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
          key: 'nickName',
          sortable: true
        },
        {
          key: 'email',
          sortable: true
        },{
          key: 'location',
          sortable: false
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
