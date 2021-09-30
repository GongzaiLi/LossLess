<!--
Page that stores table and search bar to search for other users
Date: sprint_1
-->
<template>
  <b-card style="max-width: 1200px">
    <b-row style="height: 50px">
      <b-col cols="7">
        <b-form-input v-model="searchQuery" @keyup.enter="displayResults(searchQuery)" type="search"
                      placeholder="Search users"></b-form-input>
      </b-col>
      <b-col cols="0">
        <b-button variant="primary" @click="displayResults(searchQuery)"> Search</b-button>
      </b-col>
    </b-row>
    <b-row class="userRow">
      <b-col cols="12">
        <b-table striped hover
                 ref="searchTable"
         class="text-nowrap"
         responsive
         no-border-collapse
         bordered
         no-local-sorting
         :sort-by.sync="sortBy"
         :sort-desc.sync="sortDesc"
         show-empty
         @row-clicked="rowClickHandler"
         :fields="fields"
         :per-page="perPage"
         :items="myProvider"
         :current-page="currentPage">
         <template #empty>
          <h3 class="no-results-overlay" >No results to display</h3>
        </template>
          <template #cell(name)="data">
            <b-img :src="data.item.profileImage ? getURL(data.item.profileImage.thumbnailFilename) : require('../../../public/user-profile-default.png')"
                   alt="User Profile Image" class="rounded-circle" style="margin-left: 5px; position: relative; height: 1.5rem; width:1.5rem"></b-img>
            {{createUsername(data.item)}}
          </template>
        </b-table>
      </b-col>
    </b-row>
    <pagination ref="tablePag" :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="totalResults"/>
  </b-card>
</template>

<style scoped>

.userRow {
  cursor: pointer;
}

.no-results-overlay {
  margin-top:7em;
  margin-bottom:7em;
  text-align: center;
}

@media (max-width: 992px) {
  .text-nowrap {
    white-space: normal !important;
  }
}

</style>


<script>
import api from "../../Api";
import pagination from "../model/Pagination";
import {formatAddress} from "../../util";

export default {
  components: {
    pagination,
  },
  props: {isMakeAdmin: {default: false, type: Boolean}},
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
            case "nickname":
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
     * The Array hold the an object {name: 'name', nickname: 'nickname', email: 'Email', homeAddress: 'address'}.
     * @returns [{name: 'name', nickname: 'nickName', email: 'Email', homeAddress: 'address'}, .......]
     */
    getUserInfoIntoTable: function (data) {
      let items = [];
      let tableHeader = {};
      for (const user of data) {
        tableHeader = user;
        if (this.$currentUser.role !== "user") {
          tableHeader.userType = `${this.getUserRoleString(user)}`;
        }
        tableHeader.location = formatAddress(user.homeAddress, 3);
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
    },

    /**
     * @user user information returned from backend
     * @returns String compiled from the user's first name, middle name - if exists and lastname
     */
    createUsername(user){
      return `${user.firstName} ${user.middleName || ''} ${user.lastName}`;
    },
    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return api.getImage(imageFileName);
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
      let fields = [
        {
          key: 'name',
          sortable: true
        },
        {
          key: 'nickname',
          sortable: true
        },
        {
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
    },
  }
}
</script>
