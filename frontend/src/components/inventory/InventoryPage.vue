<!--
Page that stores table to show the business inventory
Date: 11/5/2021
-->
<template>
  <div>
    <b-card>
      <b-card-title>Inventory: {{businessName}}</b-card-title>
      <hr class='m-0'>
    </b-card>
  </div>
</template>

<script>
import api from "../../Api";

export default {
  data: function () {
    return {
      businessName: "",
    }
  },
  mounted() {
    const businessId = this.$route.params.id
    this.getBusinessInfo(businessId);
  },
  methods: {
    /**
     * this is a get api to get the name of the business
     * NOTE!! Best to add currency stuff here as well similar to the products
     * @param businessId
     */
    getBusinessInfo: async function (businessId) {
      api.getBusiness(businessId)
          .then((resp) => {
            this.businessName = resp.data.name;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    }
  }
}
</script>

