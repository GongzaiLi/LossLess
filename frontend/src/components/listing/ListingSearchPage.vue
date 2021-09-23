<template>
  <b-overlay :show="loading">
  <b-card class="b_card_main shadow-lg" id="listing-search">
    <b-container>
      <h1>Search Listings</h1>
      <hr>

      <b-form @submit.prevent="getListings(true)">
        <b-row align-h="around">
          <b-col cols="12" md="5">
            <b-input-group prepend="Search:">
              <b-input ref="searchInput" placeholder="All Product Names" v-model="search.productName"></b-input>
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
          <b-col class="search_button" cols="3" md="2">
            <b-button v-b-toggle.filter_collapse><b-icon-sliders/> Filter</b-button>
          </b-col>

          <b-col class="search_button" cols="3" md="1">
            <b-button type="submit" variant="primary">Search</b-button>
          </b-col>
        </b-row>
        <hr>

        <b-collapse id="filter_collapse" class="mt-2">
          <b-row>
            <b-col cols="12" md="4">
              <label>Business Name:</label>
              <b-input placeholder="All Businesses" v-model="search.businessName"></b-input>
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
              <b-input placeholder="All countries, cities or suburbs" v-model="search.businessLocation"></b-input>
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
                  <b-input placeholder="Any" :min=search.closesStartDate type="date" v-model="search.closesEndDate"></b-input>
                </div>
                <label class="any_label">(Any if blank)</label>
                <label v-if="invalidDateRange" class="rangeWarning mb-2 ml-2">
                  <b-icon icon="exclamation-circle-fill"></b-icon> The dates may be the wrong way around
                </label>
              </div>
            </b-col>
            <b-col cols="12" md="4">

              <div class="input-group mb-2 mr-sm-2">
                <div class="input-group-prepend">
                  <div class="input-group-text">Price:</div>
                </div>
                <div>
                  <b-input class="price_min" step="0.01" min="0.00" max="1000000.00" type="number" placeholder="0" v-model="search.priceMin"></b-input>
                </div>
                <label class="to_label"> to </label>
                <div>
                  <b-input class="price_max" step="0.01" :min=search.priceMin max="1000000.00" type="number" placeholder="Any" v-model="search.priceMax"></b-input>
                </div>
                <label v-if="invalidPriceRange" class="rangeWarning mb-2 ml-2">
                  <b-icon icon="exclamation-circle-fill"></b-icon> Lowest price must come before highest price
                </label>
              </div>

            </b-col>
            <b-col class="search_button" cols="3" md="1">
              <b-button @click="clearFilters" variant="secondary">Clear</b-button>
            </b-col>
          </b-row>
          <hr>
        </b-collapse>
      </b-form>

      <b-row class="listing_row" cols-lg="3" cols-md="3">
        <b-col v-for="(listing,index) in listings" :key="index" class="mb-4">
          <partial-listing-card :listing="listing" :search-query="search"
                                @click.native="$router.push({path: `/listings/${listing.id}`, query: {fromSearch : true}})"/>
        </b-col>
      </b-row>
      <h2 v-if="listings.length === 0 && initialized">Unfortunately, no listings matched your search.</h2>

      <pagination ref="listingsSearchPag" :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="listings.length"/>
    </b-container>
  </b-card>
  </b-overlay>
</template>

<style>

.rangeWarning {
  color: orangered
}

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

.any_label {
  margin-top: auto;
}

.price_min {
  max-width: 6rem
}

.price_max {
  max-width: 7rem
}
#listing-search {
  max-width: initial;
}
</style>

<script>
import pagination from "../model/Pagination";
import Api from "../../Api";
import {getToday} from "../../util";
import PartialListingCard from "./PartialListingCard";

export default {

  name: "ListingsSearchPage",
  components: {
    PartialListingCard,
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
      loading: false,
      business: {},
      listings: [],
      perPage: 9,
      currentPage: 1,
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
     * @param newQuery True if this query should reset pagination back to 0
     **/
    getListings: async function (newQuery=false) {
      const timer = setTimeout(() =>  this.loading = true, 500);

      if (newQuery) {
        this.currentPage = 1;
        this.$refs.listingsSearchPag.currentPage = 1; // We have to do this because the v-model doesn't update when we only set data.currentPage
      }
      let closesStartDate = this.search.closesStartDate
      if (this.search.closesStartDate) {
        closesStartDate = this.search.closesStartDate.toString() + " 00:00:00";
      }
      let closesEndDate = this.search.closesEndDate
      if (this.search.closesEndDate) {
        closesEndDate = this.search.closesEndDate + " 23:59:59"
      }

      const resp = (await Api.searchListings(
          this.search.productName,
          this.search.priceMin,
          this.search.priceMax,
          this.search.businessName,
          this.search.businessTypes,
          this.search.businessLocation,
          closesStartDate,
          closesEndDate,
          this.sortOrdersForAPI,
          this.perPage,
          this.currentPage - 1)).data;   // Use new listings variable as setting currencies onto this.listings doesn't update Vue
      this.listings = resp.listings;
      this.totalResults = resp.totalItems;

      this.loading = false;
      clearTimeout(timer);
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
    },

    /**
     * Clears all the filter options
     */
    clearFilters() {
      this.search.productName = ""
      this.search.sort = "inventoryItem.product.name,asc"
      this.search.businessName = ""
      this.search.selectedBusinessType = null
      this.search.businessLocation = ""
      this.search.closesStartDate = getToday()
      this.search.closesEndDate = ""
      this.search.priceMin = ""
      this.search.priceMax = ""
      this.search.businessTypes = []
    },
  },



  computed: {

    /**
     *  Compares the listing closes start and end date for filtering listings.
     *  This is used to show a warning if the date range is the wrong way around.
     *
     * @return True if closes start date is after closes end date. False otherwise.
     */
    invalidDateRange() {
        return new Date(this.search.closesStartDate) > new Date(this.search.closesEndDate);
    },

    /**
     *  Compares the listing price and end date for filtering listings.
     *  This is used to show a warning if the price range is the wrong way around.
     *
     * @return True if closes start date is after closes end date. False otherwise.
     */
    invalidPriceRange() {
      return (this.search.priceMin > this.search.priceMax) && this.search.priceMax;
    },


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
    "$route": {
      handler: function(to) {
        if (to.name === 'listings-search' && localStorage.getItem('listingPurchased') === "true") {
          this.getListings();
          localStorage.setItem('listingPurchased', "false");
        }
      },
      deep: true
    },
    '$data.currentPage': {
      handler: function() {
        this.getListings();
      },
      deep: true
    },
  }
}
</script>
