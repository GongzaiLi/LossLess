<template>
  <b-row>
    <b-col>
      <b-pagination v-model="currentPage" :total-rows="totalItems" :per-page="perPage" @page-click="pageChange"/>
    </b-col>
    <b-col style="text-align: right; margin-top: 6px">
      Displaying {{ itemsRangeMin }} - {{ itemsRangeMax }} of total {{ totalItems }} results.
    </b-col>
  </b-row>
</template>

<script>
export default {
  name: "pagination",
  props: ['totalItems', 'perPage'],
  data: function () {
    return {
      currentPage: 1,
    }
  },
  methods: {
    /**
     *
     */
    pageChange: async function () {
      await this.$forceUpdate();
      this.$emit('input', this.currentPage);
    }
  },
  computed: {
    /**
     * Computes the min range of product displaying on the table at the current page.
     * @returns number
     */
    itemsRangeMin: function () {
      return this.currentPage === 1 ? 1 : (this.currentPage - 1) * this.perPage + 1;
    },

    /**
     * Computes the max range of product displaying on the table at the current page.
     * @returns number
     */
    itemsRangeMax: function () {
      return this.currentPage * this.perPage <= this.totalItems ? this.currentPage * this.perPage : this.totalItems;
    }
  }
}
</script>

<style scoped>

</style>