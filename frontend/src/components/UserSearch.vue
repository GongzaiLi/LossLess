<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>

    <input type="search" v-model="searchQuery" size="30" autofocus />
    <button> Search </button>
    <button @click="populateTable"> Populate Table </button>


    <br><br><br>
    <div>
      <b-table striped hover :items="items" style="width: fit-content"></b-table>
    </div>
  </div>
</template>


<script>
import api from "../Api";

export default {
  data: function () {
    return {
      searchQuery: "",
      userData: {
        firstName: "",
        lastName: "",
        city: "",
        country: "",
        email: ""
      }
    }
  },
  methods: {

    /**
     * the function is search a user id the using api to find the user's detail
     * @param id user is id or name other details
     */
    searchUser: function (searchParameter) {
      api
        .getUser(searchParameter)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.userData = response.data;
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to load user data";
        })
      items: [],
    },
    /**
     * Author: Nitish Singh
     * Sends a get request to the API and populates the tables
     * with data received from the API.
     */
    populateTable() {
      axios
      .get("https://virtserver.swaggerhub.com/nsi60/S302T29_Mock/3.0.0/users/search?searchQuery=" + this.searchQuery)
      .then(res => (this.items = res.data, console.log(res)))
      .catch(err => console.log(err));
    }
  }
}
</script>

<style>
table, th, td {
  border: 1px solid black;
  border-collapse: collapse;
  padding: 15px;
  text-align: left;

}

</style>