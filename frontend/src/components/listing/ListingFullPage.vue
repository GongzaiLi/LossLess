<template>
  <div>

    <b-link variant="info" class="back-to-search-link" to="/listingSearch">
      <strong><h4><b-icon-arrow-left/> Back to results </h4></strong>
    </b-link>
    <b-card class="listing_card shadow">
      <b-row>
        <div style="float:left; margin-right: 10px; margin-left: 20px">
          <b-carousel
              id="carousel-1"
              :controls="listingItem.inventoryItem.product.images.length > 1"
              indicators
              :interval="0"
              ref="image_carousel"
              v-model="slideNumber"
              v-if="listingItem.inventoryItem.product.images.length > 0"
          >
            <b-carousel-slide v-for="image in listingItem.inventoryItem.product.images" :key="image.id">
              <template #img>
                <!-- The class .d-block prevent browser default image alignment -->
                <img
                    class="product-image d-block w-100 rounded"
                    alt="Product image"
                    :src="getURL(image.fileName)"
                >
              </template>
            </b-carousel-slide>
          </b-carousel>
          <img v-else class="default-product-image" src="../../../public/product_default.png" alt="Product has no image">
        </div>
        <div style="float:left; margin-left: 10px">
            <h1 style="text-align: center;"> {{ listingItem.inventoryItem.product.name }} </h1>
          <h6 style="text-align: center; margin-top: -6px"> Listed on: {{listingItem.created}}</h6>
          <b-card no-body id="infobox-1">
            <template #header>
              <h5 style="margin-top: -4px"> Quantity: {{listingItem.quantity}} </h5>
              <h5> Closes: {{listingItem.closes}} </h5>
              <h6 style="word-wrap: normal; font-size: 14px; height: 5rem; margin-bottom: 10px"> {{listingItem.moreInfo}} </h6>
              <h2 style="float: left; margin-bottom: -5px">
                {{ currency.symbol }} {{ listingItem.price }} {{currency.code}}
              </h2>
              <b-button style="float: right; margin-left: 1rem; margin-top: 3px" variant="success"> Buy <b-icon-bag-check/></b-button>
            </template>
          </b-card>
          <b-icon-star class="like-icon" v-if="!userLikedListing"></b-icon-star>
          <b-icon-star-fill class="like-icon" variant="warning" v-else></b-icon-star-fill>
          <h6 style="font-size: 13px; margin-top: 13px; float: right;"> {{listingItem.numLikes}} users like this listing </h6>
        </div>
      </b-row>
      <b-row align-h="center" style="margin-top: 1rem">
        <b-input-group-text class="bottom-info-boxes">
          <b-container>
            <h6 style="margin-top: 7px"> <strong> Product Details: </strong></h6>
            <hr>
            <label class="details-text"> <strong> Manufacturer: </strong> {{ listingItem.inventoryItem.product.manufacturer }}  </label>
            <br>
            <label class="details-text"> <strong> Manufactured Date: </strong> {{ listingItem.inventoryItem.manufactured }} </label>
            <br>
            <label class="details-text"> <strong> Best Before: </strong> {{ listingItem.inventoryItem.bestBefore }}  </label>
            <br>
            <label class="details-text"> <strong> Sell By: </strong> {{ listingItem.inventoryItem.sellBy }} </label>
            <br>
            <label class="details-text"> <strong> Expires: </strong> {{ listingItem.inventoryItem.expires }}  </label>
            <br>
            <label class="details-text" style="text-align: left; height: 6rem;"> <strong> Description: </strong> {{ listingItem.inventoryItem.product.description }} </label>
          </b-container>
        </b-input-group-text>
      </b-row>
      <b-row align-h="center" style="margin-top: 1rem">
        <b-input-group-text class="bottom-info-boxes">
          <b-container>
            <h6 style="margin-top: 7px"> <strong> Seller Information: </strong></h6>
            <hr>
            <label class="details-text"> <strong> Business Name: </strong> {{ listingItem.business.name }}  </label>
            <br>
            <label class="details-text"> <strong> Business Location: </strong> {{ address }} </label>
          </b-container>
        </b-input-group-text>
      </b-row>
    </b-card>

  </div>
</template>

<style>

.listing_card {
  max-width: 60rem;
  margin-left: auto;
  margin-right: auto;

}

@media only screen and (min-width: 1250px) {
  .back-to-search-link {
    position: absolute;
    left: 50px;
  }
}

.carousel-control-prev-icon , .carousel-control-next-icon {
  background-color: black !important;
  border-radius: 20%;
  padding:4px;
  color: white;
}

#carousel-1{
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
  max-width: 30rem;
  width: 30rem;
  margin-top: 7px;
}

.product-image {
  height: 20rem;
  object-fit: cover;
  width: 100%;
}

.default-product-image {
  height: 20rem;
  object-fit: cover;
  width: 30rem;
}

#infobox-1 {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
  max-width: 25rem;
}

.like-icon {
  float: right;
  margin-top: 8px;
  margin-left: 5px;
  width: 25px;
  height: 25px;
}

.details-text {
  clear: both;
  float: left;
  word-wrap: normal;
  white-space: normal;
}

.bottom-info-boxes {
  max-width: 40rem;
  width: 40rem;
}

</style>

<script>
import Api from "../../Api";


export default {
  name: "listing-full",
  data() {
    return {
      userLikedListing: false,
      slideNumber: 0,
      listingItem: {},
      address: {},
      currency: {},
      imageError: ""
    }
  },
  async mounted() {
    console.log(111111, this.listingItem.inventoryItem)
    await this.setListingData()
  },

  methods: {

    /**
     * Gets the listing data either from the props when redirected from listings search page or
     * using api request when page is reloaded since props data is not available on reload.
     * Also formats the business address as single string.
     *
     */
    async setListingData() {
      let listingData = this.$route.params.listingData;
      this.listingItem = listingData

      if (this.listingItem === undefined) {
        const currentListingId = this.$route.params.id
        listingData = await Api.getListing(currentListingId)
        this.listingItem = listingData.data
      }

      const address = this.listingItem.business.address;
      this.address =  (address.suburb ? address.suburb + ", " : "") + `${address.city}, ${address.region}, ${address.country}`;
      this.currency = await Api.getUserCurrency(address.country);

    },

    /**
     * Makes an api request to get the individual listing
     *
     * @param id the id of the current listing
     * @return the listing object
     */

    async getCurrentListing(id) {

      let listing = {}
       await Api.getListing(id)
        .then(response => {
          listing = response.data
        })
        .catch(err => {
          console.log(err)
        })
        return listing
    },

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    }
  },
}
</script>
