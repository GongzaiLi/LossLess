<!--
Page that stores table show the business products
Date: 15/4/2021
-->
<template>
  <div>
    <b-card v-if="canEditCatalogue" class="shadow">
      <b-card-title>Product Catalogue: {{businessName}}</b-card-title>
      <b-row align-v="center">
        <b-col md="8"><h6 class="ml-2">Click on a product to view more details</h6></b-col>
        <b-col md="4">
          <b-form-group>
            <b-button @click="openCreateProductModal" class="float-right">
              <b-icon-plus-square-fill animation="fade"/>
              Create
            </b-button>
          </b-form-group>
        </b-col>
      </b-row>

      <products-table ref="productTable" :editable="true"
                      v-on:productClicked="showProductDetails"
                      v-on:editProductClicked="openEditProductCard">
      </products-table>

      <b-modal id="product-card" hide-header hide-footer
               :no-close-on-backdrop="!isProductCardReadOnly" :no-close-on-esc="!isProductCardReadOnly"
      >
        <product-detail-card :product="productDisplayedInCard"
                             :disabled="isProductCardReadOnly"
                             :currency="currency"
                             :okAction="productCardAction"
                             :cancelAction="closeProductCardModal"
                             @imageChange="refreshProducts"
        />
        <b-alert :show="productCardError ? 120 : 0" variant="danger">{{ productCardError }}</b-alert>
      </b-modal>
    </b-card>

    <b-card id="catalogue-locked-card" v-if="!canEditCatalogue">
      <b-card-title>
        <b-icon-lock/> Can't edit product catalogue
      </b-card-title>
      <h6 v-if="businessNameIfAdminOfThisBusiness"><strong>You're an administrator of this business. To edit this catalogue, you must be acting as this business.</strong>
        <br><br>To do so, click your profile picture on top-right of the screen. Then, select the name of this business ('{{businessNameIfAdminOfThisBusiness}}') from the drop-down menu.</h6>
      <h6 v-else> You are not an administrator of this business. If you need to edit this catalogue, contact the administrators of the business. <br>
      Return to the business profile page <router-link :to="'/businesses/' + $route.params.id">here.</router-link></h6>
    </b-card>

    <b-modal id="image-error-modal" title="Some images couldn't be uploaded." ok-only>
      <h5>{{ imageError }}</h5>
      However, the product has been created successfully. If you would like to add more images to it, you can do so by
      editing the product.
    </b-modal>
  </div>
</template>

<style scoped>

h2 {
  text-align: center;
}

</style>

<script>
import productDetailCard from './ProductDetailCard';
import ProductsTable from "./ProductsTable";
import Api from "../../Api";

