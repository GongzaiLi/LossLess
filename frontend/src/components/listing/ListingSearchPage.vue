<template>

  <b-card class="b_card_main shadow-lg">
    <b-container>
      <h1>Search Listings</h1>
      <hr>

      <b-form @submit="getListings(true)">
        <b-row align-h="around">
          <b-col cols="12" md="5">
            <b-input-group prepend="Search:">
              <b-input ref="searchInput" placeholder="Product Name" v-model="search.productName"></b-input>
            </b-input-group>
          </b-col>

          <b-col cols="12" md="4">
            <div class="input-group mb-2 mr-sm-2">
              <div class="input-group-prepend">
                <div class="input-group-text">Sort:</div>
              </div>
              <b-select v-model="search.sort" value="inventoryItem.product.name">
                <option value="inventoryItem.product.name,asc">Product Name A to Z</option>
                <option value="inventoryItem.product.name,desc">Product Name Z to A</option>
                <option value="business.name,asc">Seller Name A to Z</option>
                <option value="business.name,desc">Seller Name Z to A</option>
                <option value="price,asc">Price Low To High</option>
                <option value="price,desc">Price High To Low</option>
                <option value="closes,asc">Listing Closes Earlier to Later</option>
                <option value="closes,desc">Listing Closes Later to Earlier</option>
                <option value="inventoryItem.expires,asc">Expiry Date Earlier to Later</option>
                <option value="inventoryItem.expires,desc">Expiry Date Later to Earlier</option>
                <option value="location,asc">Location A to Z</option>
                <option value="location,desc">Location Z to A</option>
              </b-select>
            </div>
          </b-col>

          <b-col class="search_button" cols="3" md="1">
            <b-button type="submit">Search</b-button>
          </b-col>

          <b-col class="search_button" cols="3" md="1">
            <b-button v-b-toggle.collapse-1 variant="primary">Filter</b-button>
          </b-col>

        </b-row>
        <hr>

        <b-collapse id="collapse-1" class="mt-2">
          <b-row>
            <b-col cols="12" md="4">
              <label>Business Name:</label>
              <b-input v-model="search.businessName"></b-input>
            </b-col>
            <b-col cols="12" md="4">
              <label>Business Types:</label>
              <b-select v-model="search.selectedBusinessType" @change="addBusinessType">
                <option :value="null"> Any Business Type</option>
                <option> Accommodation and Food Services</option>
                <option> Retail Trade</option>
                <option> Charitable organisation</option>
                <option> Non-profit organisation</option>
              </b-select>
              <b-form-tags
                  class="mt-2"
                  v-if="search.businessTypes.length"
                  input-id="tags-remove-on-delete"
                  v-model="search.businessTypes"
                  remove-on-delete
              >
                <template v-slot="{ tags }">
                  <b-badge v-for="tag in tags" :key="tag" class="m-1 b-form-tag flex-grow-1">
                    <span class="b-form-tag-content flex-grow-1 text-truncate">{{ tag }}</span>
                    <button @click="removeTag(tag)" aria-keyshortcuts="Delete" type="button" aria-label="Remove tag"
                            class="close b-form-tag-remove">×
                    </button>
                  </b-badge>
                </template>
              </b-form-tags>
            </b-col>
            <b-col cols="12" md="4">
              <label>Business Location:</label>
              <b-input placeholder="Type a country, city or suburb" v-model="search.businessLocation"></b-input>
            </b-col>

          </b-row>
          <br>
          <b-row align-h="between">

            <b-col cols="12" md="7">
              <div class="input-group mb-2 mr-sm-2">
                <div class="input-group-prepend">
                  <div class="input-group-text">Listing Closes:</div>
                </div>
                <div>
                  <b-input type="date" v-model="search.closesStartDate"></b-input>
                </div>
                <label class="to_label"> to </label>
                <div>
                  <b-input type="date" v-model="search.closesEndDate"></b-input>
                </div>
              </div>
            </b-col>
            <b-col cols="12" md="4">

              <div class="input-group mb-2 mr-sm-2">
                <div class="input-group-prepend">
                  <div class="input-group-text">Price:</div>
                </div>
                <div>
                  <b-input class="price_min" step="0.01" min="0.00" max="1000000.00" type="number" placeholder="Min" v-model="search.priceMin"></b-input>
                </div>
                <label class="to_label"> to </label>
                <div>
                  <b-input class="price_max" step="0.01" min="0.01" max="1000000.00" type="number" placeholder="Max" v-model="search.priceMax"></b-input>
                </div>
              </div>

            </b-col>
          </b-row>
          <hr>
        </b-collapse>
      </b-form>

      <b-row class="listing_row" cols-lg="3" cols-md="3">
        <b-col v-for="(listing,index) in listings" :key="index" class="mb-4">
          <b-card class="b_card_listing shadow-sm" @click="goToListingPage(listing.id)">
            <b-card-title>{{ listing.quantity }} x {{ listing.inventoryItem.product.name }}</b-card-title>

            <hr>
            <b-card-sub-title>
              Product Expires: {{ listing.inventoryItem.expires }}
            </b-card-sub-title>
            <div v-if="listing.inventoryItem.product.images.length">
              <img class="product-image" :src="getPrimaryImage(listing)" alt="Failed to load image">
            </div>
            <div v-if="!listing.inventoryItem.product.images.length">
              <img class="product-image" src="product_default.png" alt="Product has no image">
            </div>
            <hr>
            <h5><strong>Seller: {{ listing.business.name }}</strong></h5>
            <span>Location: {{ listing.business.address.city }}, {{ listing.business.address.country }}</span><br>
            <span>Closes: {{ listing.closes }}</span>
            <template #footer>
              <h5 class="listing_price" v-if="listing.business.currency">
                {{ listing.business.currency.symbol }}{{ listing.price }} {{listing.business.currency.code}}
              </h5>
            </template>
          </b-card>
        </b-col>
      </b-row>
      <h2 v-if="listings.length === 0 && initialized">Unfortunately, no listings matched your search.</h2>

      <pagination v-if="totalResults > perPage" :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="listings.length"/>
    </b-container>
  </b-card>
