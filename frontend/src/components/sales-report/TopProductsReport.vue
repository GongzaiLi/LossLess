<template>
  <b-row>
    <b-col cols="6">
      <h3>Top Products</h3>
      <b-table
          ref="productsReportTable"
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
          <h3>Top Products Graph</h3>
        </b-col>
        <b-col cols="5">
          <b-select v-model="doughnutOption" @input="updateChart" :options="doughnutOptions"/>
        </b-col>
      </b-row>
      <canvas id="top-products-graph"></canvas>
    </b-col>
  </b-row>
</template>

<script>

import Api from "../../Api";
import {formatDate} from "../../util";
import Chart from "chart.js/auto";

export default {
  name: "top-products-report",
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
        {label: 'Product Code', key: 'product.id', sortable: true, formatter: value => value.split(/-(.+)/)[1] },
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
    await this.getProductsReport(this.dateRange);
    const cfg = {
      type: 'doughnut',
      data: {
        datasets: [{
          label: 'Top Products By Quantity',
          data: this.results.map(record => record.totalProductPurchases),
          backgroundColor: ['#1105c1', '#0e2fcb', '#0c4ad2', '#0a62d8', '#0979dd', '#078fe3', '#06a5e8', '#04bbee', '#03d1f3', '#01e8f9', '#00feff']        ,
        },],
        labels: this.results.map(record => record.product.id.split(/-(.+)/)[1])
      },
      options: {
        elements: {
          arc: {
            spacing: 3
          }
        },
        plugins: {
          tooltip: {
            callbacks: {
              label: (t) => {
                const labels = [
                  ` ${t.label}`,
                  ` Total Quantity Sold: ${this.results[t.dataIndex].totalProductPurchases}`,
                  ` Total Value Sold: ${this.currency.symbol}${this.results[t.dataIndex].totalValue}`,
                  ` Total Likes: ${this.results[t.dataIndex].totalLikes}`
                ];

                if (this.doughnutOption === 'totalValue') {
                  [labels[1], labels[2]] = [labels[2], labels[1]];
                } else if (this.doughnutOption === 'totalLikes') {
                  [labels[1], labels[3]] = [labels[3], labels[1]];
                }

                return labels;
              },
            }
          },
        },
      }
    };
    this.chart = new Chart(
        document.getElementById('top-products-graph').getContext('2d'),
        cfg);
  },

  methods: {

    /**
     * Uses Api.js to send a get request with the getProductsReport.
     * This is used to retrieve the data of the business's products to form a report.
     * @param dateRange
     **/
    getProductsReport: async function (dateRange) {
      const startDate = formatDate(dateRange[0]);
      const endDate = formatDate(dateRange[1]);
      await Api.getProductsReport(this.businessId, startDate, endDate)
          .then((response) => {
            this.results = response.data;
          }).catch((error) => {
            this.$log.debug("Error message", error);
          });
    },

    /**
     * Updates the chart data when different options are selected
     */
    updateChart: function() {
      this.chart.data.datasets[0].data = this.results.map((record) => record[this.doughnutOption]);
      this.chart.data.labels = this.results.map(record => record.product.id.split(/-(.+)/)[1]);

      this.chart.update();
    }
  }
}


</script>

<style scoped>

</style>