<!--
Page that stores table show the business products
Author: Gongzai Li
Date: 15/4/2021
-->
<template>
  <div>
    <h2 align="center">Product Catalogue</h2>
    <b-table striped hover
             table-class="text-nowrap"
             responsive="lg"
             no-border-collapse
             bordered
             stacked="sm"
             show-empty
             :fields="fields"
             :items="items">
      <template #empty>
        <h3 class="no-results-overlay">No Product to display</h3>
      </template>
    </b-table>


  </div>
</template>

<script>
import api from "../Api";

export default {
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
      items: []
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
      // fake date can use to be test.
      /*
      this.items = [
        {
          id: "WATT-420-BEANS",
          name: "Watties Baked Beans - 420g can",
          description: "Baked Beans as they should be.",
          recommendedRetailPrice: 2.2,
          created: "2021-04-14T13:01:58.660Z"
        }
      ];
      */
    },
    /**
     * set the response data to items
     * @param data
     */
    //todo may need rebuilt the data form.
    setResponseData: function (data) {
      this.items = data;
    }
  }

}
</script>

<style scoped>

</style>