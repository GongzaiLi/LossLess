<!--
Sales Report Page for a Business.
Date: sprint_6
-->
<template>
  <div>
    <b-card v-if="canViewReport" class="shadow mw-100">
      <template #header>
        <h4 class="mb-1">{{ business.name }}'s Sale Report</h4>
      </template>

      <DateRangeInput/>
    </b-card>
    <b-card id="inventory-locked-card" v-if="!canViewReport">
      <b-card-title>
        <b-icon-lock/>
        Permission Denied
      </b-card-title>
      <h6 v-if="businessNameIfAdminOfThisBusiness"><strong>You're an administrator of this business. To view this
        sales report, you must be acting as this business.</strong>
        <br><br>To do so, click your profile picture on top-right of the screen. Then, select the name of this business
        ('{{ businessNameIfAdminOfThisBusiness }}') from the drop-down menu.</h6>
      <h6 v-else> You are not an administrator of this business. If you need to view this sales report, contact the
        administrators of the business. <br>
        Return to the business profile page
        <router-link :to="'/businesses/' + $route.params.id">here.</router-link>
      </h6>
    </b-card>
  </div>

</template>
<script>
import api from "../../Api";
import DateRangeInput from "./DateRangeInput";
export default {
  name: "sales-report-page",
  components: {DateRangeInput},
  data: function () {
    return {
      business: {},
    }
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.getBusiness(businessId);
  },

  methods: {
    getBusiness: function(id){
      api
          .getBusiness(id)
          .then((response) => {
            this.business = response.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    }
  },

  computed: {
    /**
     * True if the user can view this report (ie they are an admin or acting as this business)
     */
    canViewReport: function () {
      return this.$currentUser.role !== 'user' ||
          (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },

    /**
     * Returns the name of the business if the user is an admin of this business, otherwise returns null
     */
    businessNameIfAdminOfThisBusiness: function () {
      for (const business of this.$currentUser.businessesAdministered) {
        if (business.id === parseInt(this.$route.params.id)) {
          return business.name;
        }
      }
      return null;
    }
  }
}
</script>