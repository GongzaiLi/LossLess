<!--
Page for users to input business information for registration of a Business
Authors: James
Date: 21/5/21
-->


<template>
  <div>
    <b-card class="shadow">
      <h1><b-icon-shop/> Market Place </h1>
      <b-tabs v-model=activeTabIndex align="center" fill>
        <b-tab title="For Sale">
          For Sale Goes Here, you can embed card sub-components directly like this
          <b-card-header>
            This is card header
          </b-card-header>
          <b-card-body>
            This is card body
          </b-card-body>
        </b-tab>
        <b-tab title="Wanted">
          Wanted goes here
        </b-tab>
        <b-tab title="Exchange">
          Exchange goes here
        </b-tab>
      </b-tabs>
    </b-card>
  </div>
</template>

<script>

export default {

  data: function () {
    return {
      errors: [],
      activeTabIndex: 0,
    }
  },
  methods: {
    /**
     * Pushes errors to errors list to be displayed as response on the screen,
     * if there are any.
     */
    pushErrors(error) {
      this.errors.push(error.message);
    }
  },
  computed: {

  }
}
</script>
