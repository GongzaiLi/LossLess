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
          <DateRangeInput @input="getSalesReport" :all-time-start="new Date(business.created)"/>
        </b-list-group-item>
        <b-list-group-item v-if="totalResults" id="total-results">
          <b-card-text>
            <h3>Sales Summary for {{ totalResults.startDate }} {{ totalResults.endDate !== totalResults.startDate ? `to ${totalResults.endDate}` : '' }}</h3>
            <b-row align-h="start">
              <b-col cols="4">
                <h4>{{ totalResults.totalPurchases }} Total Items Sold </h4>
              </b-col>
              <b-col cols="4">
                <h4> {{ currency.symbol }}{{ totalResults.totalValue }} {{ currency.code }} Total Value</h4>
              </b-col>
            </b-row>
          </b-card-text>
        </b-list-group-item>
        <b-list-group-item v-show="totalResults">
          <b-row>
            <b-col class="mt-2">
              <b-row>
                <b-col cols="4"><h3>Sales Details</h3></b-col>
                <b-col cols="4">
                  <b-form-select v-model="groupBy" id="periodSelector"
                                 @change="getSalesReport(dateRange)">
                    <option v-for="[option, name] in Object.entries(groupByOptions)" :key="option" :value="option">{{
                        name
                      }}
                    </option>
                  </b-form-select>
                </b-col>
              </b-row>
              <b-row>
                <b-col>
                  <b-table
                      ref="salesReportTable"
                      no-border-collapse
                      no-local-sorting
                      striped
                      :items="groupedResults"
                      :fields="fields"
                      :per-page="perPage"
                      :current-page="currentPage"
                      responsive="lg"
                      bordered
                      show-empty>
                    <template #empty>
                      <h3 class="no-results-overlay">No results to display</h3>
                    </template>
                  </b-table>
                </b-col>
              </b-row>
            </b-col>
            <b-col>
              <SalesReportGraph :report-data="groupedResults" :currency="currency" :group-by="groupBy"></SalesReportGraph>
            </b-col>
          </b-row>
          <b-row>
            <b-col b-col cols="4" v-show="groupedResults.length">
              <b-pagination
                  v-model="currentPage"
                  :total-rows="groupedResults.length"
                  :per-page="perPage"
                  aria-controls="my-table"
              ></b-pagination>
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
import {formatDate, getMonthName} from "../../util";
import SalesReportGraph from "./SalesReportGraph";

export default {
  name: "sales-report-page",
  components: {SalesReportGraph, DateRangeInput},
  data: function () {
    return {
      business: {},
      currency: {},
      groupBy: "year",
      totalResults: null,
      groupedResults: [],
      currentPage: 1,
      perPage: 10,
      dateRange: [],
    }
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.getBusiness(businessId);
  },

  methods: {
    /**
     * this is a get api which can take Specific business to display on the page
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param id
     **/
    getBusiness: function (id) {
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

    /**
     * Queries the currencies API to get currency info for the business
     **/
    async getCurrency(business) {
      this.currency = await api.getUserCurrency(business.address.country);
    },

    /**
     * Uses Api.js to send a get request with the getSalesReport.
     * This is used to search the sales report.
     * @param dateRange
     **/
    getSalesReport: async function (dateRange) {
      if (this.dateRange !== dateRange) {
        this.dateRange = dateRange;
        // The group by options may have changed due to the changed date range (see the groupByOptions computed property)
        // so if the selected option has been invalidated, then select the last available options.
        const optionNames = Object.keys(this.groupByOptions);
        this.groupBy = optionNames[optionNames.length - 1];
      }

      if (this.dateRange != null && this.dateRange.length) {
        const businessId = this.$route.params.id;
        const startDate = formatDate(dateRange[0]);
        const endDate = formatDate(dateRange[1]);
        await api.getSalesReport(businessId, startDate, endDate, this.groupBy)
            .then((response) => {
              this.updateTotalResults(startDate, endDate, response.data);
              this.groupedResults = response.data;
            }).catch((error) => {
              this.$log.debug("Error message", error);
            });
      }
    },

    /**
     * update the Sales Summary from the response data.
     * @param startDate
     * @param endDate
     * @param data response data
     **/
    updateTotalResults: function (startDate, endDate, data) {
      this.totalResults = {
        startDate,
        endDate,
        totalValue: data.reduce((count, item) => {
          return count + item.totalValue
        }, 0),
        totalPurchases: data.reduce((count, item) => {
          return count + item.totalPurchases
        }, 0)
      }
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

    /**
     * the table has different column in day, month, year and week.
     * @returns Array
     */
    fields: function () {

      let fields = [
        {key: 'totalPurchases', sortable: true},
        {key: 'totalValue', sortable: true}
      ]

      switch (this.groupBy) {
        case "day" :
          fields.unshift({key: 'startDate', sortable: true, label: 'Date'});
          break;
        case "week" :
          fields.unshift({key: 'startDate', sortable: true}, {key: 'endDate', sortable: true});
          break;
        case "month" :
          fields.unshift({
            key: 'startDate', sortable: true, label: 'Month', formatter: (value) => {
              return `${getMonthName(new Date(value).getMonth())} ${new Date(value).getFullYear()}`;
            }
          });
          break;
        case "year" :
          fields.unshift({
            key: 'endDate', sortable: true, label: 'Year', formatter: (value) => {
              return new Date(value).getFullYear();
            }
          });
          break;

      }

      return fields
    },
    /**
     * Computed value for the options that the user has to group the sales report by.
     * Doesn't allow users to group by a period that is larger than their selected period.
     * For example, if the selected range is one month, then the available options are Daily and Weekly.
     * If the selected range is one day, then the only option is Daily.
     */
    groupByOptions: function() {
      const options = {
        day: 'Daily',
      };
      const dateDiffDays = Math.ceil((this.dateRange[1] - this.dateRange[0]) / (1000 * 60 * 60 * 24));
      if (dateDiffDays > 7) {
        options.week = 'Weekly';
      }
      if (dateDiffDays > 31) {
        options.month = 'Monthly';
      }
      if (dateDiffDays > 365) {
        options.year = 'Yearly';
      }

      return options;
    },
  },
}
</script>