<template>
  <b-tabs fill>
    <b-tab title="Products" >
      <top-products-report :date-range="dateRange" :currency="currency" class="mt-4"></top-products-report>
    </b-tab>
    <b-tab title="Manufacturers">
      <top-manufacturers-report :date-range="dateRange" :currency="currency" class="mt-4"></top-manufacturers-report>
    </b-tab>
    <b-tab title="Listings">
    </b-tab>
  </b-tabs>

</template>

<script>

import TopProductsReport from "./TopProductsReport";
import TopManufacturersReport from "./TopManufacturersReport";

export default {
  name: "extended-sales-report",
  components: {TopManufacturersReport, TopProductsReport},
  props: ["dateRange", "currency"],
}


</script>
