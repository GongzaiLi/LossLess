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
        <DateRangeInput/>
        </b-list-group-item>
        <b-list-group-item v-if="totalResults">
          <b-card-text>
            <h3>Sales Summary for {{totalResults.startDate}} to {{totalResults.endDate}}</h3>
            <b-row align-h="start" >
              <b-col cols="4">
              <h4>{{totalResults.totalPurchases}} Total Items Sold </h4>
              </b-col>
              <b-col cols="4">
             <h4> {{ currency.symbol }}{{totalResults.totalValue}} {{currency.code}} Total Value</h4>
              </b-col>
            </b-row>
          </b-card-text>
        </b-list-group-item>
        <b-list-group-item>
          <b-row>
          <b-col cols="2"><h3>Sales Details</h3></b-col>
            <b-col cols="2"><b-select v-model="groupBy" :options="groupByOptions"></b-select></b-col>
          </b-row>
          <b-row>
          <b-col>
            <b-table striped :items="groupedResults" :fields="fields" bordered>
            </b-table>
          </b-col>
            <b-col>
              graph
            </b-col>
          </b-row>
        </b-list-group-item>
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
import api from "../../Api";
import DateRangeInput from "./DateRangeInput";
export default {
  name: "sales-report-page",
  components: {DateRangeInput},
  data: function () {
    return {
      business: {},
      currency:{},
      groupBy:"day",
      groupByOptions:[
        {value:"day", text:"Daily"},
        {value:"week", text:"Weekly"},
        { value:"month", text:"Monthly"},
        {value:"year", text:"Yearly"}
      ],
      fields:[
        { key: 'startDate', sortable: true },
        { key: 'endDate', sortable: true },
        { key: 'totalPurchases', sortable: true },
        { key: 'totalValue', sortable: true }
      ],
      totalResults:{
        "startDate": "2021-09-01",
        "endDate": "2021-09-04",
        "totalPurchases": 999999999999,
        "totalValue": 99999999999
      },
      groupedResults:
        [
            {
              "startDate": "2021-09-01",
              "endDate": "2021-09-01",
              "totalPurchases": 0,
              "totalValue": 0.0
            },
    {
      "startDate": "2021-09-02",
        "endDate": "2021-09-02",
        "totalPurchases": 0,
        "totalValue": 0.0
    },
    {
      "startDate": "2021-09-03",
        "endDate": "2021-09-03",
        "totalPurchases": 0,
        "totalValue": 0.0
    },
    {
      "startDate": "2021-09-04",
        "endDate": "2021-09-04",
        "totalPurchases": 1,
        "totalValue": 2.5
    }
  ]
      ,
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
            this.getCurrency(this.business)
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },
    async getCurrency(business) {
      this.currency = await api.getUserCurrency(business.address.country);
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
    },
  }
}
</script>