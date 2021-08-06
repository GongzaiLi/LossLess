<template>

  <b-card style="max-width: 80em;">
    <b-container>
      <h1>Search for Listings</h1>
      <hr>
      <div>

        <b-row >
          <b-col cols="5">
            <div class="input-group mb-2 mr-sm-2">
              <div class="input-group-prepend">
                <div class="input-group-text">Search:</div>
              </div>
              <b-input placeholder="Product Name" v-model="search.productName"></b-input>
            </div>
          </b-col>


          <b-col cols="4">
            <div class="input-group mb-2 mr-sm-2">
              <div class="input-group-prepend">
                <div class="input-group-text">Sort:</div>
              </div>
              <b-select v-model="search.sort" value="inventoryItem.product.name">
                <option value="nameAsc">Product Name A to Z</option>
                <option value="nameDesc">Product Name Z to A</option>
                <option value="businessNameAsc">Seller Name A to Z</option>
                <option value="businessNameDesc">Seller Name Z to A</option>
                <option value="priceAsc">Price Low To High</option>
                <option value="priceDesc">Price High To Low</option>
                <option value="closesAsc">Listing Closes Earlier to Later</option>
                <option value="closesDesc">Listing Closes Later to Earlier</option>
                <option value="expiryAsc">Expiry Date Earlier to Later</option>
                <option value="expiryDesc">Expiry Date Later to Earlier</option>
                <option value="countryAsc">Location A to Z</option>
                <option value="countryDesc">Location A to Z</option>
              </b-select>
            </div>

          </b-col>

          <b-col class="search_button" cols="">
            <b-button @click="doSearch">Search</b-button>
          </b-col>
          <b-col class="search_button" cols="1">
            <b-button v-b-toggle.collapse-1 variant="primary">Filter</b-button>
          </b-col>

        </b-row>
        <hr>
        <b-collapse id="collapse-1" class="mt-2">
        <b-row>

          <b-col>
            <b-input placeholder="Business Name" v-model="search.businessName"></b-input>
          </b-col>
          <b-col>
            <b-select  v-model="search.businessType" >
              <option :value="null"> Business Type</option>
              <option> Accommodation and Food Services</option>
              <option> Retail Trade</option>
              <option> Charitable organisation</option>
              <option> Non-profit organisation</option>
            </b-select>
          </b-col>
          <b-col>
            <b-input placeholder="Business Location" v-model="search.businessLocation"></b-input>
          </b-col>

        </b-row>
          <br>
        <b-row align-h="between">
          <b-col cols="7">
              <b-input-group prepend="Listing Close:">
                <b-input type="date" v-model="search.closesStartDate"></b-input>
                <label style="margin-left: 1rem;margin-right: 1rem;"> to </label>
                <b-input type="date" v-model="search.closesEndDate"> </b-input>
              </b-input-group>

          </b-col>
          <b-col cols="4" >
            <b-input-group prepend="Price:" >
              <b-input type="number" placeholder="Min:" v-model="search.priceMin"></b-input>
              <label style="margin-left: 1rem;margin-right: 1rem"> to </label>
              <b-input type="number" placeholder="Max"  v-model="search.priceMax"></b-input>
            </b-input-group>
          </b-col>
        </b-row>
          <hr>
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
              <h5><b>Seller: {{ listing.business.name }}</b></h5>
              <span>Location: {{ listing.business.address.city }}, {{ listing.business.address.country }}</span><br>
              <span>Closes:{{ listing.closes }}</span>
            <b-card-footer>
            <h5 v-if="listing.business.currency">
              {{ listing.business.currency.symbol }}{{ listing.price }} {{listing.business.currency.code}}
            </h5>
            </b-card-footer>
          </b-card>
        </b-col>
      </b-row>

      <pagination :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="cards.length"/>
    </b-container>
  </b-card>
</template>

<style>

.search_button {
  text-align: right;
}

</style>

<script>
import pagination from "../model/Pagination";
import api from "../../Api";
import testCards from "./testCards.json"

export default {

name: "ListingsSearchPage",
  components: {
    pagination,
  },
  data: function () {
    return {
      search:{
        productName:"",
        sort:"nameAsc",
        businessName:"",
        businessType:null,
        businessLocation:"",
        closesStartDate: "",
        closesEndDate:"",
        priceMin:null,
        priceMax:null,
      },
      business: {},
      cards: [],
      perPage: 12,
      currentPage: 1,
      totalResults: 0,
      mainProps: {blank: true, width: 250, height: 200},
      images: [],
      cardDemoData:testCards,
    }
  },
  mounted() {
    this.initListingPage();
    this.search.closesStartDate = this.getToday()
  },

  methods: {
    /**
     * Get today's date without the time
     * need to add one to get correct date
     * @return today's date in format yyyy-mm-dd
     **/
    getToday() {
      let date = new Date();
      return date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');
    },

    /**
     * Page initilisation function
     **/
    initListingPage: async function () {
      await this.getListings()
      .then(()=>this.getBusinessCurrency());
    },

    /**
     * Api request to get business information
     **/
    async getBusinessCurrency() {
      for (const card of this.cards) {
        await api.getUserCurrency(card.business.address.country)
            .then((response) => {
              card.business.currency = response;
            })
            .catch((error) => {
              this.$log.debug(error);
            });
      }
    },

    /**
     * read all the listing in for the corresponding business
     **/
    getListings: async function () {
      this.cards=this.cardDemoData
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

    /**
     * Placeholder for search function
     */
    doSearch(){
      console.log(this.search)
    }
  }

}
</script>

<style scoped>

</style>