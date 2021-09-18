<template>
  <b-tabs fill>
    <b-tab title="Products" >
      <b-row>
        <b-col>
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
        <b-col>
          <h3>Sales Graph</h3>
        </b-col>
        <b-col>
          <b-select>

          </b-select>
        </b-col>
      </b-row>
    </b-tab>
    <b-tab title="Manufacturers">
    </b-tab>
    <b-tab title="Listings">
    </b-tab>
  </b-tabs>

</template>

<script>

import Api from "../../Api";
import {formatDate} from "../../util";

export default {
  name: "extended-sales-report",
  props: ["dateRange", "currency"],
  data: function () {
    return {
      businessId: '',
      totalResults: null,
      results: [],
      loading: false,
      fields: [
        {label: 'Product Code', key: 'product.id', sortable: true, formatter: value => value.split(/-(.+)/)[1] },
        {label: 'Quantity', key: 'totalProductPurchases', sortable: true},
        {key: 'totalValue', sortable: true},
        {key: 'totalLikes', sortable: true}
      ]
    }
  },

  async mounted() {
    this.businessId = this.$route.params.id;
    await this.getProductsReport(this.dateRange);


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



  }

}


</script>

<style scoped>

</style>