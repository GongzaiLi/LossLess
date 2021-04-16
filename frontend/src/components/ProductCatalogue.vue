<!--
Page that stores table show the business products
Author: Gongzai Li
Date: 15/4/2021
-->
<template>

  <div>
    <h2>Product Catalogue</h2>
    <div v-show="productFound">
      <b-table striped hover
               table-class="text-nowrap"
               responsive="lg"
               no-border-collapse
               bordered
               stacked="sm"
               :fields="fields"
               :items="items"
               :per-page="perPage"
               :current-page="currentPage"
      >
      </b-table>
      <b-col>
        <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage"></b-pagination>
      </b-col>
    </div>

    <div v-show="!productFound" class="no-results-overlay">
      <h4>No Product to display</h4>
      <createButton></createButton>
    </div>
  </div>
</template>

<style>
.no-results-overlay {
  margin-top: 7em;
  margin-bottom: 7em;
  text-align: center;
}

h2 {
  text-align: center;
}
h4 {
  color: #880000;
}
</style>

<script>
import api from "../Api";
import Vue from 'vue'




export default {
  component: {
    createButton: Vue.component('createButton', {
      template: `<b-button>Create</b-button>`
    })
  },
  data: function () {
    return {
      fields: [{
        key: 'id',
        label: 'ID',
        sortable: true
      },
        {
          key: 'name',
          label: 'Name',
          sortable: true
        },
        {
          key: 'description',
          label: 'Description',
          formatter: (value) => {
            return `${value.split(0, 10)}...`;
          },
          sortable: true
        },
        {
          key: 'recommendedRetailPrice',
          label: 'RRP',
          sortable: true
        },
        {
          key: 'created',
          label: 'Created',
          formatter: (value) => {
            const date = new Date(value);
            return `${date.getUTCDate()}/${date.getUTCMonth() + 1}/${date.getUTCFullYear()}`;
          },
          sortable: true
        }],
      items: [],
      productFound: false,
      perPage: 10,
      currentPage: 0
    }
  },
  mounted() {
    const businessId = this.$route.params.id;
    this.getProducts(businessId);

  },
  methods: {
    /**
     * this is a get api which can take Specific business product data keep in the items and then show in the table
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param businessId
     */
    //todo need check the api is work
    getProducts: function (businessId) {
      api
        .getProducts(businessId)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.setResponseData(response.data);
        })
        .catch((error) => {
          this.$log.debug(error);
        })
      // fake date can use be test.

      this.items = [
        {
          id: "WATT-420-BEANS1",
          name: "Watties Baked Beans - 420g can",
          description: "Baked Beans as they should be.",
          recommendedRetailPrice: 2.2,
          created: "2021-04-14T13:01:58.660Z"
        },
        {
          id: "WATT-420-BEANS2",
          name: "Watties Baked Beans - 420g can",
          description: "Baked Beans as they should be.",
          recommendedRetailPrice: 2.2,
          created: "2021-04-14T13:01:58.660Z"
        },
        {
          id: "WATT-420-BEANS3",
          name: "Watties Baked Beans - 420g can",
          description: "Baked Beans as they should be.",
          recommendedRetailPrice: 2.2,
          created: "2021-04-14T13:01:58.660Z"
        }
      ];

    },
    /**
     * set the response data to items
     * @param data
     */
    //todo may need rebuilt the data form.
    setResponseData: function (data) {
      this.items = data;
    }
  },
  computed: {
    /**
     * The rows function just computed how many pages in the search table.
     * @returns {number}
     */
    rows() {
      return this.items.length;
    },

  }

}
</script>