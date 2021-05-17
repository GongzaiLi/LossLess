<!--
Listings Page
-->
<template>
  <b-container>
    <div class="card-columns">
    <b-card  v-for="(listing) in cards" :key="listing"
      img-src="https://pic.onlinewebfonts.com/svg/img_148071.png"
      img-alt="Image"
      img-top
      tag="article"
      style="max-width: 20rem;"
      class="mb-2"
      >
      <b-card-title>{{listing.product.name}}</b-card-title>
      <b-card-sub-title>{{listing.product.manufacturer}}</b-card-sub-title>
      <b-card-text>

        <span>Some</span><br>
        quick example text to build on the card title and make up the bulk of the card's content.
      </b-card-text>

      <b-button href="#" variant="primary">Go somewhere</b-button>

    </b-card>
    </div>



  </b-container>
</template>

<style scoped>
.profile-card {
  max-width: 50rem;
  margin-left: auto;
  margin-right: auto;
}
.card-columns{
  column-count: 3;
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
    return {
      cards:[
        {
          "id": 101,
          "product": {
            "id": "WATT-420-BEANS",
            "name": "Watties Baked Beans - 420g can",
            "description": "Baked Beans as they should be.",
            "manufacturer": "Heinz Wattie's Limited",
            "recommendedRetailPrice": 2.2,
            "created": "2021-05-17T02:11:58.602Z",
            "images": [
              {
                "id": 1234,
                "filename": "/media/images/23987192387509-123908794328.png",
                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
              }
            ]
          },
          "quantity": 4,
          "pricePerItem": 6.5,
          "totalPrice": 21.99,
          "manufactured": "2021-05-17",
          "sellBy": "2021-05-17",
          "bestBefore": "2021-05-17",
          "expires": "2021-05-17"
        },
        {
          "id": 101,
          "product": {
            "id": "WATT-420-BEANS",
            "name": "Watties Baked Beans - 420g can",
            "description": "Baked Beans as they should be.",
            "manufacturer": "Heinz Wattie's Limited",
            "recommendedRetailPrice": 2.2,
            "created": "2021-05-17T02:11:58.602Z",
            "images": [
              {
                "id": 1234,
                "filename": "/media/images/23987192387509-123908794328.png",
                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
              }
            ]
          },
          "quantity": 4,
          "pricePerItem": 6.5,
          "totalPrice": 21.99,
          "manufactured": "2021-05-17",
          "sellBy": "2021-05-17",
          "bestBefore": "2021-05-17",
          "expires": "2021-05-17"
        },
        {
          "id": 101,
          "product": {
            "id": "WATT-420-BEANS",
            "name": "Watties Baked Beans - 420g can",
            "description": "Baked Beans as they should be.",
            "manufacturer": "Heinz Wattie's Limited",
            "recommendedRetailPrice": 2.2,
            "created": "2021-05-17T02:11:58.602Z",
            "images": [
              {
                "id": 1234,
                "filename": "/media/images/23987192387509-123908794328.png",
                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
              }
            ]
          },
          "quantity": 4,
          "pricePerItem": 6.5,
          "totalPrice": 21.99,
          "manufactured": "2021-05-17",
          "sellBy": "2021-05-17",
          "bestBefore": "2021-05-17",
          "expires": "2021-05-17"
        },
      ]
    }
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