export default {
  components: {
    ProductsTable,
    productDetailCard
  },
  data: function () {
    return {
      businessName: "",
      business : {},
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'US Dollar',
      },
      productCardAction: null,
      productCardError: "",
      imageError: "",
      productDisplayedInCard: { images: [] },
      isProductCardReadOnly: true,
      items: [],
      perPage: 10,
      currentPage: 1,
      oldProductId: 0,
    }
  },
  mounted() {
    const businessId = this.$route.params.id;
    this.getBusinessInformation(businessId);
    this.refreshTable(businessId);
  },
  methods: {

    /**
     * Api request to get business information
     **/
    async getBusinessInformation(businessId) {
      await Api.getBusiness(businessId)
          .then((response) => {
            this.business = response.data;
            return Api.getUserCurrency(response.data.address.country);
          })
          .then(currencyData => this.currency = currencyData)
          .catch((error) => {
            this.$log.debug(error);
          });
    },


    refreshTable: async function(businessId) {
      const curBusiness = (await Api.getBusiness(businessId)).data;
      this.businessName = curBusiness.name;

      if (this.$refs.productTable.getProducts) {  // If you don't have permission to access the table the table component won't render
        await this.$refs.productTable.getProducts(curBusiness); // Only load products if the table component has rendered
      }
    },
    /**
     * modify the ID so that it doesn't display the "{businessId}-" at the start
     * @return string
     */
    setId: function (id) {
      return id.split(/-(.+)/)[1];
    },

    /**
     * when click the table will show a new model card.
     * @param product object
     */
    showProductDetails(product) {
      this.isProductCardReadOnly = true;

      this.productDisplayedInCard = Object.assign({}, product);
      this.productDisplayedInCard.id = this.setId(this.productDisplayedInCard.id);
      this.$bvModal.show('product-card');
    },

    /**
     * route to the create product page
     */
    openCreateProductModal: function () {
      this.isProductCardReadOnly = false;
      this.productCardAction = this.createProduct;

      this.productDisplayedInCard = {
        id: '',
        name: '',
        description: '',
        manufacturer: '',
        recommendedRetailPrice: 0,
        image: '',
      }
      this.$bvModal.show('product-card');
    },

    /**
     * button function when clicked shows edit card
     * @param product edit product
     */
    openEditProductCard: function (product) {
      this.isProductCardReadOnly = false;
      this.productCardAction = this.modifyProduct;

      this.productDisplayedInCard = Object.assign({}, product);
      this.oldProductId = product.id;
      this.productDisplayedInCard.id = this.setId(this.productDisplayedInCard.id);
      this.$bvModal.show('product-card');
    },

    /**
     * Closes the modal popup for the show/edit/create product card, and clears any errors displayed.
     */
    closeProductCardModal: function () {
      this.$bvModal.hide('product-card');
      this.productCardError = '';
    },

    /**
     * Makes a request to the API to create a product with the form input.
     * If successful, it will then upload all the images that the user has set for the product.
     * Then, will hide the product popup and reload the table items if successful.
     */
    async createProduct() {
      let productCreated = false;
        await Api.createProduct(this.$route.params.id, this.productDisplayedInCard)
          .then(async (createProductResponse) => {
            productCreated = true;
            this.$log.debug("Product Created", createProductResponse);
            this.$log.debug("Uploading images");
            // When creating a new product, the ProductDetailCard component will set product.images to the product images to be uploaded
            // That is a bit hacky and it would be better for the API requests to be handled in the card component itself, but we don't have time to refactor all this
            // We also need to extract the file objects from the list of images (see addImagePreviewsToCarousel in ProductDetailCard for why)
            for (let image of this.productDisplayedInCard.images) {
              let resp = await Api.uploadProductImage(this.$route.params.id, createProductResponse.data.productId, image.fileObject);
              if (image.id === this.productDisplayedInCard.primaryImage.id) {
                await Api.setPrimaryImage(this.$route.params.id, createProductResponse.data.productId, resp.data.id);
              }
            }
          })
          .then(() => {
            this.refreshProducts(); // Refresh the table of products again to get the images
            this.$bvModal.hide('product-card');
          })
          .catch((error) => {
            if (productCreated) {
              this.refreshProducts(); // Product has been created, must be image error, so refresh the table of products
              this.$bvModal.hide('product-card'); // Hide modal anyway, the product was created
              this.imageError = (error.response.status !== 413) ? error.response.data.message : "Some images you tried to upload are too large. Images must be less than 1MB in size.";
              this.$bvModal.show('image-error-modal');
            } else {
              this.productCardError = this.getErrorMessageFromApiError(error);
            }
            this.$log.debug(error);
          });
    },

    /**
     * button function for ok when clicked calls an API
     * place holder function for API task
     */
    modifyProduct: async function () {
      let editData = this.productDisplayedInCard;
      delete editData["created"];
      await Api
          .modifyProduct(this.$route.params.id, this.oldProductId, editData)
          .then((editProductResponse) => {
            this.$log.debug("Product has been edited",editProductResponse);
            this.$bvModal.hide('product-card');
            this.refreshProducts();
          })
          .catch((error) => {
            this.productCardError = this.getErrorMessageFromApiError(error);
            this.$log.debug(error);
          })
    },

    /**
     * Given an error thrown by a rejected axios (api) request, returns a user-friendly string
     * describing that error. Only applies to POST and PUT requests for products
     */
    getErrorMessageFromApiError(error) {
      if ((error.response && error.response.status === 400)) {
        return error.response.data.message;
      } else if ((error.response && error.response.status === 403)) {
        return "Forbidden. You are not an authorized administrator";
      } else if (error.request) {  // The request was made but no response was received, see https://github.com/axios/axios#handling-errors
        return "No Internet Connectivity";
      } else {
        return "Server error";
      }
    },

    /**
     * function when clicked refreshes the table so that it can be reloaded with
     * new/edited data
     */
    refreshProducts: function () {
      this.productCardError = "";
      this.refreshTable(this.$route.params.id);
    },
  },

  computed: {

    /**
     * True if the user can edit this catalogue (ie they are an admin or acting as this business)
     */
    canEditCatalogue: function() {
      return this.$currentUser.role !== 'user' ||
          (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },

    /**
     * Returns the name of the business if the user is an admin of this business, otherwise returns null
     */
    businessNameIfAdminOfThisBusiness: function() {
      for (const business of this.$currentUser.businessesAdministered) {
        if (business.id === parseInt(this.$route.params.id)) {
          return business.name;
        }
      }
      return null;
    }
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
     * This watches for those routing changes, and will update the profile with the catalogue of the business specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      const id = to.params.id;
      if (this.canEditCatalogue) {
        this.refreshTable(id);
      }
    },
  }
}
</script>
