<template>
  <b-tabs fill>
    <b-tab title="Products" >
      <top-report :is-top-products="true" :date-range="dateRange" :currency="currency" class="mt-4"></top-report>
    </b-tab>
    <b-tab title="Manufacturers">
      <top-report :is-top-products="false" :date-range="dateRange" :currency="currency" class="mt-4"></top-report>
    </b-tab>
    <b-tab title="Listings">
      <listings-durations-graph :date-range="dateRange"/>
    </b-tab>
  </b-tabs>

</template>

<script>

import TopReport from "./TopReport";
import ListingsDurationsGraph from "./ListingsDurationsGraph";

export default {
  name: "extended-sales-report",
  components: {TopReport, ListingsDurationsGraph},
  props: ["dateRange", "currency"],
}


</script>
