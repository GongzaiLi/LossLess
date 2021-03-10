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
    <button @click="populateTable"> Change Data </button>


    <br><br><br>
    <div>
      <b-table striped hover :items="items2"></b-table>
    </div>
  </div>
</template>


<script>
const data = require('./users.json');
const users = data.users;
const notUsers = [ { id: 1, firstName: 40, lastName: 'Dickerson', city: 'Macdonald', country: ' ', email: ''},]
console.log(users);

export default {
  data: function () {
    return {
      searchQuery: "",
      items2: [ { id: 1, firstName: 40, lastName: 'Dickerson', city: 'Macdonald', country: ' ', email: ''},],
      items: [
        { id: 1, firstName: 40, lastName: 'Dickerson', city: 'Macdonald', country: ' ', email: ''},
        { id: 1, firstName: 40, lastName: 'Dickerson', city: 'Macdonald', country: ' ', email: ''},
        { id: 1, firstName: 40, lastName: 'Dickerson', city: 'Macdonald', country: ' ', email: ''},
        { id: 1, firstName: 40, lastName: 'Dickerson', city: 'Macdonald', country: ' ', email: ''},
      ]
    }
  },
  methods: {
    populateTable() {
      if (this.items2.length > 1) {
        this.items2 = notUsers;
      } else {
        this.items2 = users;
      }
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