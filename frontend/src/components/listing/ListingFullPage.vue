<template>
  <div class="listings-page">
    <b-container>
      <b-row class="mb-5">
        <b-link v-if="$route.query.fromBusiness" variant="info" class="back-to-search-link"
                                  @click="$router.push({path: `/businesses/${$route.query.fromBusiness}/listings`})">
          <h4>
            <strong>
              <b-icon-arrow-left/>
              Back to business listings
            </strong>
          </h4>
        </b-link>
        <b-link v-else-if="$route.query.fromSearch" variant="info" class="back-to-search-link" @click="listingPageRedirect">
          <h4>
            <strong>
              <b-icon-arrow-left/>
              Back to results
            </strong>
          </h4>
        </b-link>
      </b-row>

      <b-row>
        <b-card v-if="!listingLoading" class="listing_card shadow">
          <b-row>
            <b-col md="6">
              <b-carousel
                  id="carousel-1"
                  :controls="listingItem.inventoryItem.product.images.length > 1"
                  indicators
                  :no-animation="true"
                  aria-busy="false"
                  :interval="0"
                  ref="image_carousel"
                  v-model="slideNumber"
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
                <b-carousel-slide v-if="listingItem.inventoryItem.product.images.length === 0">
                  <template #img>
                    <img class="product-image d-block w-100 rounded" src="../../../public/product_default.png"
                         alt="Product has no image">
                  </template>
                </b-carousel-slide>
              </b-carousel>

            </b-col>
            <b-col md="6">
              <h1 class="listing-title"> {{ listingItem.inventoryItem.product.name }} </h1>
              <h6 style="text-align: center; font-size: 12px"> Listed on: {{ listingItem.created }}</h6>
              <b-card no-body id="infobox-1">
                <template #header>
                  <h5 style="margin-top: -4px"> Quantity: {{ listingItem.quantity }} </h5>
                  <h5> Closes: {{ listingItem.closes }} </h5>
                  <h6 style="word-wrap: normal; font-size: 14px; height: 5rem; margin-bottom: 10px">
                    {{ listingItem.moreInfo }} </h6>
                  <b-row no-gutters>
                    <b-col md="6">
                      <h2 style="float: left; margin-bottom: -5px">
                        {{ currency.symbol }} {{ listingItem.price }} {{ currency.code }}
                      </h2>
                    </b-col>
                    <b-col md="6">
                      <b-button v-if="!$currentUser.currentlyActingAs"
                                style="float: right; margin-left: 1rem; margin-top: 3px" variant="success"
                                @click="openConfirmPurchaseDialog">
                        Purchase
                        <b-icon-bag-check/>
                      </b-button>
                      <span v-else>You must be acting as a user to like or purchase listings</span>
                    </b-col>
                  </b-row>
                </template>
              </b-card>
              <div v-if="!$currentUser.currentlyActingAs">
                <b-icon-heart title="Like this listing" class="like-icon" v-if="!listingItem.currentUserLikes"
                              @click="likeListing"></b-icon-heart>
                <b-icon-heart-fill title="Unlike this listing" class="like-icon" variant="danger" v-else
                                   @click="dislikeListing"></b-icon-heart-fill>
              </div>
              <h6 style="font-size: 13px; margin-top: 13px; float: right;"> {{ listingItem.usersLiked }}
                {{ getLikeString }}</h6>
            </b-col>
          </b-row>
          <b-row align-h="center" style="margin-top: 1rem">
            <b-col md="8">
              <b-input-group-text class="bottom-info-boxes">
                <b-container>
                  <h6 style="margin-top: 7px"><strong> Product Details: </strong></h6>
                  <hr>
                  <label class="details-text"> <strong> Manufacturer: </strong>
                    {{ listingItem.inventoryItem.product.manufacturer }} </label>
                  <br>
                  <label class="details-text"> <strong> Manufactured Date: </strong> {{
                      listingItem.inventoryItem.manufactured
                    }} </label>
                  <br>
                  <label class="details-text"> <strong> Best Before: </strong> {{
                      listingItem.inventoryItem.bestBefore
                    }}
                  </label>
                  <br>
                  <label class="details-text"> <strong> Sell By: </strong> {{ listingItem.inventoryItem.sellBy }}
                  </label>
                  <br>
                  <label class="details-text"> <strong> Expires: </strong> {{ listingItem.inventoryItem.expires }}
                  </label>
                  <br>
                  <label class="details-text" style="text-align: left; height: 6rem;"> <strong> Description: </strong>
                    {{ listingItem.inventoryItem.product.description }} </label>
                </b-container>
              </b-input-group-text>
            </b-col>
          </b-row>
          <b-row align-h="center" style="margin-top: 1rem">
            <b-col md="8">
              <b-input-group-text class="bottom-info-boxes">
                <b-container>
                  <h6 style="margin-top: 7px"><strong> Seller Information: </strong></h6>
                  <hr>
                  <label class="details-text"> <strong> Business Name: </strong>
                    <router-link :to="'/businesses/'+listingItem.inventoryItem.businessId">
                      {{ listingItem.business.name }}
                    </router-link>
                  </label>
                  <br>
                  <label class="details-text"> <strong> Business Location: </strong> {{ getAddress }} </label>
                </b-container>
              </b-input-group-text>
            </b-col>
          </b-row>

          <b-modal ref="confirmPurchaseModal" size="sm" title="Confirm Purchase" ok-variant="success"
                   ok-title="Purchase"
                   @ok="purchaseListingRequest">
            <h6>
              Are you sure you want to <strong>purchase</strong> this listing?
            </h6>
          </b-modal>

          <b-modal ref="purchaseErrorModal" size="sm" title="Purchase Error" ok-only no-close-on-backdrop
                   no-close-on-esc
                   ok-title="Ok" @ok="listingPageRedirect">
            <h6>
              {{ errMessage }}
            </h6>
          </b-modal>

          <b-modal id="completedPurchaseModal" title="Purchase Successful"
                   ok-only ok-variant="success" ok-title="Go to Home Page" @ok="$router.push('/homepage')"
                   no-close-on-backdrop no-close-on-esc hide-header-close>
            <h6>
              You have successfully purchased listing: {{ listingItem.inventoryItem.product.name }}.
            </h6>
            Further instructions for your purchase will be in a notification on your home page.
          </b-modal>
        </b-card>


        <b-card v-if="listingNotExists">
          <b-card-title>
            <b-icon-exclamation-triangle/>
            This Listing no longer exists
          </b-card-title>
          There is no listing at this page. It may have already been purchased by another user, or deleted by the
          business
          owner.
        </b-card>

      </b-row>
    </b-container>
  </div>
