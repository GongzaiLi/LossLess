<!--
Page for users to input their information for registration
Date: 3/3/2021
-->


<template>
  <div>
    <b-card class="shadow">
    <b-form
        @submit="submit"
        @input="setCustomValidities"
      >
      <h1 v-if="!isEditUser"><img src="../../../public/logo.png" style="width: 2.5em" alt="LossLess Logo"> Sign Up to LossLess</h1>
      <h5 v-else>User Details</h5>
      <hr>
      <b-form-group>
          <strong>First Name *</strong>
          <b-form-input v-model="userData.firstName" maxLength=50 required placeholder="First Name" autofocus trim></b-form-input>
        </b-form-group>

        <b-form-group>
          <strong>Last Name *</strong>
          <b-form-input v-model="userData.lastName" maxLength=50 required placeholder="Last Name" trim></b-form-input>
        </b-form-group>

        <b-form-group>
          <strong>Middle Name</strong>
          <b-form-input v-model="userData.middleName" maxLength=50 placeholder="Middle Name" trim></b-form-input>
        </b-form-group>

        <b-form-group>
          <strong>Nickname</strong>
          <b-form-input v-model="userData.nickname" maxLength=50 placeholder="Nickname" trim></b-form-input>
        </b-form-group>

        <b-form-group>
          <strong>Bio</strong>
          <b-form-textarea v-model="userData.bio" maxLength=250 placeholder="Enter your Bio"></b-form-textarea>
        </b-form-group>

        <b-form-group>
          <strong>Email *</strong>
          <b-form-input required type="email" maxLength=50 v-model="userData.email" placeholder="Email" trim></b-form-input>
        </b-form-group>

        <hr v-if="isEditUser">
        <b-form-group v-if="isEditUser">
          <b-form-checkbox v-model="changePassword"><strong>Change Password</strong></b-form-checkbox>
        </b-form-group>

        <b-form-group v-if="isEditUser && !currentUserAdminAndEditingAnotherUser && changePassword" class="input-group-addon">
          <strong>Enter Current Password</strong>
          <password-input v-model="userData.oldPassword" id="oldPassword" :min-password-length="0" :is-required="!isEditUser || changePassword" place-holder="Current Password"/>
        </b-form-group>

        <b-form-group v-if="changePassword || !isEditUser">
          <strong>{{isEditUser ? 'New Password' : 'Password *'}}</strong>
          <password-input autocomplete="new-password" v-model="userData.newPassword" :min-password-length="8" id="newPassword" :is-required="changePassword ? changePassword : !isEditUser" place-holder="New Password"/>

          <strong>Confirm {{isEditUser ? 'New Password' : 'Password *'}}</strong>
          <password-input v-model="userData.confirmPassword" id="confirmPasswordInput" :min-password-length="8" :is-required="!isEditUser" place-holder="Confirm Password"/>
        </b-form-group>
        <hr v-if="isEditUser">

        <b-form-group>
          <strong>Home Address *</strong>
          <address-input v-model="userData.homeAddress" :address="userData.homeAddress"/>
        </b-form-group>

        <b-form-group>
          <strong>Date of Birth *</strong>
          <div v-if="isEditUser">Note: you must be at least 13 years old to use this site</div>
          <div v-else> Note: you must be at least 13 years old to register </div>
          <b-form-input type="date" v-model="userData.dateOfBirth" required
                        id="dateOfBirthInput"
                        placeholder="Date of Birth"
                        autocomplete="off"
                        size=30;
          />
        </b-form-group>

        <b-form-group>
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
            <b-button v-show="isEditUser" class="button" id="cancel-button m-0" @click="$bvModal.hide('edit-user-profile')">Cancel</b-button>
          </b-col>
          <b-col cols="auto" class="p-3">
            <b-button v-if="isEditUser" variant="primary" class="button m-0" type="submit" id="confirm-btn">Confirm</b-button>
            <b-button v-else variant="primary" type="submit" class="button m-0"  id="register-btn">Register</b-button>
          </b-col>
        </b-row>
      </b-form>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{
            error
          }}
        </b-alert>
      </div>
      <h6 v-if="!isEditUser" class="float-right">
        Already have an account?
        <router-link to="/login">Login here</router-link>
      </h6>
    </b-card>
    <br>
  </div>
</template>

<style scoped>
#cancel-button {
  align-self: end
}
</style>

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
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
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
      if (this.changePassword || !this.isEditUser) {
        const confirmPasswordInput = document.getElementById('confirmPasswordInput');
        confirmPasswordInput.setCustomValidity(this.passwordMatchValidity());
      }

      const dateOfBirthInput = document.getElementById('dateOfBirthInput');
      dateOfBirthInput.setCustomValidity(this.dateOfBirthCustomValidity);
    },

    /**
     * Called when user submits form, if editing calls edit function else register function
     * if the user has changed their country and that causes a new currency the open the currency change confirm modal
     * */
    async submit(event) {
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening
      if (this.isEditUser) {
        await this.updateUser();
      } else {
        await this.register()
      }
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
          .then(async () => {
            EventBus.$emit("updatedUserDetails");
            await this.notificationCurrencyChange();
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
     * Compares the currency of the business's old country to their new country and gives a notification if it
     * has changed.
     * */
    notificationCurrencyChange: async function () {
      if (this.country !== this.userData.homeAddress.country) {
        EventBus.$emit("notificationUpdate");
        const oldCurrency = await Api.getUserCurrency(this.country);
        const newCurrency = await Api.getUserCurrency(this.userData.homeAddress.country);
        if (oldCurrency.code !== newCurrency.code) {
          EventBus.$emit("currencyChanged", oldCurrency.code, newCurrency.code);
        }
      }
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

      await Api
        .register(registerData)
        .then((loginResponse) => {
          this.$log.debug("Registered");
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
