<template>
  <div>
    <b-img v-if="uploaded" :src="imageURL" class="mx-auto" fluid block rounded="circle"
           alt="userImage" style="height: 12rem; width: 12rem; display: inline-block" />
    <b-img v-else :src="profileImage ? getURL(profileImage.fileName) : require('../../../public/profile-default.jpg')"
           class="mx-auto"
           fluid block rounded="circle"
           alt="default image" style="height: 12rem; width: 12rem; display: inline-block"/>
    <input @change="openImage($event)" type="file" style="display:none" ref="userImagePicker"
           accept="image/png, image/jpeg, image/gif, image/jpg" class="py-2 mb-2">
    <div v-if="errors.length">
      <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }}
      </b-alert>
    </div>

    <div v-if="userLookingAtSelfOrIsAdmin && !$currentUser.currentlyActingAs">
      <h6 style="text-align: center"> A picture helps people recognize you. </h6>
      <b-button v-if="!profileImage" variant="info" class="w-100 mt-2 mb-4" size="sm" @click="$refs.userImagePicker.click()">
        <b-icon-image/> Add profile photo
      </b-button>
      <b-button v-if="profileImage" class="w-50 mt-2 mb-4" variant="danger" size="sm" title="Delete image" @click="$bvModal.show('confirmDeleteImageModal')">
        <b-icon-trash-fill/> Delete
      </b-button>
      <b-button v-if="profileImage" variant="info" class="w-50 mt-2 mb-4" size="sm" @click="$refs.userImagePicker.click()">
        <b-icon-pencil-fill/> Change
      </b-button>
    </div>

    <b-modal id="confirmDeleteImageModal" size="sm" title="Delete Image" ok-variant="danger" ok-title="Delete" @ok="confirmDeleteImage">
      <h6>
        Are you sure you want to <strong>delete</strong> this image?
        <strong><br>{{!uploaded ? "It will be permanently deleted from your account.": ""}}</strong>
      </h6>
    </b-modal>
  </div>
</template>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus"

export default {
  props: {
    details: {
      type: Object,
      default: null
    },
    userLookingAtSelfOrIsAdmin: {
      type: Boolean,
      default: false
    },
  },
  data: function () {
    return {
      profileImage: this.details,
      uploaded: false,
      errors: [],
      imageURL: '',
      imageFile: '',
    }
  },

  beforeMount() {
    this.$log.debug(this.details)
  },

  methods: {

    /**
     * when user uploads image display the image as a preview
     * @param event object for image uploaded
     **/
    openImage(event) {
      if (event.target.files[0]) {
        this.imageFile = event.target.files[0];
        this.imageURL = window.URL.createObjectURL(event.target.files[0]);
        this.uploadImageRequest()
        this.uploaded = true;
      }
    },

    /**
     * This sends an api request to post an image.
     * If successful, it emits an event called updatedUser.
     * Else, it pushes error messages to the errors field.
     */
    uploadImageRequest() {
      this.errors = [];
      const userId = this.$route.params.id;

      Api.uploadProfileImage(userId, this.imageFile).then(() => {
        EventBus.$emit("updatedUser");
      })
          .catch((error) => {
            this.errors = [];
            this.$log.debug(error);
            if (error.response) {
              if (error.response.status === 413) {
                this.errors.push("The image you tried to upload is too large. Images must be less than 1MB in size.");
                return
              }
              this.errors.push(`Uploading images failed: ${error.response.data.message}`);
            } else {
              this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
            }
          })
    },

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    },

    /**
     * Deletes the image with the id stored in this.imageIdToDelete. Makes an API call if
     * the user already exists, otherwise will only remove image from being uploaded.
     * This should only be called when the user confirms they want to delete the image
     * Method moved from UserDetailsModal, method written by ojc31.
     **/
    confirmDeleteImage: async function () {
      const userId = this.$route.params.id;
      if (this.profileImage) {  // Only make Api request if the user exists and is editing
        try {
          await Api.deleteUserProfileImage(userId);
          EventBus.$emit("updatedImage");
        } catch (error) {
          this.imageError = error.response.statusText;
          this.$log.debug(error);
          return;
        }
      }
      this.profileImage = null
      this.imageURL = '';
      this.imageFile = '';
      this.uploaded = false;
      this.imageError = '';
    },

  },
}
</script>