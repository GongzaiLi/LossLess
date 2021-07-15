<template>
  <div
      class="profile-card"
      style="max-width: 50rem"
  >
    <b-carousel
        id="carousel-1"
        :controls="productCard.images.length > 1"
        indicators
        :interval="0"
        ref="image_carousel"
        v-model="slideNumber"
        v-if="productCard.images.length > 0"
    >
      <b-carousel-slide v-for="image in productCard.images" :key="image.id">
        <template #img>
          <div style="position: absolute; width:100%; z-index: 999999" v-if="!disabled">
            <!--We need a huge z-index to make sure the buttons appear over the left/right controls-->
            <b-button variant="danger" size="sm" v-b-tooltip.hover title="Delete image" @click="openDeleteConfirmDialog(image.id)">
              <b-icon-trash-fill/>
            </b-button>
            <b-btn variant="primary" size="sm" style="float: right;" v-b-tooltip.hover class="make-primary-btn"
                   :title="image.id === productCard.primaryImage.id ? 'This is the primary image': 'Set as primary image'"
                   :disabled="image.id === productCard.primaryImage.id"
                   @click="setPrimaryImage(image)">
              <b-icon-star v-if="image.id !== productCard.primaryImage.id"></b-icon-star>
              <b-icon-star-fill variant="warning" v-else></b-icon-star-fill>
            </b-btn>
          </div>
          <div style="position: absolute; width:100%; z-index: 999999" v-else>
            <b-btn class="make-primary-btn" disabled v-if="image.id === productCard.primaryImage.id" variant="primary" size="sm" style="float: right;" v-b-tooltip.hover
                   :title="'This is the primary image'">
              <b-icon-star-fill variant="warning"></b-icon-star-fill>
            </b-btn>
          </div>
          <!-- The class .d-block prevent browser default image alignment -->
          <img
              class="product-image d-block w-100 rounded"
              alt="Product image"
              :src="getURL(image.fileName)"
          >
        </template>
      </b-carousel-slide>
    </b-carousel>

    <b-input-group>
      <div v-if="!productCard.images.length">
        <img class="product-image" src="../../../public/product_default.png" alt="Product has no image">
      </div>
      <div class="w-100" v-if="!disabled">
        <!--Quick and dirty way of having a button that open a file picker. The file input is invisible and button click triggers the file input element-->
        <input @change="onFileChange" multiple type="file" style="display:none" ref="filepicker"
               accept="image/png, image/jpeg, image/gif, image/jpg">
        <b-button variant="info" class="w-100" id="add-image-btn" @click="$refs.filepicker.click()" :disabled="isUploadingFile || productCard.images.length >= 5">
          <strong>{{ addImageButtonText }} <b-spinner small v-if="isUploadingFile"/></strong>
        </b-button>
      </div>
      <h6 class="w-100 text-center" v-else>To edit or delete product images, edit the product.</h6>
    </b-input-group>
    <br>
    <b-card>
      <b-card-title class="text-center">Product Attributes</b-card-title>
      <b-form @submit.prevent="okAction">
      <b-card-body>
        <h6><strong>ID*:</strong></h6>
        <p v-bind:hidden="disabled" style="margin:0">Ensure there are no special characters (e.g. "/","?").
          <br>This will be automatically changed into the correct format.</p>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" pattern="[a-zA-Z0-9\d\-_\s]{0,100}" v-bind:disabled=disabled
                        placeholder="PRODUCT-ID" v-model="productCard.id" autofocus required/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Name*:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" v-bind:disabled=disabled v-model="productCard.name" required/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Manufacturer:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" v-bind:disabled=disabled v-model="productCard.manufacturer"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Recommended Retail Price:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <template #prepend>
            <b-input-group-text>{{ currency.symbol }}</b-input-group-text>
          </template>
          <b-form-input type="number" max="1000000000" step=".01" min=0 v-bind:disabled=disabled
                        v-model="productCard.recommendedRetailPrice" required/>
          <template #append>
            <b-input-group-text>{{ currency.code }}</b-input-group-text>
          </template>
        </b-input-group>

        <div v-if="disabled"><!--Only show if we're not in the 'modify' or 'create' mode-->
          <b-input-group>
            <h6><strong>Created:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-1">
            <b-form-input type="text" maxlength="50" disabled v-model="productCard.created"/>
          </b-input-group>
        </div>

        <hr style="width:100%">

        <b-input-group>
          <h6><strong>Description:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-textarea rows="5" type="text" maxlength="250" v-bind:disabled=disabled
                           v-model="productCard.description "/>
        </b-input-group>
      </b-card-body>
      <div>
        <b-button v-if="!disabled" style="float: right" variant="primary" type="submit">Apply</b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
      </div>
    </b-form>
    </b-card>

    <b-modal ref="errorModal" size="sm" title="Error" ok-only>
      {{ imageError }}
    </b-modal>

    <b-modal ref="confirmDeleteImageModal" size="sm" title="Delete Image" ok-variant="danger" ok-title="Delete" @ok="confirmDeleteImage">
      <h6>
        Are you sure you want to <strong>permanently</strong> delete this image?
      </h6>
    </b-modal>
  </div>
</template>

<style>
.make-primary-btn.disabled {
  opacity: 1 !important;
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
}

#add-image-btn {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}

.product-image {
  height: 15rem;
  object-fit: cover;
  width: 100%;
}
</style>

<script>
import Api from "../../Api.js";

