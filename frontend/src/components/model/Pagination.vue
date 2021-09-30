<template>
  <b-row style="padding-top: 15px">
    <b-col>
      <b-pagination
          v-model="currentPage"
          :total-rows="totalItems"
          :per-page="perPage"
          @page-click="pageChange"
      />
    </b-col>
    <b-col lg="4">
      <form @submit.prevent="jumpToPage">
        <b-input-group prepend="Jump to page">
          <b-form-input v-model="pageToJumpTo" type="number" min="1" :max="Math.ceil(this.totalItems/this.perPage)"></b-form-input>
          <b-input-group-append>
            <b-button type="submit" variant="primary">Go</b-button>
          </b-input-group-append>
        </b-input-group>
      </form>
    </b-col>
    <b-col style="text-align: right;" class="mt-2">
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
      pageToJumpTo: 1,
    }
  },
  methods: {
    /**
     * Handler for when pagination buttons clicked. Emits current page for parent components to use
     */
    pageChange: async function () {
      await this.$forceUpdate();
      this.$emit('input', this.currentPage);
    },
    /**
     * Handler for when the jump to page button is pressed.
     * Sets the current page as the one to jump to and emits a page changed event.
     */
    jumpToPage: function () {
      this.currentPage = this.pageToJumpTo;
      this.pageChange();
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
