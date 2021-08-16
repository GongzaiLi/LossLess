<!--
Listings Page
-->
<template>
  <div>
    <h4><b-link to="./?fromSearch=true"><b-icon-arrow-left/> Back to Business Profile</b-link></h4>
    <b-card style="max-width: 80em;" class="shadow">
      <b-container>
        <b-row>
          <h1>{{ business.name }} Listings</h1>
        </b-row>
        <b-row align-h="start">
          <h3>Sort by:</h3>
          <b-col md="auto">
            <b-select v-model="sortProperty" value="inventoryItem.product.name">
              <option value="inventoryItem.product.name">Product Name</option>
              <option value="price">Price</option>
              <option value="closes">Listing Closes</option>
              <option value="created">Listing Opens</option>
              <option value="quantity">Quantity</option>
            </b-select>
          </b-col>
          <b-col md="auto">
            <b-select v-model="sortDirection" value="ASC">
              <option value="ASC">Asc</option>
              <option value="DESC">Desc</option>
            </b-select>
          </b-col>
          <b-col md="auto">
            <b-button @click="getListings">Sort</b-button>
          </b-col>
          <b-col v-if="canEditListings">
            <b-form-group>
              <b-button @click="openCreateListingModal" class="float-right">
                <b-icon-plus-square-fill/>
                Create
              </b-button>
            </b-form-group>
          </b-col>
        </b-row>


        <b-row cols-lg="3" cols-md="3" style="margin-left: -38px">
          <b-col v-for="(listing,index) in cards" :key="index" class="mb-4">
            <partial-listing-card :listing="listing"></partial-listing-card>
          </b-col>
        </b-row>

        <pagination :per-page="perPage" :total-items="totalResults" v-model="currentPage" v-show="cards.length"/>
      </b-container>
      <b-modal
          id="add-listing-card" hide-header hide-footer
          :no-close-on-backdrop="!isListingCardReadOnly"
          :no-close-on-esc="!isListingCardReadOnly">
        <add-listing-card :disabled="isListingCardReadOnly"
                          :inventory="listingDisplayedInCard"
                          :current-business="business"
                          :currency="currency"
                          @itemCreated="initListingPage"
                          :refresh-listing-card="initListingPage"/>
      </b-modal>
    </b-card>
  </div>
</template>

<style scoped>
hr {
  margin-top: 0.4rem;
  margin-bottom: 0.4rem;
}

</style>

<script>
import pagination from "../model/Pagination";
import AddListingCard from "./AddListingCard";
import api from "../../Api";
import PartialListingCard from "./PartialListingCard";


export default {

  components: {
    PartialListingCard,
    pagination,
    AddListingCard,
  },
  data: function () {
    return {
      businessId: 0,
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
    initListingPage: async function() {
      this.businessId = this.$route.params.id;
      await this.getBusinessInfo(this.businessId);
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
     * open listing modal
     **/
    openCreateListingModal: function () {
      this.isListingCardReadOnly = false;
      this.listingDisplayedInCard = {
        inventoryItem: '',
        quantity: 0,
        price: 0,
        moreInfo: '',
        created: '',
        closes: ''
      }
      this.$bvModal.show('add-listing-card');
    },
    /**
     * read all the listing in for the corresponding business
     **/
    getListings: async function () {
      await api.getListings(this.businessId, this.perPage, this.currentPage - 1, this.sortProperty, this.sortDirection)
          .then((response) => {
            this.$log.debug("Listing Data", response);
            this.totalResults = response.data.totalItems;
            this.cards = response.data.listings;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },
  },

  computed: {

    /**
     * True if the user can edit this catalogue (ie they are an admin or acting as this business)
     */
    canEditListings: function () {
      return this.$currentUser.role !== 'user' ||
          (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },
  },


  watch: {

    /**
     * Watches the current user data (specifically, who the user is acting as). If this changes to someone without permission
     * to access the catalogue, then a modal is shown informing them that they will be redirected to the business's home page.
     */
    $currentUser: {
      handler() {
        if (this.canEditCatalogue) {
          this.refreshTable(this.$route.params.id);
        }
      },
      deep: true, // So we can watch all the subproperties (eg. currentlyActingAs)
    },

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

    '$data.currentPage': {
      handler: function() {
        this.getListings(this.business);
      },
      deep: true
    }


  },


}
</script>

