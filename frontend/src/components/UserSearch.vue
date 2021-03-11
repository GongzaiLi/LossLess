<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>
    <form>
      <div>
        <input type="search" v-model="searchQuery" s ize="30" autofocus/>
        <button @click="displayResults"> Search </button>
      </div>
      <br><br><br>
    </form>

    <div>
      <b-table sticky-header striped hover :fields="fields" :items="items" style="width: fit-content"></b-table>
      <b-pagination></b-pagination>>
    </div>

  </div>
</template>


<script>
import api from "../Api";

export default {
  data: function () {
    return {
      searchQuery: "",

      items: [],
      fields: [

        {
          key: ['id', 'firstName'],
          sortable: true
        },

        {
          key: 'lastName',
          sortable: true
        },
        {
          key: 'middleName',
          sortable: true
        },
        {
          key: 'nickname',
          sortable: true
        },
        {
          key: 'bio',
          sortable: true
        },
        {
          key: 'email',
          sortable: true
        },
        {
          key: 'dateOfBirth',
          sortable: true
        },
        {
          key: 'phoneNumber',
          sortable: true
        },
        {
          key: 'homeAddress',
          sortable: true
        },
        {
          key: 'created',
          sortable: true
        },
        {
          key: 'role',
          sortable: true
        },
        {
          key: 'businessesAdministered',
          sortable: true
        },
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
          this.items = response.data;  //Add functionality to return results based on query
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to load user data";
        })
      console.log(this.items);
    }
  },
  computed: {

  }
}
</script>

<style>
table, th, td {
  border: 2px solid black;
  border-collapse: collapse;
  padding: 5px;
  text-align: left;

}

</style>