<!--
Page that stores table to show the business inventory
Date: 11/5/2021
-->
<template>
  <div>
    <b-card v-if="canEditInventory">
      <b-card-title>Inventory: {{businessName}}</b-card-title>
      <hr class='m-0'>
    </b-card>

    <b-card id="inventory-locked-card" v-if="!canEditInventory">
      <b-card-title>
        <b-icon-lock/> Can't edit inventory page
      </b-card-title>
      <h6 v-if="businessNameIfAdminOfThisBusiness"><strong>You're an administrator of this business. To edit this inventory, you must be acting as this business.</strong>
        <br><br>To do so, click your profile picture on top-right of the screen. Then, select the name of this business ('{{businessNameIfAdminOfThisBusiness}}') from the drop-down menu.</h6>
      <h6 v-else> You are not an administrator of this business. If you need to edit this inventory, contact the administrators of the business. <br>
        Return to the business profile page <router-link :to="'/businesses/' + $route.params.id">here.</router-link></h6>
    </b-card>
  </div>
</template>

<script>
import api from "../../Api";

export default {
  data: function () {
    return {
      businessName: "",
      currentUser: {},
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
  },

  computed: {

    /**
     * True if the user can edit this catalogue (ie they are an admin or acting as this business)
     */
    canEditInventory: function() {
      return this.$currentUser.role !== 'user' ||
          (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },

    /**
     * Returns the name of the business if the user is an admin of this business, otherwise returns null
     */
    businessNameIfAdminOfThisBusiness: function() {
      for (const business of this.$currentUser.businessesAdministered) {
        if (business.id === parseInt(this.$route.params.id)) {
          return business.name;
        }
      }
      return null;
    }
  },

  watch: {
    /**
     * Watches the current user data (specifically, who the user is acting as). If this changes to someone without permission
     * to access the inventory, then a modal is shown informing them that they will be redirected to the business's home page.
     */
    $currentUser: {
      handler() {
        if (this.canEditInventory) {
          this.getBusinessInfo(this.$route.params.id);
        }
      },
      deep: true, // So we can watch all the subproperties (eg. currentlyActingAs)
    },
    /**
     * This watches for those routing changes, and will update the profile with the catalogue of the business specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      const id = to.params.id;
      if (this.canEditInventory) {
        this.getBusinessInfo(id);
      }
    },
  }
}
</script>

