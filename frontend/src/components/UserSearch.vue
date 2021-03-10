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
import axios from 'axios';

export default {
  data: function () {
    return {
      searchQuery: "",
      items: [],
    }
  },
  methods: {
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