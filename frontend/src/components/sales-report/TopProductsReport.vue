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
          :sort-by.sync="sortBy"
          :sort-desc.sync="sortDesc"
          :per-page="perPage"
          :current-page="currentPage"
          striped
          responsive="lg"
          bordered
          show-empty>
        <template #empty>
          <h3 class="no-results-overlay">No results to display</h3>
        </template>
      </b-table>
      <b-pagination
          v-model="currentPage"
          :total-rows="results.length"
          :per-page="perPage"
          aria-controls="my-table"/>

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
      topTenResults: [],
      currentPage: 1,
      perPage: 10,
      sortBy: '',
      sortDesc: true,
      loading: false,
      doughnutOptions: {
        totalProductPurchases: "Total Quantity Sold",
        totalValue: "Total Value Sold",
        totalLikes: "Number of Likes"
      },
      fields: [
        {label: 'Product Code', key: 'product.id', sortable: false, formatter: value => value.split(/-(.+)/)[1]},
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
    this.filterResults();
    const cfg = {
      type: 'doughnut',
      data: {
        datasets: [{
          label: 'Top Products By Quantity',
          data: this.topTenResults.map(record => record.totalProductPurchases),
          backgroundColor: [
            '#332288',
            '#88ccee',
            '#44aa99',
            '#117733',
            '#999933',
            '#ddcc77',
            '#661100',
            '#cc6677',
            '#882255',
            '#aa4499',
            '#989898'
          ],
        },],
        labels: this.topTenResults.map(record => record.product.name)
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

      let sortByParam;
      switch (this.sortBy) {
        case "totalProductPurchases":
          sortByParam = "quantity";
          break;

        case "totalLikes":
          sortByParam = "likes";
          break;

        case "totalValue":
          sortByParam = "value";
          break;

        default:
          sortByParam = "quantity";
      }

      await Api.getProductsReport(this.businessId, startDate, endDate, sortByParam, this.sortDesc ? "DESC" : "ASC")
          .then((response) => {
            this.results = response.data;
          }).catch((error) => {
            this.$log.debug("Error message", error);
          });
    },

    /**
     * Updates the chart data when different options are selected
     */
    updateChart: function () {
      this.chart.data.datasets[0].data = this.topTenResults.map((record) => record[this.doughnutOption]);
      this.chart.data.labels = this.topTenResults.map(record => record.product.name);

      this.chart.update();
    },

    /**
     * filter Products Report results.
     * if Products Report results are more then ten, filter the top ten product then combine to the Other
     **/
    filterResults: function () {
      const topTenResults = this.results.slice(0, 10);
      const otherResults = this.results.slice(10);
      const other = {
        product: {
          name: `Other`
        },
        totalLikes: otherResults.reduce((totalLike, item) => {
          return totalLike + item.totalLikes
        }, 0),
        totalProductPurchases: otherResults.reduce((totalLike, item) => {
          return totalLike + item.totalProductPurchases
        }, 0),
        totalValue: otherResults.reduce((totalLike, item) => {
          return totalLike + item.totalValue
        }, 0)
      }
      if (otherResults.length > 0) {
        topTenResults.push(other)
      }
      this.topTenResults = topTenResults;
    }
  },

  watch: {
    /**
     * Watch for sortBy change, refresh table when it happens.
     */
    '$data.sortBy': {
      handler: function () {
        this.getProductsReport(this.dateRange);
      },
      deep: true
    },
    /**
     * Watch for sortDesc change, refresh table when it happens.
     */
    '$data.sortDesc': {
      handler: function () {
        this.getProductsReport(this.dateRange);
      },
      deep: true
    }
  },

}


</script>

<style scoped>

</style>