<template>
  <b-tabs fill>
    <b-tab title="Products" >
      <top-report :is-top-products="true" :date-range="dateRange" :currency="currency" class="mt-4"></top-report>
    </b-tab>
    <b-tab title="Manufacturers">
      <top-report :is-top-products="false" :date-range="dateRange" :currency="currency" class="mt-4"></top-report>
    </b-tab>
    <b-tab title="Listings">
    </b-tab>
  </b-tabs>

</template>

<script>

import TopReport from "./TopReport";

export default {
  name: "extended-sales-report",
  components: {TopReport},
  props: ["dateRange", "currency"],
}


</script>
