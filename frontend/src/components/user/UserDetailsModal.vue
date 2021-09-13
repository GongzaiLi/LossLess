<!--
Page for users to input their information for registration
Date: 3/3/2021
-->


<template>
  <div>
    <b-card class="shadow">
      <h1 v-show="!isEditUser">Sign Up to Wasteless</h1>
      <br>
      <b-form
        @submit="submit"
        @input="setCustomValidities"
      >
        <b-button v-if="uploaded || userData.profileImage" class="position-absolute" variant="danger" size="sm" v-b-tooltip.hover title="Delete image" @click="openDeleteConfirmDialog">
          <b-icon-trash-fill/>
        </b-button>
        <b-img v-if="uploaded" :src="imageURL" class="mx-auto" fluid block rounded="circle"
             alt="userImage" style="height: 12rem; width: 12rem; display: inline-block" />
        <b-img v-else :src="userData.profileImage ? getURL(userData.profileImage.fileName) : require('../../../public/profile-default.jpg')"
               class="mx-auto"
               fluid block rounded="circle"
               alt="default image" style="height: 12rem; width: 12rem; display: inline-block">
        </b-img>
        <input @change="openImage($event)" type="file" style="display:none" ref="userImagePicker"
               accept="image/png, image/jpeg, image/gif, image/jpg" class="py-2 mb-2">
        <b-button variant="info" class="w-100 add-image-btn mt-2 mb-4" @click="$refs.userImagePicker.click()">Upload</b-button>

        <b-form-group>
          <strong>First Name *</strong>
          <b-form-input v-model="userData.firstName" maxLength=50 required placeholder="First Name" autofocus></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Last Name *</strong>
          <b-form-input v-model="userData.lastName" maxLength=50 required placeholder="Last Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Middle Name</strong>
          <b-form-input v-model="userData.middleName" maxLength=50 placeholder="Middle Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Nickname</strong>
          <b-form-input v-model="userData.nickname" maxLength=50 placeholder="Nickname"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Bio</strong>
          <b-form-textarea v-model="userData.bio" maxLength=250 placeholder="Enter your Bio"></b-form-textarea>
        </b-form-group>

        <b-form-group
        >
          <strong>Email *</strong>
          <b-form-input required type="email" maxLength=50 v-model="userData.email" placeholder="Email"></b-form-input>
        </b-form-group>

        <hr v-if="isEditUser">
        <b-form-group v-if="isEditUser">
          <b-form-checkbox v-model="changePassword"><strong>Change Password</strong></b-form-checkbox>
        </b-form-group>

        <b-form-group v-if="isEditUser && !currentUserAdminAndEditingAnotherUser && changePassword" class="input-group-addon">
          <strong>Enter Current Password</strong>
          <password-input v-model="userData.oldPassword" id="oldPassword" :is-required="!isEditUser || changePassword" place-holder="Current Password"/>
        </b-form-group>

        <b-form-group v-if="changePassword || !isEditUser">
          <strong>{{isEditUser ? 'New Password' : 'Password *'}}</strong>
          <password-input autocomplete="new-password" v-model="userData.newPassword" id="newPassword" :is-required="!isEditUser" place-holder="New Password"/>

          <strong>Confirm {{isEditUser ? 'New Password' : 'Password *'}}</strong>
          <password-input v-model="userData.confirmPassword" id="confirmPasswordInput" :is-required="!isEditUser" place-holder="Confirm Password"/>
        </b-form-group>
        <hr v-if="isEditUser">

        <b-form-group
        >
          <strong>Home Address *</strong>
          <address-input v-model="userData.homeAddress" :address="userData.homeAddress"/>
        </b-form-group>

        <b-form-group
        >
          <strong>Date of Birth *</strong>
          <div>Note: you must be at least 13 years old to register</div>
          <b-form-input type="date" v-model="userData.dateOfBirth" required
                        id="dateOfBirthInput"
                        placeholder="Date of Birth"
                        autocomplete="off"
                        size=30;
          />
        </b-form-group>

        <b-form-group
        >
          <strong>Phone Number</strong>
          <b-form-input v-model="userData.phoneNumber"
                        maxLength=50
                        placeholder="Phone Number"
                        autocomplete="off"
                        size=30;
          />
        </b-form-group>
        <b-row>
          <b-col cols="auto" class="mr-auto p-3">
            <b-button v-if="isEditUser" variant="primary" class="button" type="submit" id="confirm-btn">Confirm</b-button>
            <b-button v-else variant="primary" type="submit" class="button"  id="register-btn">Register</b-button>
          </b-col>
          <b-col cols="auto" class="p-3">
            <b-button v-show="isEditUser" class="button"   @click="$bvModal.hide('edit-user-profile')" style="align-self: end">Cancel</b-button>
          </b-col>
        </b-row>
      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{
            error
          }}
        </b-alert>
      </div>
      <h6 v-if="!isEditUser">
        Already have an account?
        <router-link to="/login">Login here</router-link>
      </h6>
    </b-card>
    <br>

    <b-modal ref="confirmDeleteImageModal" size="sm" title="Delete Image" ok-variant="danger" ok-title="Delete" @ok="confirmDeleteImage">
      <h6>
        Are you sure you want to <strong>delete</strong> this image?
        <strong><br>{{!uploaded ? "It will be permanently deleted from your account.": ""}}</strong>
      </h6>
    </b-modal>
  </div>