export default {
  name: "product-detail-card",
  props: ['product', 'disabled', 'currency', 'okAction', 'cancelAction'],
  data() {
    return {
      isUploadingFile: false,
      slideNumber: 0,
      imageIdToDelete: null,
      productCard: {
        id: '',
        name: '',
        description: '',
        manufacturer: '',
        recommendedRetailPrice: 0,
        created: '',
        images: [],
      },
      imageError: ""
    }
  },
  mounted() {
    this.productCard = this.product;
    // Sometimes the product passed in should not have a 'created' attribute, eg. if it is a new object for creation.
    if (this.productCard.created) {
      this.productCard.created = new Date(this.productCard.created).toUTCString();
    } else {
      this.productCard.images = [];
    }

  },
  methods: {
    /**
     * Handler for when the file selected by the invisible file input changes (ie whenever the user
     * selects a file). If we're creating a new product, it will add the image to the list of images to be
     * uploaded when the product is created. Otherwise, it will simply make the Api call to upload the image
     * directly. In both cases the selected image is added to the carousel.
     */
    async onFileChange(e) {
      const files = e.target.files;
      console.log(files);
      console.log(this.productCard.images);
      if (files.length + this.productCard.images.length > 5) {
        this.imageError = `Could not upload images. Maximum number of images per product is 5. Please try selecting less images.`;
        this.$refs.errorModal.show();
        return;
      }

      if (this.isCreatingProduct) {
        this.addImagePreviewsToCarousel(files);
      } else {
        const businessId = this.$route.params.id;
        this.isUploadingFile = true;
        try {
          for (const file of files) {
            await Api.uploadProductImage(businessId, `${businessId}-${this.productCard.id}`, file);
          }
        } catch (error) {
          this.imageError = error.response.data;
          this.$refs.errorModal.show();
        }
        this.$emit('imageChange');
        this.isUploadingFile = false;
        // TODO: refresh the product images if an API call was sent
      }
      this.$forceUpdate(); // For some reason pushing to the images array doesn't update the gallery, so this forces and update
      await this.$nextTick(); // Wait for the gallery to render with the added image, so we can switch to that image
      this.slideNumber = this.productCard.images.length - 1; // Change the displayed image slide to the newly added image
    },
    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      if (this.isCreatingProduct) { // Return preview URL if we're creating to product (and so image isn't in the backend yet).
        return imageFileName;
      } else {
        return Api.getImage(imageFileName);
      }
    },
    /**
     * Given a list of image files, adds each as a preview to the product image carousel. This should be used when creating
     * the product, so users are able to see and change which images will be created along with their product.
     * This does not actually upload the files - that should be done when the user creates the product.
     */
    addImagePreviewsToCarousel(files) {
      for (let file of files) {
        let url = URL.createObjectURL(file);
        this.productCard.images.push({
          id: url,
          fileName: url,
          fileObject: file
        });
      }
      if (!this.productCard.primaryImage) {
        this.productCard.primaryImage = this.productCard.images[0];
      }
    },

    /**
     * Deletes the image with the id stored in this.imageIdToDelete. Makes an API call if
     * the product already exists, otherwise will only remove image from list of images to be created.
     * This should only be called when the user confirms they want to delete the image
     **/
    confirmDeleteImage: async function () {
      const businessId = this.$route.params.id;
      if (!this.isCreatingProduct) {  // Only make Api request if the product exists
        try {
          await Api.deleteImage(businessId, `${businessId}-${this.productCard.id}`, this.imageIdToDelete);
          this.$emit('imageChange');
        } catch(error) {
          this.imageError = error.response.statusText;
          this.$log.debug(error);
          return;
        }
      }
      this.productCard.images = this.productCard.images.filter(image => image.id !== this.imageIdToDelete);
      this.imageError = '';
      this.$forceUpdate(); // For some carousel isn't reactive to the images array, so this forces an update
    },

    /**
     * Opens the dialog to confirm if the image with given id should be deleted.
     * Stores the given id in this.imageIdToDelete
     */
    openDeleteConfirmDialog: function(imageId) {
      this.imageIdToDelete = imageId;
      this.$refs.confirmDeleteImageModal.show();
    },

    /**
     * Sets this image to be the primary image. Will send the API request if the product already exists.
     * In either case will set the primaryImage field to the new primary image, as we can't realy refresh
     * the product form the API.
     */
    setPrimaryImage(image) {
      const businessId = this.$route.params.id;
      if (!this.isCreatingProduct) {
        Api.setPrimaryImage(businessId, `${businessId}-${this.productCard.id}`, image.id);
        this.$emit('imageChange');
      }
      this.productCard.primaryImage = image;
      this.$forceUpdate(); // For some carousel isn't reactive to the images array, so this forces an update
    }
  },
  computed: {
    /**
     * @returns {boolean} True if this component is being used to create a new product, False otherwise.
     */
    isCreatingProduct() {
      return !this.productCard.created; // If the product has no created attribute we must be creating a new product
    },
    /**
     * @returns {string} The text to be displayed on the button that uploads images.
     * If we're currently uploading a file, the text displays "Uploading Image(s) ".
     * Otherwise, it displays a prompt to add product images.
     */
    addImageButtonText() {
      if (this.isUploadingFile) {
        return "Uploading Image(s) ";
      } else if (this.productCard.images.length >= 5) {
        return "Maximum product images reached";
      } else if (this.productCard.images.length > 0) {
        return "Add more product images";
      } else {
        return "Add product images";
      }
    }
  }
}
</script>
