<template>
  <div>
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
      <b-overlay :show="loading" rounded="sm">
        <b-row>
          <b-col class="mt-2">
            <b-row>
              <b-col cols="4"><h3>Sales Details</h3></b-col>
              <b-col cols="4">
                <b-form-select v-model="groupBy" id="periodSelector"
                               @change="getSalesReport(dateRange, true)">
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
                    striped hovers
                    ref="salesReportTable"
                    no-border-collapse
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
            <h3>Sales Graph</h3>
            <SalesReportGraph :report-data="groupedResults" :currency="currency" :group-by="groupBy" v-on:finishedLoading="finishedLoadingGraph"/>
          </b-col>
        </b-row>
        <b-row>
          <b-col b-col lg="4" md="5" sm="12" v-show="groupedResults.length">
            <b-pagination
                v-model="currentPage"
                :total-rows="groupedResults.length"
                :per-page="perPage"
                aria-controls="my-table"
            ></b-pagination>
          </b-col>
        </b-row>
      </b-overlay>
    </b-list-group-item>
  </div>
</template>

<script>

import SalesReportGraph from "./SalesReportGraph";
import Api from "../../Api";
import {formatDate, getMonthName} from "../../util";

export default {
  name: "sales-report-page",
  components: {SalesReportGraph,},
  props: ['currency', 'dateRange'],
  data: function () {
    return {
      business: {},
      groupBy: "year",
      totalResults: null,
      groupedResults: [],
      currentPage: 1,
      perPage: 10,
      loading: false,
    }
  },
  mounted() {
    this.getSalesReport(this.dateRange);
  },
  methods: {
    /**
     * Uses Api.js to send a get request with the getSalesReport.
     * This is used to search the sales report.
     * @param dateRange
     * @param groupByChange
     **/
    getSalesReport: async function (dateRange, groupByChange=false) {
      this.loading = true;
      if (!groupByChange) {
        // The group by options may have changed due to the changed date range (see the groupByOptions computed property)
        // so if the selected option has been invalidated, then select the last available options.
        const optionNames = Object.keys(this.groupByOptions);
        this.groupBy = optionNames[optionNames.length - 1];
      }

      if (this.dateRange != null && this.dateRange.length) {
        const businessId = this.$route.params.id;
        const startDate = formatDate(dateRange[0]);
        const endDate = formatDate(dateRange[1]);
        await Api.getSalesReport(businessId, startDate, endDate, this.groupBy)
          .then((response) => {
            this.updateTotalResults(startDate, endDate, response.data);
            this.groupedResults = response.data;
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
      const totalValue = data.reduce((count, item) => {
        return count + item.totalValue
      }, 0);
      this.totalResults = {
        startDate,
        endDate,
        totalValue: Math.round(totalValue * 100) / 100,
        totalPurchases: data.reduce((count, item) => {
          return count + item.totalPurchases
        }, 0)
      }
    },

    /**
     * This shows the graph now that it has finished loading.
     */
    finishedLoadingGraph: function () {
      this.loading = false;
    },
  },

  computed: {
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
  watch: {
    dateRange(newVal) {
      this.getSalesReport(newVal);
    }
  }
}

</script>