</template>


<script>
import Api from "../../Api";
import AddressInput from "../model/AddressInput";
import PasswordInput from "../model/PasswordInput";
import EventBus from "../../util/event-bus"

const MIN_AGE_YEARS = 13;
const MAX_AGE_YEARS = 120;
const UNIX_EPOCH_YEAR = 1970;

export default {
  components: {
    PasswordInput,
    AddressInput,
  },
  props: {
    isEditUser: {
      type: Boolean,
      default: false
    },
    loggedInUserAdmin: {
      type: Boolean,
      default: false
    },
    userDetails: {
      type: Object
    }
  },
  data: function () {
    return {
      userData: {
        id: "",
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: {
          streetNumber: "",
          streetName: "",
          suburb: "",
          city: "",
          region: "",
          country: "",
          postcode: ""
        },
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      changePassword: false,
      email: '',
      country: '',
      uploaded: false,
      errors: [],
      imageURL: '',
      imageFile: '',
      currentUserAdminAndEditingAnotherUser: false,
    }
  },

  beforeMount() {
    //if in edit mode set password fields to empty
    //set email and country to compare if the user has altered the values
    if (this.isEditUser) {
      this.userData = JSON.parse(JSON.stringify(this.userDetails));
      this.userData.oldPassword = '';
      this.userData.newPassword = '';
      this.userData.confirmPassword = '';
      this.email = this.userData.email;
      this.country = this.userData.homeAddress.country;
      if (this.loggedInUserAdmin && this.userData.id !== this.$currentUser.id) {
        this.currentUserAdminAndEditingAnotherUser = true;
      }
    }
    this.$log.debug(this.userData);
  },

  methods: {

    /**
     * If new password does not match old pass word return a string
     * that can be used as the value for a custom validity on the confirm password field else return empty string
     * */
    passwordMatchValidity() {
      return this.userData.newPassword !== this.userData.confirmPassword ? "Passwords do not match." : ""
    },

    /**
     * when user uploads image display the image as a preview
     * @param event object for image uploaded
     **/
    openImage(event) {
      if (event.target.files[0]) {
        this.imageFile = event.target.files[0];
        this.imageURL = window.URL.createObjectURL(event.target.files[0]);
        event.target.value = '';
        this.uploaded = true;
      }

    },

    /**
     * Gets the data from the HTML elements that is to be used for registering a user
     **/
    getRegisterData() {
      return {
        firstName: this.userData.firstName,
        lastName: this.userData.lastName,
        middleName: this.userData.middleName,
        nickname: this.userData.nickname,
        bio: this.userData.bio,
        email: this.userData.email,
        dateOfBirth: this.userData.dateOfBirth,
        phoneNumber: this.userData.phoneNumber,
        homeAddress: this.userData.homeAddress,
        password: this.userData.newPassword
      };
    },

    /**
     * Gets the data from the HTML elements that is to be used for editing a user
     **/
    getEditData() {
      let editData = {
        firstName: this.userData.firstName,
        lastName: this.userData.lastName,
        middleName: this.userData.middleName,
        nickname: this.userData.nickname,
        bio: this.userData.bio,
        email: this.userData.email,
        dateOfBirth: this.userData.dateOfBirth,
        phoneNumber: this.userData.phoneNumber,
        homeAddress: this.userData.homeAddress,
      }
      if (this.changePassword) {
        editData.newPassword = this.userData.newPassword;
        editData.password = this.userData.oldPassword;
      }
      return editData;
    },

    /**
     * Uses HTML constraint validation to set custom validity rules checks:
     * that the 'password' and 'confirm password' fields match and date of birth is valid
     * Old password present when trying to change new password (editing only)
     * See below for more info:
     * https://stackoverflow.com/questions/49943610/can-i-check-password-confirmation-in-bootstrap-4-with-default-validation-options
     */
    setCustomValidities() {
      const confirmPasswordInput = document.getElementById('confirmPasswordInput');
      confirmPasswordInput.setCustomValidity(this.passwordMatchValidity());

      const dateOfBirthInput = document.getElementById('dateOfBirthInput');
      dateOfBirthInput.setCustomValidity(this.dateOfBirthCustomValidity);
    },

    /**
     * Called when user submits form, if editing calls edit function else register function
     * if the user has changed their country and that causes a new currency the open the currency change confirm modal
     * */
    async submit(event) {
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening
      if(this.isEditUser) {
        if (this.country !== this.userData.homeAddress.country) {
          let oldCurrency = await Api.getUserCurrency(this.country);
          let newCurrency = await Api.getUserCurrency(this.userData.homeAddress.country);
          if (oldCurrency.code !== newCurrency.code) {
            if (await this.$bvModal.msgBoxConfirm("By updating your country your currency will change from " + oldCurrency.code + " to " + newCurrency.code + ". This will be updated for all future listing you create." +
                " This will not affect the currency of products in your administered business unless you " +
                "also modify the address of the business."
            )) {
              await this.updateUser();
            }
          }
        } else {
          await this.updateUser();
        }
      } else {
        await this.register()
      }
    },

    /**
     * This sends an api request to post an image.
     * If successful, it emits an event called updatedUser.
     * Else, it pushes error messages to the errors field.
     * @param id Id of user who wants an image to be uploaded.
     */
    uploadImageRequest(id) {
      Api.uploadProfileImage(id, this.imageFile).then(() => {
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
     * Uses Api.js to send a put request to user profile.
     * This is used to modify the user's details.
     * If successful, it emits an event called updatedUser.
     * Else, it pushes error messages to the errors field.
     */
    async updateUser() {
      let editData = this.getEditData();
      await Api
        .modifyUser(editData, this.$route.params.id)
          .then(() => {
            if (this.imageURL) {
              this.uploadImageRequest(this.userData.id)
            } else {
              EventBus.$emit("updatedUser");
            }
        })
        .catch((error) => {
          this.errors = [];
          this.$log.debug(error);
          if (error.response) {
            this.errors.push(`Updating user failed: ${error.response.data.message}`);
          } else {
            this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
          }
        });
    },

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    },

    /**
     * Makes a request to the API to register a user with the form input.
     * Then, will redirect to the login page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    async register() {
      let registerData = this.getRegisterData();
      this.errors = [];

      // Different form of error checking as upload attempt on images need user id, hence it isn't possible until log in
      if (this.imageURL && this.imageFile.size > 1000 * 1000) {
        this.errors.push(`The image you tried to upload is too large. Images must be less than 1MB in size.`);
        return
      }

      await Api
        .register(registerData)
        .then((loginResponse) => {
          this.$log.debug("Registered");
          if (this.imageURL) {
            this.uploadImageRequest(loginResponse.data.id);
          }
          return Api.getUser(loginResponse.data.id);
        })
        .then((userResponse) => {
          this.$currentUser = userResponse.data;
          this.$router.push({path: `/homePage`});
        })
        .catch((error) => {
          this.$log.debug(error);
          if (error.response) {
            this.errors.push(`Registration failed: ${error.response.data.message}`);
          } else {
            this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
          }
        });
    },

    /**
     * Opens the dialog to confirm if the image with given id should be deleted.
     * Stores the given id in this.imageIdToDelete
     */
    openDeleteConfirmDialog: function() {
      this.$refs.confirmDeleteImageModal.show();
    },

    /**
     * Deletes the image with the id stored in this.imageIdToDelete. Makes an API call if
     * the user already exists, otherwise will only remove image from being uploaded.
     * This should only be called when the user confirms they want to delete the image
     **/
    confirmDeleteImage: async function () {
      const userId = this.$route.params.id;
      if (this.isEditUser && this.userData.profileImage) {  // Only make Api request if the user exists and is editing
        try {
          await Api.deleteUserProfileImage(userId);
          EventBus.$emit("updatedImage");
        } catch(error) {
          this.imageError = error.response.statusText;
          this.$log.debug(error);
          return;
        }
      }
      this.userData.profileImage = '';
      this.imageURL = '';
      this.imageFile = '';
      this.uploaded = false;
      this.imageError = '';
    },

  },

  computed: {
    /**
     * Returns the HTML5 validity string based on the value of the birth date input. Birth dates must be at least
     * 13 years old and cannot be more tha 130 years old.
     * See below for more details:
     * https://stackoverflow.com/questions/49943610/can-i-check-password-confirmation-in-bootstrap-4-with-default-validation-options
     * @returns {string} A HTML5 validity string. Equal to "" (empty string) if the birthdate is valid,
     * otherwise "You must be at least 13 years old" or "Please enter a valid birthdate"
     */
    dateOfBirthCustomValidity() {
      const dateOfBirthJSDate = Date.parse(this.userData.dateOfBirth);
      if (isNaN(dateOfBirthJSDate)) {
        return "Please enter a valid birthdate";
      }

      // Taken from https://stackoverflow.com/questions/8152426/how-can-i-calculate-the-number-of-years-between-two-dates
      const dateDiffMs = Date.now() - dateOfBirthJSDate;
      const diffDate = new Date(dateDiffMs); // milliseconds from epoch
      const yearsSinceBirthDay = diffDate.getUTCFullYear() - UNIX_EPOCH_YEAR;

      if (yearsSinceBirthDay < MIN_AGE_YEARS) {
        return "You must be at least 13 years old";
      } else if (yearsSinceBirthDay >= MAX_AGE_YEARS) {
        return "You cannot be older than 120 years"
      }
      return "";
    },

  }
}
</script>
