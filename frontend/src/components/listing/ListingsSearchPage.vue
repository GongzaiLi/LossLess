<template>

  <b-card style="max-width: 80em;">
    <b-container>
      <h1>Search for Listings</h1>
      <div align-h="start">
        <b-row>
          <b-col cols="5">
            <b-input placeholder="Search: Product Name"></b-input>
          </b-col>
          <b-col cols="4">
            <b-select v-model="sortProperty" value="inventoryItem.product.name">
              <option value="inventoryItem.product.name">Sort: Product Name</option>
              <option value="price">Price</option>
              <option value="closes">Listing Closes</option>
              <option value="created">Listing Opens</option>
              <option value="quantity">Quantity</option>
            </b-select>
          </b-col>
          <b-col>
            <b-button>Search</b-button>
          </b-col>
          <b-col>
            <b-button v-b-toggle.collapse-1 variant="primary">Filter</b-button>
          </b-col>

        </b-row>
        <b-collapse id="collapse-1" class="mt-2">
        <b-row>
          <b-col>
            <b-input placeholder="Business Name"></b-input>
          </b-col>
          <b-col>
            <b-select placeholder="Business Type"></b-select>
          </b-col>
          <b-col>
            <b-input placeholder="Business Location"></b-input>
          </b-col>

        </b-row>
        <b-row>
          <b-col>
            <b-input-group>
              <b-input type="date" placeholder="Date Min:" style="width: 5rem"></b-input>
              <label style="margin-left: 1rem;margin-right: 1rem"> to </label>
              <b-input type="date" placeholder="Date: Max"  style="width: 5rem"></b-input>
            </b-input-group>
          </b-col>
          <b-col>
            <b-input-group>
              <b-input type="number" placeholder="Price Min:" style="width: 5rem"></b-input>
              <label style="margin-left: 1rem;margin-right: 1rem"> to </label>
              <b-input type="number" placeholder="Price Max"  style="width: 5rem"></b-input>
            </b-input-group>
          </b-col>
        </b-row>


        </b-collapse>
      </div>


      <b-row cols-lg="3" cols-md="3" style="margin-left: -38px">
        <b-col v-for="(listing,index) in cards" :key="index" class="mb-4">
          <b-card style="min-width: 17rem; height: 100%">
            <b-card-title>{{ listing.quantity }} x {{ listing.inventoryItem.product.name }}</b-card-title>

            <hr>
            <b-card-sub-title>
              Product Expires: {{ listing.inventoryItem.expires }}
            </b-card-sub-title>
            <div v-if="listing.inventoryItem.product.images.length">
              <img class="product-image" :src="getPrimaryImage(listing)" alt="Failed to load image">
            </div>
            <div v-if="!listing.inventoryItem.product.images.length">
              <img class="product-image" :src="require(`/public/product_default.png`)" alt="Product has no image">
            </div>
            <hr>
              <h5><b>Seller: {{ business.name }}</b></h5>
              <span>Location: {{ business.address.city }}, {{ business.address.country }}</span><br>
              <span>Closes:{{ listing.closes }}</span>
            <b-card-footer>
            <h5>
              {{ currency.symbol }}{{ listing.price }} {{currency.code}}
            </h5>
            </b-card-footer>
          </b-card>
        </b-col>
      </b-row>

      <pagination :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="cards.length"/>
    </b-container>
  </b-card>
</template>

<script>
import pagination from "../model/Pagination";
import api from "../../Api";

export default {

name: "ListingsSearchPage",
  components: {
    pagination,
  },
  data: function () {
    return {
      business: {},
      listingCardError: "",
      listingDisplayedInCard: {},
      isListingCardReadOnly: true,
      cards: [],
      sortProperty: 'inventoryItem.product.name',
      sortDirection: 'ASC',
      perPage: 12,
      currentPage: 1,
      totalResults: 0,
      mainProps: {blank: true, width: 250, height: 200},
      images: [],
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'US Dollar'
      },
    }
  },
  mounted() {
    this.initListingPage();
  },
  methods: {
    /**
     * Page initilisation function
     **/
    initListingPage: async function () {
      this.businessId = this.$route.params.id;
      await this.getBusinessInfo(1);
      await this.getListings();
    },
    /**
     * Api request to get business information
     **/
    async getBusinessInfo(businessId) {
      await api.getBusiness(businessId)
          .then((response) => {
            this.business = response.data;
            return api.getUserCurrency(response.data.address.country);
          })
          .then(currencyData => this.currency = currencyData)
          .catch((error) => {
            this.$log.debug(error);
          });
    },
    /**
     * read all the listing in for the corresponding business
     **/
    getListings: async function () {
      await api.getListings(1, this.perPage, this.currentPage - 1, this.sortProperty, this.sortDirection)
          .then((response) => {
            this.$log.debug("Listing Data", response);
            this.totalResults = response.data.totalItems;
            this.cards = response.data.listings;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },
    /**
     * Takes a listing as input and returns the primary image for that listing
     * @return image      The image of the listing
     * @param listing     The listing to get the image of
     **/
    getPrimaryImage: function (listing) {
      const primaryImageFileName = listing.inventoryItem.product.primaryImage.fileName;
      return api.getImage(primaryImageFileName);
    },
  }

}
</script>

<style scoped>

</style>