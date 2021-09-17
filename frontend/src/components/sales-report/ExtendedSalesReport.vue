<template>
  <b-row>
    <b-col>
      <b-table
          ref="salesReportTable"
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
  </b-row>
</template>

<script>
export default {
name: "ExtendedSalesReport"
}
</script>

<style scoped>

</style>