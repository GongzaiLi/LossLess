<!--
Listings Page
-->
<template>
  <b-container>
    <b-row>
      <b-col  v-for="(listing) in cards" :key="listing" lg="4">
    <b-card
      img-src="https://pic.onlinewebfonts.com/svg/img_148071.png"
      img-alt="Image"
      img-top
      style="min-width: 15rem;"
      >
      <b-card-title>{{listing.product.name}}</b-card-title>
      <b-card-sub-title>{{listing.product.manufacturer}}</b-card-sub-title>
      <b-card-text>

        <span>{{ listing.product.description }}</span><br>
        <b-list-group flush>
          <b-list-group-item>Cras justo odio</b-list-group-item>
          <b-list-group-item>Dapibus ac facilisis in</b-list-group-item>
          <b-list-group-item>Vestibulum at eros</b-list-group-item>
        </b-list-group>
      </b-card-text>

      <b-button href="#" variant="primary" disabled>Purchase</b-button>

    </b-card>
      </b-col>
    </b-row>



  </b-container>
</template>

<style scoped>
.profile-card {
  max-width: 50rem;
  margin-left: auto;
  margin-right: auto;
}
.card-columns {
  display: block;
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
          "id": 100,
          "product": {
            "id": "1WATT-420-BEANS1",
            "name": "1Watties Baked Beans - 420g can",
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
            "id": "2WATT-420-BEANS2",
            "name": "2Watties Baked Beans - 420g can",
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
          "id": 102,
          "product": {
            "id": "WATT-420-BEANS3",
            "name": "3Watties Baked Beans - 420g can",
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
          "id": 103,
          "product": {
            "id": "WATT-420-BEANS4",
            "name": "4Watties Baked Beans - 420g can",
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
          "id": 104,
          "product": {
            "id": "WATT-420-BEANS5",
            "name": "5Watties Baked Beans - 420g can",
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
          "id": 105,
          "product": {
            "id": "WATT-420-BEANS",
            "name": "6Watties Baked Beans - 420g can",
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
          "id": 106,
          "product": {
            "id": "WATT-420-BEANS",
            "name": "7Watties Baked Beans - 420g can",
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
        }
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

