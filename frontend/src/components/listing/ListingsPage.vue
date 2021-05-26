<!--
Listings Page
-->
<template>
  <b-card>
    <b-container>
    <h1 align="middle">{{ business.name }} Listings</h1>
    <b-row align-h="start">
      <h3>Sort by:</h3>
      <b-col md="auto">
        <b-select v-model="sortProperty" value="name">
          <option value="name">Product Name</option>
          <option value="price">Price</option>
          <option value="closing">Listing Closes</option>
          <option value="created">Listing Opens</option>
        </b-select>
      </b-col>
      <b-col md="auto">
        <b-select v-model="sortDirection" value="asc">
          <option value="asc">Asc</option>
          <option value="desc">Desc</option>
        </b-select>
      </b-col>
      <b-col md="auto">
        <b-button @click="sortListings">Sort</b-button>
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


    <b-row cols-lg="3" style="margin-left: -38px">
      <b-col v-for="(listing,index) in splitListings()" :key="index" class="mb-4">
        <b-card style="min-width: 17rem; height: 100%">
          <b-img v-bind="mainProps" thumbnail fluid style="border-radius: 10px" blank-color="#777"
                 alt="Default Image"></b-img>
          <hr>
          <b-card-title>{{ listing.quantity }} x {{ listing.inventoryItem.product.name }}</b-card-title>
          <b-card-sub-title v-if="listing.inventoryItem.product.manufacturer">
            {{ listing.inventoryItem.product.manufacturer }}
          </b-card-sub-title>
          <b-card-text>
            <span>{{ listing.inventoryItem.product.description }}</span><br>
            <hr>
            <b-list-group flush>
              <b-card-text>
                <span
                    v-if="listing.inventoryItem.manufactured">Manufactured: {{ listing.inventoryItem.manufactured }}<br></span>
                <span
                    v-if="listing.inventoryItem.bestBefore">Best before: {{ listing.inventoryItem.bestBefore }}<br></span>
                <span v-if="listing.inventoryItem.sellBy">Sell by: {{ listing.inventoryItem.sellBy }}<br></span>
                <span v-if="listing.inventoryItem.expires">Expires: {{ listing.inventoryItem.expires }}</span>
              </b-card-text>
            </b-list-group>
            <hr>
            <b-list-group flush>
              <h3>
                {{ currency.symbol }}{{ listing.price }} {{currency.code}}
              </h3>
              <span v-if="listing.moreInfo">{{ listing.moreInfo }}</span>
            </b-list-group>
          </b-card-text>
          <b-card-footer>
            Created:{{ listing.created }}<br>
            <span v-if="listing.closes">Closes:{{ listing.closes }}</span>
          </b-card-footer>
        </b-card>
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


export default {

  components: {
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
      sortProperty: 'name',
      sortDirection: 'asc',
      perPage: 12,
      currentPage: 1,
      totalResults: 0,
      mainProps: {blank: true, width: 250, height: 200},
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
      this.sortListings();
      this.totalResults = this.cards.length
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
     * Takes two inputs and compares them to each other based on the variable sortProperty
     * @param  {string} a string that is being paired against
     * @param  {string} b
     * @return int
     **/
    compare(a, b) {
      let less = 1;
      let more = -1;
      if (this.sortDirection === 'asc') {
        less = -1;
        more = 1;
      }
      if (this.sortProperty === 'name') {
        if (a.inventoryItem.product.name < b.inventoryItem.product.name) {
          return less;
        }
        if (a.inventoryItem.product.name > b.inventoryItem.product.name) {
          return more;
        }
      } else if (this.sortProperty === 'created') {
        if (a.created < b.created) {
          return less;
        }
        if (a.created > b.created) {
          return more;
        }
      } else if (this.sortProperty === 'closing') {
        if (a.closes < b.closes) {
          return less;
        }
        if (a.closes > b.closes) {
          return more;
        }
      } else if (this.sortProperty === 'price') {
        if (a.price < b.price) {
          return less;
        }
        if (a.price > b.price) {
          return more;
        }
      }

      return 0;
    },
    /**
     * Sorts all the cards according to the custom sort function compare()
     **/
    sortListings() {
      this.cards.sort(this.compare)
    },

    /**
     * Splits all the listings for pagination
     **/
    splitListings() {
      return this.cards.slice((this.currentPage - 1) * this.perPage, this.perPage * this.currentPage);
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
      await api.getListings(this.businessId)
          .then((response) => {
            this.$log.debug("Listing Data", response);
            this.cards = response.data;
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
  },

}
</script>

