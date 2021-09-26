<!--
Sales Report Page for a Business.
Date: sprint_6
-->
<template>
  <div>
    <b-card v-if="canViewReport" class="shadow mw-100" no-body>
      <b-list-group>
        <b-list-group-item>
          <h3 class="mb-1">{{ business.name }}'s Sale Report</h3>
          <DateRangeInput v-model="dateRange" @input="gotReport=true" :all-time-start="new Date(business.created)"/>
        </b-list-group-item>
      <b-tabs fill v-if="gotReport">
        <b-tab title="Sales">
          <sales-report-tab :date-range="dateRange" :currency="currency"/>
        </b-tab>
        <b-tab title="Products" >
          <top-report :is-top-products="true" :date-range="dateRange" :currency="currency"></top-report>
        </b-tab>
        <b-tab title="Manufacturers">
          <top-report :is-top-products="false" :date-range="dateRange" :currency="currency"></top-report>
        </b-tab>
        <b-tab title="Listings">
          <listings-durations-graph :date-range="dateRange"/>
        </b-tab>
      </b-tabs>
      </b-list-group>
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
import Api from "../../Api";
import DateRangeInput from "./DateRangeInput";
import SalesReportTab from "./SalesReportTab";
import TopReport from "./TopReport";
import ListingsDurationsGraph from "./ListingsDurationsGraph";


export default {
  name: "sales-report-page",
  components: {SalesReportTab, DateRangeInput, TopReport, ListingsDurationsGraph},
  data: function () {
    return {
      business: {},
      currency: {},
      dateRange: [],
      loading: false,
      gotReport: false
    }
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.getBusiness(businessId);
  },

  methods: {
    /**
     * this is a get Api which can take Specific business to display on the page
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param id
     **/
    getBusiness: function (id) {              this.loadingTable = false;
      Api
          .getBusiness(id)
          .then((response) => {
            this.business = response.data;
            this.getCurrency(this.business)
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },

    /**
     * Queries the currencies API to get currency info for the business
     **/
    async getCurrency(business) {
      this.currency = await Api.getUserCurrency(business.address.country);
    },
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
    },
  },
}
</script>
