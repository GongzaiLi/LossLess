<template>
  <b-card
    class="profile-card"
    style="max-width: 50rem"
  >
    <b-carousel
        id="carousel-1"
        controls
        indicators
        class="mb-2"
        :interval="0"
        ref="image_carousel"
        v-model="slideNumber"
    >
      <b-carousel-slide v-for="image in productCard.images" :key="image.id">
        <template #img>
          <div style="position: absolute; width:100%; z-index: 999999"> <!--We need a huge z-index to make sure the buttons appear over the left/right controls-->
            <b-button variant="danger" size="sm" v-b-tooltip.hover title="Delete image"><b-icon-trash-fill></b-icon-trash-fill></b-button>
            <b-btn variant="primary" size="sm" style="float: right;" v-b-tooltip.hover title="Set as primary image"><b-icon-star></b-icon-star></b-btn>
          </div>
          <!-- The class .d-block prevent browser default image alignment -->
          <img
              class="product-image d-block w-100 rounded"
              alt="Product image"
              :src="image.filename"
          >
        </template>
      </b-carousel-slide>
    </b-carousel>
    <b-input-group>
      <div v-if="!productCard.images.length">
        <img class="product-image" :src="require(`/public/product_default.png`)" alt="Product has no image">
      </div>
      <div class="w-100">
        <!--Quick and dirty way of having a button that open a file picker. The file input is invisible and button click triggers the file input element-->
        <input @change="onFileChange" multiple type="file" style="display:none" ref="filepicker" accept="image/png, image/jpeg, image/gif, image/jpg">
        <b-button class="w-100" id="add-image-btn" @click="$refs.filepicker.click()">{{productCard.images.length > 0 ? "Add more product images": "Add product images"}}</b-button>
      </div>
    </b-input-group>

    <b-form @submit.prevent="okAction" >
    <b-card-body>
        <h6><strong>ID*:</strong></h6>
        <p v-bind:hidden="disabled" style="margin:0">Ensure there are no special characters (e.g. "/","?").
          <br>This will be automatically changed into the correct format.</p>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" pattern="[a-zA-Z0-9\d\-_\s]{0,100}"  v-bind:disabled=disabled placeholder="PRODUCT-ID" v-model="productCard.id" autofocus required/>
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
            <b-input-group-text >{{currency.symbol}}</b-input-group-text>
          </template>
          <b-form-input type="number" max="1000000000" step=".01" min=0 v-bind:disabled=disabled v-model="productCard.recommendedRetailPrice" required/>
          <template #append>
            <b-input-group-text >{{currency.code}}</b-input-group-text>
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
          <b-form-textarea rows="5" type="text" maxlength="250" v-bind:disabled=disabled v-model="productCard.description "/>
        </b-input-group>
    </b-card-body>
    <hr style="width:100%">
      <div>
        <b-button  v-if="!disabled" style="float: right" variant="primary" type="submit">OK</b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
      </div>
    </b-form>
  </b-card>
</template>

<style>
.carousel-control-prev, .carousel-control-next {
  background-color: black;
  opacity: 0.2 !important;
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
import Api from "@/Api";

export default {
  name: "product-detail-card",
  props: ['product', 'disabled', 'currency', 'okAction', 'cancelAction'],
  data() {
    return {
      images: [],
      slideNumber: 0,
      productCard: {
        id: '',
        name: '',
        description: '',
        manufacturer: '',
        recommendedRetailPrice: 0,
        created: '',
        images: [],
      },
    }
  },
  mounted() {
    this.productCard = this.product;
    this.productCard.images = []
    // Sometimes the product passed in should not have a 'created' attribute, eg. if it is a new object for creation.
    if (this.productCard.created) {
      this.productCard.created = new Date(this.productCard.created).toUTCString();
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
      if (this.isCreatingProduct) {
        this.addImagePreviewsToCarousel(files);
      } else {
        await Api.uploadProductImages(this.$route.params.id, this.productCard.id, files)
        // TODO: refresh the product images if an API call was sent
      }
      this.$forceUpdate(); // For some reason pushing to the images array doesn't update the gallery, so this forces and update
      await this.$nextTick(); // Wait for the gallery to render with the added image, so we can switch to that image
      this.slideNumber = this.productCard.images.length-1; // Change the displayed image slide to the newly added image
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
          "id": url,
          "filename": url,
          fileObject: file
        });
      }
    },

  },
  computed: {
    /**
     * @returns {boolean} True if this component is being used to create a new product, False otherwise.
     */
    isCreatingProduct() {
      return !this.productCard.created; // If the product has no created attribute we must be creating a new product
    }
  }
}
</script>