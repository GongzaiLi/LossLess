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
        </div>
        <div style="float:left; margin-left: 10px">
          <h1 style="text-align: center"> {{ listingItem.inventoryItem.product.name }} </h1>
          <h6 style="text-align: center; margin-top: -6px"> Listed on: {{listingItem.created}}</h6>
          <b-card no-body id="infobox-1">
            <template #header>
              <h5 style="margin-top: -4px"> Quantity: {{listingItem.quantity}} </h5>
              <h5> Closes: {{listingItem.closes}} </h5>
              <h6 style="word-wrap: normal; font-size: 14px; height: 5rem; margin-bottom: 10px"> {{listingItem.moreInfo}} </h6>
              <h2 style="float: left; margin-bottom: -5px"> {{listingItem.price}} </h2>
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
            <label class="details-text" style="text-align: left; white-space: normal; word-wrap: normal; height: 6rem;"> <strong> Description: </strong> {{ listingItem.inventoryItem.product.description }} </label>
          </b-container>
        </b-input-group-text>
      </b-row>
      <b-row align-h="center" style="margin-top: 1rem">
        <b-input-group-text class="bottom-info-boxes">
          <b-container>
            <h6 style="margin-top: 7px"> <strong> Seller Information: </strong></h6>
            <hr>
            <label class="details-text"> <strong> Business Name: </strong> {{ listingItem.businessName }}  </label>
            <br>
            <label class="details-text"> <strong> Business Location: </strong> {{ getAddress }} </label>
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
    left: 200px;
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
      listingItem: {
        id: '',
        quantity: 4,
        created: '11/11/2000',
        closes: '11/11/2021',
        moreInfo: 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean ' +
            'commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis,',
        price: '$20.00',
        numLikes: 123,
        businessName: "Wonka Chocolate",
        businessAddress: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          suburb: "Riccarton",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        inventoryItem: {
          manufactured: '1/1/1000',
          bestBefore: '25/12/2021',
          sellBy: '1/12/2021',
          expires: '30/12/2021',
          product: {
            name: 'Chocolate Bar',
            manufacturer: 'The Chocolate Factory',
            description: "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by thei",
            images: [{
              fileName: 'media/images/61be6c0a-c721-412f-858f-8ec5aded4df1.jpeg'
            }],
          }
        },
      },
      imageError: ""
    }
  },
  methods: {

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    }
  },

  computed: {

    /**
     * Combine fields of address
     */
    getAddress: function () {
      const address = this.listingItem.businessAddress;
      return (address.suburb ? address.suburb + ", " : "") + `${address.city}, ${address.region}, ${address.country}`;
    },
  }
}
</script>
