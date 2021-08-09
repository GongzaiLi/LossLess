<template>
  <b-card>
    <b-button variant="primary" @click="goBack">Return to search results</b-button>
    <hr>
    <h1>
      Listing Title
    </h1>
  </b-card>

</template>

<script>
export default {
  name: "listing-full",
  methods: {

    /**
     * Redirects to the listings search page
     */
    goBack() {
      this.$router.push(`/listingSearch`);
    }
  }

}
</script>

<style scoped>


</style>