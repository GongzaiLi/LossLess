<!--
Listings Page
-->
<template>

  <div>
    <h1 align="middle">Listings</h1>
    <b-row align-h="end">
      <h3>Sort by:</h3>
      <b-col md="auto">
    <b-select v-model="sortProperty" value="name">
    <option  value="name">Product Name</option>
    <option  value="price">Price</option>
    <option  value="closes">Listing Closes</option>
    <option  value="opened">Listing Opens</option>
  </b-select>
      </b-col>
      <b-col md="auto">
    <b-select v-model="sortDirection" value="asc">
      <option value="asc">Asc</option>
      <option value="desc">Desc</option>
    </b-select>
      </b-col>
    <b-button @click="sortListings">Sort</b-button>
    </b-row>

  <b-row cols-lg="3" cols-xl="4">
    <b-col  v-for="(listing,index) in cards" :key="index" class="mb-4">
      <b-card
          img-src="https://pic.onlinewebfonts.com/svg/img_148071.png"
          img-alt="Image"
          img-top
          style="min-width: 17rem;"
      >
        <b-card-title>{{listing.quantity}} x {{listing.inventoryItem.product.name}}</b-card-title>
        <b-card-sub-title v-if="listing.inventoryItem.product.manufacturer">{{listing.inventoryItem.product.manufacturer}}</b-card-sub-title>
        <b-card-text>

          <span>{{ listing.inventoryItem.product.description }}</span><br>
          <b-list-group flush>
            <b-list-group-item></b-list-group-item>
            <b-card-text>
              <span v-if="listing.inventoryItem.manufactured">Manufactured: {{listing.inventoryItem.manufactured}}</span><br>
              <span v-if="listing.inventoryItem.bestBefore">Best before: {{listing.inventoryItem.manufactured}}</span><br>
              <span v-if="listing.inventoryItem.sellBy">Sell by: {{listing.inventoryItem.manufactured}}</span><br>
              <span v-if="listing.inventoryItem.expires">Expires: {{listing.inventoryItem.manufactured}}</span><br>
            </b-card-text>
          </b-list-group>
          <b-list-group flush>
            <b-list-group-item></b-list-group-item>

            <h3><b-button href="#" variant="primary" disabled>Purchase</b-button>
              ${{listing.price}}</h3>
            <span v-if="listing.moreInfo">{{listing.moreInfo}}</span>
          </b-list-group>
        </b-card-text>
        <b-card-footer>
          Created:{{listing.created}}<br>
          Closes:{{listing.closes}}
        </b-card-footer>


      </b-card>
    </b-col>
  </b-row>


  </div>
</template>

<style scoped>


</style>

<script>



export default {
  components: {
  },
  data: function () {
    return {
      cards:[
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "2Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 11.99,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-14T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "1Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 17.98,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-15T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "3Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 52.00,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-12T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "5Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 179.99,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-11T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "6Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 1.99,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-09T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "7Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 17.99,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-25T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
        {
          "id": 57,
          "inventoryItem": {
            "id": 101,
            "product": {
              "id": "WATT-420-BEANS",
              "name": "Watties Baked Beans - 420g can",
              "description": "Baked Beans as they should be.",
              "manufacturer": "Heinz Wattie's Limited",
              "recommendedRetailPrice": 2.2,
              "created": "2021-05-17T10:46:27.701Z",
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
          "quantity": 3,
          "price": 17.99,
          "moreInfo": "Seller may be willing to consider near offers",
          "created": "2021-07-14T11:44:00Z",
          "closes": "2021-07-21T23:59:00Z"
        },
      ],
      sortProperty:'name',
      sortDirection:'asc'
    }
  },

  mounted() {
    this.sortListings();
  },

  methods: {
    compare( a, b ) {
      let less=1;
      let more=-1;
      if (this.sortDirection==='asc'){
        less=-1;
        more=1;
      }
      if (this.sortProperty==='name'){
        if ( a.inventoryItem.product.name < b.inventoryItem.product.name ){
          return less;
        }
        if ( a.inventoryItem.product.name > b.inventoryItem.product.name ){
          return more;
        }
      }
      else if (this.sortProperty==='created'){
        if ( a.created < b.created ){
          return less;
        }
        if ( a.created > b.created ){
          return more;
        }
      }
      else if (this.sortProperty==='closing'){
        if ( a.closes < b.closes ){
          return less;
        }
        if ( a.closes > b.closes ){
          return more;
        }
      }
      else if (this.sortProperty==='price'){
        if ( a.price < b.price ){
          return less;
        }
        if ( a.price > b.price ){
          return more;
        }
      }

      return 0;
    },
    sortListings(){
      this.cards.sort(this.compare)
    }

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

