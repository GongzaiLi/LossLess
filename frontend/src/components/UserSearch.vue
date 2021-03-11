<!--
Page that stores table and search bar to search for other users
Author: Caleb Sim
Date: 7/3/2021
-->
<template>
  <div>
    <h2>Search For a User</h2>

    <!--
    <form>
      <div>
        <input type="search" v-model="searchQuery" s ize="30" autofocus/>
        <button @click="displayResults"> Search </button>
      </div>
      <br><br><br>
    </form>
    -->

    <div>
      <b-row>
        <b-col md="8">
          <b-form-input v-model="searchQuery" type="search" placeholder="Search"></b-form-input>
        </b-col>
      </b-row>
      <b-row id="window">
        <b-col >
          <b-table striped hover
                   :headers="headers"
                   :fields="fields"
                   :items="items"
                   :filter="searchQuery"
                   :per-page="perPage"
                   :current-page="currentPage"
                   style="width: fit-content">
          </b-table>
          <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage"></b-pagination>
        </b-col>
      </b-row>


    </div>

  </div>
</template>


<script>
import api from "../Api";


export default {
  data: function () {
    return {
      searchQuery: "",
      perPage: 10,
      currentPage: 1,
      items: [],
      headers: [
        {
          sortable: true,
          value: 'id'
        },
        {value: 'id'},
        {value: 'firstName'},
        {value: 'lastName'},
        {value: 'middleName'},
        {value: 'nickname'},
        {value: 'bio'},
        {value: 'email'},
        {value: 'dateOfBirth'},
        {value: 'phoneNumber'},
        {value: 'homeAddress'},
        {value: 'created'},
        {value: 'role'},
        {value: 'role'}
      ],
      fields: [
        {
          key: 'id',
          sortable: true
        },
        {
          key: 'firstName',
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
    rows() {
      return this.items.length;
    }
  },
  mounted() {
    this.displayResults();
  }
}
</script>

<style>
/*table, th, td {*/
/*  border: 1px solid black;*/
/*  padding: 5px;*/
/*  text-align: left;*/

/*}*/

/*table {*/
/*  border-collapse: collapse;*/
/*}*/

/*th {*/
/*  height: 50px;*/
/*}*/
#window {
  float: left;
}
</style>
