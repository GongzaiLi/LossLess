<template>
  <b-row>
    <b-col cols="6">
      <h3>Top Manufacturers</h3>
      <b-table
          ref="manufacturersReportTable"
          :items="results"
          :fields="fields"
          no-border-collapse
          no-local-sorting
          striped
          responsive="lg"
          bordered
          show-empty>
        <template #empty>
          <h3 class="no-results-overlay">No results to display</h3>
        </template>
      </b-table>
    </b-col>
    <b-col cols="6">
      <b-row>
        <b-col cols="auto">
          <h3>Top Manufacturers Graph</h3>
        </b-col>
        <b-col cols="5">
          <b-select v-model="doughnutOption" @input="updateChart" :options="doughnutOptions"/>
        </b-col>
      </b-row>
      <canvas id="top-manufacturers-graph"></canvas>
    </b-col>
  </b-row>
</template>

<script>

import Api from "../../Api";
import {formatDate} from "../../util";

export default {
  name: "top-manufacturers-report",
  props: ["dateRange", "currency"],
  data: function () {
    return {
      businessId: '',
      totalResults: null,
      results: [],
      loading: false,
      doughnutOptions: {
        totalProductPurchases: "Total Quantity Sold",
        totalValue: "Total Value Sold",
        totalLikes: "Number of Likes"
      },
      fields: [
        {label: 'Manufacturer', key: 'manufacturer', sortable: true},
        {label: 'Quantity', key: 'totalProductPurchases', sortable: true},
        {key: 'totalValue', sortable: true},
        {key: 'totalLikes', sortable: true}
      ],
      chart: null,
      doughnutOption: "totalProductPurchases",
    }
  },

  async mounted() {
    this.businessId = this.$route.params.id;
    await this.getManufacturersReport(this.dateRange);

  },

  methods: {

    /**
     * Uses Api.js to send a get request with the getProductsReport.
     * This is used to retrieve the data of the business's products to form a report.
     * @param dateRange
     **/
    getManufacturersReport: async function (dateRange) {
      const startDate = formatDate(dateRange[0]);
      const endDate = formatDate(dateRange[1]);
      await Api.getManufacturersReport(this.businessId, startDate, endDate)
          .then((response) => {
            this.results = response.data;
          }).catch((error) => {
            this.$log.debug("Error message", error);
          });
    },

  }
}


</script>

<style scoped>

</style>