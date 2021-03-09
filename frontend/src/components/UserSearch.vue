<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>

    <input type="search" v-model="searchQuery" size="30" autofocus/>
    <button @click="searchUser(searchQuery)"> Search</button>

    <br><br><br>
    <table style="width: 100%">
      <caption>Search Results</caption>
      <tr>
        <th>Firstname</th>
        <th>Lastname</th>
        <th>City</th>
        <th>Country</th>
        <th>Email</th>

      </tr>
      <tr>
        <td>Jill</td>
        <td>Smith</td>
        <td>Christchurch</td>
        <td>New Zealand</td>
        <td>jill@gmail.com</td>
      </tr>
      <tr>
        <td>Eve</td>
        <td>Jackson</td>
        <td>Dunedin</td>
        <td>New Zealand</td>
        <td>Eve@gmail.com</td>
      </tr>
      <tr>
        <td>Bob</td>
        <td>Jones</td>
        <td>Sydney</td>
        <td>Australia</td>
        <td>Bob@outlook.com</td>
      </tr>
    </table>

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