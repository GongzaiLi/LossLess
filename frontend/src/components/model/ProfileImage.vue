<template>
  <div>
    <b-img v-if="uploaded" :src="imageURL"
           class="profile-image mx-auto"
           block rounded="circle" thumbnail
           alt="userImage"/>
    <b-img v-else :src="profileImage ? getURL(profileImage.fileName) : require('../../../public/profile-default.jpg')"
           class="profile-image mx-auto"
           fluid block rounded="circle" thumbnail
           alt="default image"/>
    <input @change="openImage($event)" type="file" ref="userImagePicker"
           accept="image/png, image/jpeg, image/gif, image/jpg" class="upload-image py-2 mb-2">
    <div v-if="errors.length">
      <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }}
      </b-alert>
    </div>

    <div v-if="userLookingAtSelfOrIsAdmin && !$currentUser.currentlyActingAs">
      <b-button v-if="isUploadingFile" variant="info" class="w-100 mt-2" size="sm"
                @click="$refs.userImagePicker.click()">
        <b-spinner small v-if="isUploadingFile"/>
      </b-button>
      <b-button v-if="!uploaded && !profileImage && !isUploadingFile" variant="info" class="w-100 mt-2" size="sm"
                @click="$refs.userImagePicker.click()">
        <b-icon-image/>
        Add profile photo
      </b-button>
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

    <b-modal id="confirmUploadImageModal" size="sm" title="Upload Image" ok-variant="success" ok-title="Upload"
             @ok="confirmUploadImageRequest">
      <h6>
        Are you sure you want to <strong>upload</strong> this image?
        <strong><br>It will permanently change your profile image.</strong>
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
  },
  data: function () {
    return {
      profileImage: this.details,
      errors: [],
      imageFile: '',
      uploaded: false,
      isUploadingFile: false,
      imageURL: '',
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
        this.$bvModal.show('confirmUploadImageModal')
      }
    },

    /**
     * This sends an api request to post an image.
     * If successful, it emits an event called updatedUser.
     * Else, it pushes error messages to the errors field.
     */
    confirmUploadImageRequest() {
      this.errors = [];
      const userId = this.$route.params.id;
      this.isUploadingFile = true;
      Api.uploadProfileImage(userId, this.imageFile).then(() => {
        this.imageURL = window.URL.createObjectURL(this.imageFile)
        this.isUploadingFile = false;
        this.uploaded = true;
        EventBus.$emit("updatedUserImage", this.imageURL);
      })
          .catch((error) => {
            this.errors = [];
            this.$log.debug(error);
            this.isUploadingFile = false;
            if (error.response) {
              if (error.response.status === 413) {
                this.errors.push("The image you tried to upload is too large. Images must be less than 1MB in size.");
              } else {
                this.errors.push(`Uploading image failed: ${error.response.data.message}`);
              }
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
     * the image already exists. This should only be called when the user confirms they want to delete the image
     **/
    confirmDeleteImage: function () {
      this.errors = [];
      const userId = this.$route.params.id;

      if (this.profileImage || this.uploaded) {  // Only make Api request if the image exists
        Api.deleteUserProfileImage(userId).then(() => {
          this.imageURL = null;
          EventBus.$emit("updatedImage", this.imageURL);
          this.profileImage = null;
          this.uploaded = false;
          this.imageFile = '';
          this.imageError = '';
        }).catch((error) => {
          this.errors = [];
          this.$log.debug(error);
          if (error.response) {
            this.errors.push(`Deleting image failed: ${error.response.data.message}`);
          } else {
            this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
          }
        })
      }
    },
  },
}
</script>