</template>

<style>

.search_button {
  text-align: right;
}

.b_card_main {
  max-width: 80em;
}

.to_label {
  margin-left: 1rem;
  margin-right: 1rem;
  margin-top: auto;
}

.price_min {
  max-width: 6rem
}

.price_max {
  max-width: 7rem
}

.b_card_listing {
  min-width: 17rem;
  height: 100%;
}

.b_card_listing:hover {
  -webkit-box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.18) !important;
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.18) !important;
  cursor: pointer;
}

.listing_price {
  float: right;
}

</style>

<script>
import pagination from "../model/Pagination";
import Api from "../../Api";
import {getToday} from "../../util";

export default {

  name: "ListingsSearchPage",
  components: {
    pagination,
  },
  data: function () {
    return {
      search: {
        productName: "",
        sort: "inventoryItem.product.name,asc",
        businessName: "",
        selectedBusinessType: null,
        businessLocation: "",
        closesStartDate: "",
        closesEndDate: "",
        priceMin: "",
        priceMax: "",
        businessTypes: []
      },
      business: {},
      listings: [],
      perPage: 9,
      currentPage: 0, // page start with 0
      totalResults: 0,
      mainProps: {blank: true, width: 250, height: 200},
      images: [],
      initialized: false
    }
  },
  async mounted() {
    this.$refs.searchInput.focus();
    this.search.productName = this.$route.query.searchQuery || "";
    await this.getListings();
    this.search.closesStartDate = getToday();
    this.initialized = true;
  },

  methods: {
    /**
     * Sends API request to get all the listings with the search parameters stored in this component.
     * Also queries the currencies API to get currency info for each business (the API call is cached
     * so this should be fast).
     **/
    getListings: async function (newQuery=false) {
      if (newQuery) {
        this.currentPage = 0;
      }
      console.log(this.sortOrdersForAPI);
      const resp = (await Api.searchListings(
          this.search.productName,
          this.search.priceMin,
          this.search.priceMax,
          this.search.businessName,
          this.search.businessTypes,
          this.search.businessLocation,
          this.search.closesStartDate,
          this.search.closesEndDate,
          this.sortOrdersForAPI,
          this.perPage,
          this.currentPage - 1)).data;   // Use new listings variable as setting currencies onto this.listings doesn't update Vue
      for (const listing of resp.listings) {
        listing.business.currency = await Api.getUserCurrency(listing.business.address.country);
      }
      this.listings = resp.listings;
      this.totalResults = resp.totalItems;
    },

    /**
     * Takes a listing as input and returns the primary image for that listing
     * @return image      The image of the listing
     * @param listing     The listing to get the image of
     **/
    getPrimaryImage: function (listing) {
      const primaryImageFileName = listing.inventoryItem.product.primaryImage.fileName;
      return Api.getImage(primaryImageFileName);
    },

    /**
     * Redirects to the full listing page when a listing card is clicked on the browse/search listings page.
     * @param id The listing id that is displayed on the full listing page.
     */
    goToListingPage(id) {
      this.$router.push(`/listing/${id}`);
    },

    /**
     * Handler for when the user selects a business type to filter as. If the type is "Any Business Type",
     * then the list of business types is cleared, otherwise the type is added to the selected business types.
     */
    addBusinessType() {
      if (this.search.selectedBusinessType) {
        this.search.businessTypes.push(this.search.selectedBusinessType);
      } else {
        this.search.businessTypes = [];
      }
    },

    /**
     */
    removeTag(tag) {
      this.search.businessTypes = this.search.businessTypes.filter(x => x !== tag);
      if (this.search.selectedBusinessType === tag) {
        this.search.selectedBusinessType = '';
      }
      if (this.search.businessTypes.length === 0) {
        this.search.selectedBusinessType = null;
      }
    }
  },

  computed: {
    /**
     * Returns a list of sort orders that should be passed to the API call to search listings.
     * This exists mainly because we need special sort orders for sorting by location (as we
     * need to sort by country first, then city etc.)
     */
    sortOrdersForAPI() {
      if (this.search.sort === 'location,asc') {
        return ["business.address.country,asc", "business.address.city,asc", "business.address.suburb,asc"];
      } else if (this.search.sort === 'location,desc') {
        return ["business.address.country,desc", "business.address.city,desc", "business.address.suburb,desc"];
      } else {
        return [this.search.sort];
      }
    }
  },

  watch: {
    /**
     * This watches for those routing changes, so when the search query param is changed (eg from the navbar)
     * then the search query is re-sent
     */
    $route(to) {
      this.search.productName = to.query.searchQuery || '';
      this.getListings(true);
      this.$refs.searchInput.focus();
    },

    '$data.currentPage': {
      handler: function() {
        this.getListings();
      },
      deep: true
    }
  }
}
</script>