</template>

<style>

.listing-title {
  text-align: center;
  font-size: 24px;
  word-wrap: break-word;
  white-space: normal;
  height: 3rem
}

.listing_card {
  max-width: 60rem;
  margin-left: auto;
  margin-right: auto;
}

.listings-page .modal-header .close {
  display: none;
}

@media only screen and (min-width: 1250px) {
  .back-to-search-link {
    position: absolute;
    left: 50px;
  }
}

.carousel-control-prev-icon, .carousel-control-next-icon {
  background-color: black !important;
  border-radius: 20%;
  padding: 4px;
  color: white;
}

#carousel-1 {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
  margin-top: 7px;
}

.product-image {
  height: 20rem;
  object-fit: cover;
}


#infobox-1 {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

.like-icon {
  float: right;
  margin-top: 8px;
  margin-left: 5px;
  width: 25px;
  height: 25px;
  cursor: pointer;
}

.details-text {
  clear: both;
  float: left;
  word-wrap: normal;
  white-space: normal;
}

</style>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus"
import {formatAddress} from "../../util";


export default {
  name: "listing-full",
  data() {
    return {
      slideNumber: 0,
      listingItem: {},
      currency: {},
      listingLoading: true,
      listingNotExists: false,
      error: "",
      errorFlag: false,
      errMessage: null,
      queryHistory: {}
    }
  },
  async mounted() {
    this.queryHistory = this.$route.query.queryHistory
    await this.setListingData();
  },

  methods: {

    /**
     * Gets the listing data for the current listing by making an api request.
     * Also formats the business address into a single string of address.
     */
    async setListingData() {
      this.listingNotExists = false;
      this.listingLoading = true;

      const currentListingId = this.$route.params.id;
      await Api.getListing(currentListingId)
          .then(async listingData => {
            this.listingItem = listingData.data;

            let product = this.listingItem.inventoryItem.product;

            // Display primary image as first image
            if (product.images.length > 0) {
              product.images = product.images.filter((a) => a.id !== product.primaryImage.id);
              product.images.unshift(product.primaryImage);
            }

            const address = this.listingItem.business.address;
            this.currency = await Api.getUserCurrency(address.country);
            this.listingLoading = false;
          })
          .catch(() => {
            this.listingNotExists = true;
          });
    },


    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    },

    /**
     * Method that gets called when like icon is pressed
     * Sets the currentUserLikes to true and increments value of usersLiked
     */
    async likeListing() {
      await this.callLikeRequest();
      if (!this.errorFlag) {
        this.listingItem.currentUserLikes = true;
        this.listingItem.usersLiked++;
      }
    },

    /**
     * Method that gets called when unlike icon is pressed
     * Sets the currentUserLikes to false and decrements value of usersLiked
     */
    async dislikeListing() {
      await this.callLikeRequest();
      if (!this.errorFlag) {
        this.listingItem.currentUserLikes = false;
        this.listingItem.usersLiked--;
      }
    },

    /**
     * Uses Api.js to send a likeListing put request.
     * It sends the current Id of the listing to the Api request
     */
    async callLikeRequest() {
      try {
        await Api.likeListing(this.$route.params.id)
        EventBus.$emit('notificationUpdate')
        this.errorFlag = false
      } catch (error) {
        this.error = error
        this.errorFlag = true
      }
    },

    /**
     * Opens the dialog to confirm the purchase
     */
    openConfirmPurchaseDialog: function () {
      this.$refs.confirmPurchaseModal.show();
    },

    /**
     * Sends an api request to notify the backend that a user has tried to purchase a listing.
     */
    async purchaseListingRequest() {
      await Api
          .purchaseListing(this.listingItem.id)
          .then(() => {
            this.$bvModal.show("completedPurchaseModal");
            EventBus.$emit('notificationUpdate');
            localStorage.setItem('listingPurchased', "true");
          })
          .catch((err) => {
            if (err.response.status === 406) {
              this.errMessage = "Someone else has already purchase this listing sorry."
            } else {
              this.errMessage = err;
            }
            this.openErrorModal()
          })
    },

    /**
     * Handles errors and displays them in a modal for purchase, clicking okay on this modal redirects to listings search
     */
    listingPageRedirect: function () {
      this.$router.push({path: `/listingSearch`});
    },

    /**
     * Opens the error modal
     */
    openErrorModal() {
      this.$refs.purchaseErrorModal.show();
    }

  },

  computed: {
    /**
     * Formats the address using util function and appropriate privacy level.
     *
     * @return address formatted
     */
    getAddress: function () {
      return formatAddress(this.listingItem.business.address, 2);
    },
    /**
     * Returns the listing like string based on number of likes
     * @returns {string} The string to be returned
     */
    getLikeString() {
      if (this.listingItem.usersLiked === 1) {
        return "user likes this listing"
      } else {
        return "users like this listing"
      }
    }
  },

  watch: {
    $route() {
      this.setListingData();
    }
  }
}
</script>
