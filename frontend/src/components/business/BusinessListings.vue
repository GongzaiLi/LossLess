<!--
Listings Page
-->
<template>
  <b-container>
    <b-row><h1>Listings:</h1></b-row>
    <b-row><b-card>Listing 1</b-card></b-row>
    <b-row><b-card>Listing 2</b-card></b-row>
    <b-row><b-card>Listing 3</b-card></b-row>



  </b-container>
</template>

<style scoped>
.profile-card {
  max-width: 50rem;
  margin-left: auto;
  margin-right: auto;
}

h6 {
  line-height: 1.4;
}
</style>

<script>



export default {
  components: {
  },
  data: function () {
    return {}
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.launchPage(businessId);
  },

  methods: {

    /**
     * set up the page
     **/
    launchPage(businessId) {
      this.loading = true;
      this.getBusinessInfo(businessId);
    },

  },

  watch: {
    /**
     * This watches for those routing changes, and will update the profile with the data of the business specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      const id = to.params.id;
      this.launchPage(id);
    },
  },

}
</script>

