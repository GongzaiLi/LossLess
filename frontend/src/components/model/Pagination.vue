<template>
  <b-row style="padding-top: 15px">
    <b-col>
      <b-pagination
          v-model="currentPage"
          :total-rows="totalItems"
          :per-page="perPage"
          @page-click="pageChange"
          first-text="First"
          prev-text="Prev"
          next-text="Next"
          last-text="Last"
      />
    </b-col>
    <b-col style=" margin-top: -20px" md="2">
      <div>
        Jump to page:
      </div>
      <b-form-select v-model="currentPage" :options="jumpToPagesOptions" size="sm-1" @change="pageChange"></b-form-select>
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
     * Handler for when pagination buttons clicked. Emits current page for parent components to use
     */
    pageChange: async function () {
      await this.$forceUpdate();
      this.$emit('input', this.currentPage);
    }
  },
  computed: {

    /**
     * Computes an array of page numbers for the jump to selector
     * @returns array
     */
    jumpToPagesOptions: function () {
      return Array.from({length: Math.ceil(this.totalItems/this.perPage)}, (_, i) => i + 1)
    },

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
