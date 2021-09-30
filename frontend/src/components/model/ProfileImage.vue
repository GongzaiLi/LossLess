<template>
  <div>
    <b-img v-if="uploaded" :src="imageURL"
           class="profile-image mx-auto"
           block rounded="circle" thumbnail
           alt="userImage"/>
    <b-img v-else :src="profileImage ? getURL(profileImage.fileName) : defaultImage"
           class="profile-image mx-auto"
           fluid block rounded="circle" thumbnail
           alt="default image"/>
    <input @change="openImage($event)" type="file" ref="userImagePicker"
           accept="image/png, image/jpeg, image/gif, image/jpg" class="upload-image py-2 mb-2">
    <div v-if="errors.length">
      <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }}
      </b-alert>
    </div>

    <div v-if="userLookingAtSelfOrIsAdmin">
      <b-button v-if="isUploadingFile" variant="info" class="w-100 mt-2" size="sm"
                @click="$refs.userImagePicker.click()">
        <b-spinner small v-if="isUploadingFile"/>
      </b-button>
      <b-button v-if="!uploaded && !profileImage && !isUploadingFile" variant="info" class="w-100 mt-2" size="sm"
                @click="$refs.userImagePicker.click()">
        <b-icon-image/>
        Add profile photo
      </b-button>

      <div v-if="canSave">
        <b-button id="confirmButton" variant="info" class="mt-2" size="sm" @click="save">
          <b-icon-pencil-fill/> Save </b-button>
      </div>

        <b-button id="deleteButton" v-if="(profileImage || uploaded) && !isUploadingFile" class="mt-2" variant="danger" size="sm"
                  @click="$bvModal.show('confirmDeleteImageModal')">
          <b-icon-trash-fill/>
          Delete
        </b-button>
        <b-button id="changeButton" v-if="(profileImage || uploaded) && !isUploadingFile" variant="info" class="mt-2" size="sm"
                  @click="$refs.userImagePicker.click()">
          <b-icon-pencil-fill/>
          Change
        </b-button>
    </div>

    <b-modal id="confirmDeleteImageModal" size="sm" title="Delete Image" ok-variant="danger" ok-title="Delete"
             @ok="confirmDeleteImage">
      <h6>
        Are you sure you want to <strong>delete</strong> this image?
        <strong><br>It will be permanently deleted from your account.</strong>
      </h6>
    </b-modal>
  </div>
</template>

<style scoped>

#changeButton {
  width: 49%;
  margin-left: 1%;
}

#deleteButton {
  width: 49%;
  margin-right: 1%;
}

.profile-image {
  height: 16rem;
  width: 16rem;
  object-fit: cover;
  display: inline-block
}

.upload-image {
  display: none
}

</style>

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
    defaultImage: {
      type: String,
      default: require('../../../public/user-profile-default.png')
    }

  },
  data: function () {
    return {
      profileImage: this.details,
      errors: [],
      imageFile: '',
      uploaded: false,
      isUploadingFile: false,
      imageURL: '',
      error: null,
      confirmed: false,
      canSave: false
    }
  },

  beforeMount() {
    this.$log.debug(this.details)
  },

  methods: {

    /**
     * Confirms image upload and calls method that makes the api request.
     */
    save() {
      this.confirmed = true;
      this.uploadImageRequest();
      this.canSave = false;
    },

    /**
     * when user uploads image display the image as a preview
     * @param event object for image uploaded
     **/
    openImage(event) {
      if (event.target.files[0]) {
        this.imageFile = event.target.files[0];
        this.uploadImageRequest();
        this.canSave = true;
      }
    },

    /**
     * This calls a method sends an api request to post an image, either for the user or the business.
     * If successful, it emits an event called updatedImage.
     * Else, it pushes error messages to the errors field.
     */
    async uploadImageRequest() {
      this.errors = [];
      this.isUploadingFile = true;
      if (this.confirmed) {
        if (!this.$currentUser.currentlyActingAs) {
          await this.uploadUserImage();
        } else {
          await this.uploadBusinessImage();
        }
      }

      if (!this.error) {
        this.imageURL = window.URL.createObjectURL(this.imageFile)
        this.isUploadingFile = false;
        this.uploaded = true;
        if (this.confirmed) {
          if (!this.$currentUser.currentlyActingAs) {
            EventBus.$emit("updatedUserImage");
          } else {
            EventBus.$emit("updatedBusinessImage");
          }
        }
        this.confirmed = false;

      } else {
        if (!this.profileImage) {
          this.imageURL = this.defaultImage;
        }
        this.confirmed = false
        this.isUploadingFile = false;
        if (this.error.response) {
          if (this.error.response.status === 413) {
            this.errors.push("The image you tried to upload is too large. Images must be less than 1MB in size.");
          } else {
            this.errors.push(`Uploading image failed: ${this.error.response.data.message}`);
          }
        } else {
          this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
        }
        this.error = null;
      }
    },

    /**
     * This sends an api request to post an image for the user.
     * If successful, it sets the error to back null.
     * Else, it pushes error messages to the errors field and sets the current error to the error returned.
     */
    async uploadUserImage() {
      const userId = this.$route.params.id;
      await Api.uploadProfileImage(userId, this.imageFile).then(() => {
        this.error = null;
      })
          .catch((error) => {
            this.error = error
            this.$log.debug(error);
          })
    },

    /**
     * This sends an api request to post an image or the business.
     * If successful, it sets the error to back null.
     * Else, it pushes error messages to the errors field and sets the current error to the error returned.
     */
    async uploadBusinessImage() {
      const businessId = this.$route.params.id;
      await Api.uploadBusinessProfileImage(businessId, this.imageFile).then(() => {
        this.error = null;
      })
          .catch((error) => {
            this.error = error
            this.$log.debug(error);
          })
    },

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    },

    /**
     * Deletes the image with the id stored in this.imageIdToDelete. Calls the method that makes an API call if
     * the image already exists. This should only be called when the user confirms they want to delete the image
     **/
    async confirmDeleteImage() {

      this.errors = [];
      if (!this.$currentUser.currentlyActingAs && (this.profileImage || this.uploaded)) {
        await this.deleteUserImage();
      } else {
        await this.deleteBusinessImage();
      }

      if (this.error) {
        if (this.error.response) {
          this.errors.push(`Deleting image failed: ${this.error.response.data.message}`);
        } else {
          this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
        }
      } else {
        this.canSave = false;
        this.imageURL = null;
        if (!this.$currentUser.currentlyActingAs) {
          EventBus.$emit("updatedUserImage");
        } else {
          EventBus.$emit("updatedBusinessImage");
        }
        this.profileImage = null;
        this.uploaded = false;
        this.imageFile = '';
        this.imageError = '';
      }

    },

    /**
     * This sends an api request to delete the for the user.
     * If successful, it sets the error to back null.
     * Else, it pushes error messages to the errors field and sets the current error to the error returned.
     */
    async deleteUserImage() {
      const userId = this.$route.params.id;
      await Api.deleteUserProfileImage(userId).then(() => {
        this.error = null;
      }).catch((error) => {
        this.error = error
        this.$log.debug(error);
      })
    },

    /**
     * This sends an api request to delete the for the business.
     * If successful, it sets the error to back null.
     * Else, it pushes error messages to the errors field and sets the current error to the error returned.
     */
    async deleteBusinessImage() {
      const business = this.$route.params.id;
      await Api.deleteBusinessProfileImage(business).then(() => {
        this.error = null;
      }).catch((error) => {
        this.error = error
        this.$log.debug(error);
      })
    }
  },
}
</script>
