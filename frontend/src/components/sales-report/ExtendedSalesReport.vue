<template>
  <b-tabs fill>
    <b-tab title="Products" >
      <top-products-report :date-range="dateRange" :currency="currency" class="mt-4"></top-products-report>
    </b-tab>
    <b-tab title="Manufacturers">
      <top-manufacturers-report :date-range="dateRange" :currency="currency" class="mt-4"></top-manufacturers-report>
    </b-tab>
    <b-tab title="Listings">
      <listings-durations-graph :date-range="dateRange"/>
    </b-tab>
  </b-tabs>

</template>

<script>

import TopProductsReport from "./TopProductsReport";
import ListingsDurationsGraph from "./ListingsDurationsGraph";
import TopManufacturersReport from "./TopManufacturersReport";

export default {
  name: "extended-sales-report",
  components: {ListingsDurationsGraph, TopProductsReport, TopManufacturersReport},
  props: ["dateRange", "currency"],
}


</script>
