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

    <br><br><br>
    <table style="width: 100%" >
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
export default {
  data: function () {
    return {
      searchQuery: ""
    }
  }
}
</script>

<style scoped>

</style>