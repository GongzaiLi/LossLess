<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>

    <input type="search" v-model="searchQuery" size="30" autofocus />
    <button @click="displayResults"> Search </button>


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
      items: []
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
          this.items = response.data;  //Add functionality to return results based on query
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to load user data";
        })
    }
  }
}
</script>

<style>
table, th, td {
  border: 1px solid black;
  border-collapse: collapse;
  padding: 5px;
  text-align: left;

}

</style>