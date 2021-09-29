<!--
Individual User profile Page. Currently displays all user data
to all logged in users, regardless of permissions.
Date: 5/3/2021
-->
<template>

  <div>
    <div v-show="!loading">
      <b-card class="profile-card shadow" no-body v-if="userFound">
        <template #header>
          <b-row>
            <b-col lg="2" class="p-0">
            <div class="profile-image-container">
            <b-img :src="userData.profileImage ? getURL(userData.profileImage.fileName) : require('../../../public/profile-default.jpg')"
                     alt="User Profile Image" width="75" height="75" class="rounded-circle"
                     id="profile-image"
                     title="View profile image"
              />
              <b-button @click="$bvModal.show('edit-profile-image')"
                        v-if="userLookingAtSelfOrIsAdmin && userData.role !== 'defaultGlobalApplicationAdmin' && !$currentUser.currentlyActingAs"
                        class="edit-business-image" size="sm"><b-icon-image/> Edit </b-button>
            </div>
            </b-col>
            <b-col class="mt-2">
              <b-row>
                <h4 class="md">{{ userData.firstName + " " + userData.lastName }}
                </h4>
              </b-row>
              <b-row>
                <p>
                  Member since: <member-since :date="userData.created"/>
                </p>
              </b-row>
            </b-col>
            <b-col lg="3" sm="12"
                   v-if="currentUserAdmin"
                   class="p-0">
              <b-icon-pencil-fill class="close edit-details"
                                  v-if="userLookingAtSelfOrIsAdmin && userData.role !== 'defaultGlobalApplicationAdmin' && !$currentUser.currentlyActingAs"
                                  @click="editUserModel"
                                  title="Update Profile Details">
              </b-icon-pencil-fill>
              <h5>{{ userRoleDisplayString }}</h5>
              <b-button
                  v-bind:variant="toggleAdminButtonVariant"
                  v-if="showToggleAdminButton"
                  @click="toggleAdmin">{{ adminButtonText }}
              </b-button>
            </b-col>
            <b-icon-pencil-fill class="close edit-details"
                                v-if="userLookingAtSelfOrIsAdmin && userData.role !== 'defaultGlobalApplicationAdmin' && !$currentUser.currentlyActingAs && !currentUserAdmin"
                                @click="editUserModel"
                                title="Update Profile Details">
            </b-icon-pencil-fill>
          </b-row>
        </template>
        <b-list-group v-if="userData.bio">
          <b-list-group-item style="border-radius: 0">
            <b-card-text style="text-align: justify">
              {{ userData.bio }}
            </b-card-text>
          </b-list-group-item>
        </b-list-group>
        <b-card-body>
          <b-container>
            <h6>
              <b-row>
                <b-col cols="0">
                  <b-icon-person-fill></b-icon-person-fill>
                </b-col>
                <b-col cols="4"><strong>Full Name:</strong></b-col>
                <b-col>{{ fullName }}</b-col>
              </b-row>
            </h6>
            <h6 v-if="userData.nickname">
              <b-row v-show="userData.nickname.length">
                <b-col cols="0">
                  <b-icon-emoji-smile-fill></b-icon-emoji-smile-fill>
                </b-col>
                <b-col cols="4"><strong>Nickname:</strong></b-col>
                <b-col>{{ userData.nickname }}</b-col>
              </b-row>
            </h6>
            <h6 v-show="viewable">
              <b-row>
                <b-col cols="0">
                  <b-icon-calendar-event-fill></b-icon-calendar-event-fill>
                </b-col>
                <b-col cols="4"><strong>Date Of Birth:</strong></b-col>
                <b-col>{{ userData.dateOfBirth }}</b-col>
              </b-row>
            </h6>
            <h6 v-show="viewable">
              <b-row>
                <b-col cols="0">
                  <b-icon-envelope-fill></b-icon-envelope-fill>
                </b-col>
                <b-col cols="4"><strong>Email:</strong></b-col>
                <b-col>{{ userData.email }}</b-col>
              </b-row>
            </h6>
            <h6 v-show="userData.phoneNumber && viewable">
              <b-row>
                <b-col cols="0">
                  <b-icon-telephone-fill></b-icon-telephone-fill>
                </b-col>
                <b-col cols="4"><strong>Phone Number:</strong></b-col>
                <b-col>{{ userData.phoneNumber }}</b-col>
              </b-row>
            </h6>
            <h6>
              <b-row>
                <b-col cols="0">
                  <b-icon-house-fill></b-icon-house-fill>
                </b-col>
                <b-col cols="4"><strong>Home Address:</strong></b-col>
                <b-col> {{ getAddress }}</b-col>
              </b-row>
            </h6>
            <h6 v-if="userData.businessesAdministered">
              <b-row v-if="userData.businessesAdministered.length">
                <b-col cols="0">
                  <b-icon-building></b-icon-building>
                </b-col>
                <b-col cols="4"><strong>Businesses Created:</strong></b-col>
                <b-col>
                  <router-link v-for="business in this.userData.businessesAdministered"
                               :to="'/businesses/'+business.id.toString()" v-bind:key="business.id">
                    <template v-if="(business.primaryAdministratorId===userData.id)">{{ business.name }}<br></template>
                  </router-link>
                </b-col>
              </b-row>
            </h6>
          </b-container>
        </b-card-body>
      </b-card>

      <b-card border-variant="secondary" header-border-variant="secondary"
              style="max-width: 45rem" no-body
              v-if="!userFound"
      >
        <template #header>
          <h4 class="mb-1">User not found</h4>
        </template>
        <b-card-body>
          <h6>
            The user you are looking for does not exist. The account may have been deleted, or you may have typed an
            invalid URL into the address bar.<br><br>
            Try
            <router-link to="/userSearch">searching for a user here.</router-link>
          </h6>
        </b-card-body>
      </b-card>
    </div>

    <b-modal id="edit-user-profile" title="Update User Profile" hide-footer scrollable>
      <UserDetailsModal :is-edit-user="true" :logged-in-user-admin="loggedInUserAdmin" :user-details="userData" v-on:updatedUser="updatedUserHandler" v-on:updateImage="updatedImageHandler"/>
    </b-modal>

    <b-modal id="edit-profile-image" title="Profile Image" hide-footer>
      <ProfileImage :details="userData.profileImage" :userLookingAtSelfOrIsAdmin=userLookingAtSelfOrIsAdmin />
    </b-modal>

    <currency-notification toast-id="user-currency-changed" :oldCurrency="oldCurrency" :new-currency="newCurrency" :is-user="true"/>
  </div>
</template>

<style scoped>
.profile-card {
  max-width: 45rem;
  margin-left: auto;
  margin-right: auto;
}

.edit-business-image {
  position: absolute;
  bottom: 0px;
  left: 3px;
  font-size: 0.7rem
}

.edit-details {
  cursor: pointer;
}

.profile-image-container {
  position: relative;
  margin-left: 1rem;
  text-align: left;
}

h6 {
  line-height: 1.4;
}

#profile-image {
  margin-left: 5px;
  position: relative;
  object-fit: cover;
}

</style>

<script>
import api from "../../Api";
import memberSince from "../model/MemberSince";
import UserDetailsModal from "./UserDetailsModal";
import ProfileImage from "../model/ProfileImage";
import EventBus from "../../util/event-bus";
import {formatAddress} from "../../util/index";
import CurrencyNotification from "../model/currencyNotification";

export default {
  components: {
    CurrencyNotification,
    UserDetailsModal,
    memberSince,
    ProfileImage,
  },

  data: function () {
    return {
      userData: {
        id: "",
        role: "",
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
        created: "",
        businessesAdministered: [],
      },
      loggedInUserAdmin: false,
      userFound: true,
      loading: true,
      oldCurrency: '',
      newCurrency : ''
    }
  },

  mounted() {
    const userId = this.$route.params.id;
    this.launchPage(userId);

    EventBus.$on('updatedUserDetails', this.updatedUserHandler);
    EventBus.$on('updatedUserImage', this.updatedImageHandler);
    EventBus.$on("currencyChanged", this.showNotification);

  },

  methods: {
    /**
     * When the user's currency has changed, then it will show a notification
     */
    showNotification(oldCurrency, newCurrency) {
      [this.oldCurrency, this.newCurrency] = [oldCurrency, newCurrency];
      this.$bvToast.show('user-currency-changed');
    },
    /**
     * set up the page
     * add debounced delay 400 ms to launch the page
     **/
    launchPage(userId) {
      this.loading = true;
      this.getUserInfo(userId);
      this.loggedInUserAdmin = this.currentUserAdmin;
    },

    /**
     * this is a get api which can take Specific user to display on the page
     * The function id means user's id, if the serve find -
     * -the user's id will response the data and keep the data into this.userData
     */
    getUserInfo: function (id) {
      api
          .getUser(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.userData = response.data;
            this.userFound = true;
            this.loading = false;
          })
          .catch((error) => {
            this.$log.debug(error);
            this.userFound = false;
            this.loading = false;
          })
    },

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return api.getImage(imageFileName);
    },

    /**
     * Revoke or give the current user the 'globalApplicationAdmin' role,
     * depending on whether the current user already has that role.
     */
    toggleAdmin: function () {
      if (this.userData.role === 'defaultGlobalApplicationAdmin' || this.userData.role === 'globalApplicationAdmin') {
        this.revokeAdmin();
      } else {
        this.giveAdmin();
      }
    },
    /**
     *  Attempts to grant the displayed user the 'globalApplicationAdmin' role
     *  by sending a request to the API. Will show an error alert if unsuccessful.
     */
    giveAdmin: function () {
      api
          .makeUserAdmin(this.userData.id)
          .then(() => {
            this.$log.debug(`Made user ${this.userData.id} admin`);
            this.userData.role = 'globalApplicationAdmin';
          })
          .catch((error) => {
            this.$log.debug(error);
          });
    },
    /**
     *  Attempts to revoke the 'globalApplicationAdmin' role from the displayed user
     *  by sending a request to the API. Will show an error alert if unsuccessful.
     */
    revokeAdmin: function () {
      api
          .revokeUserAdmin(this.userData.id)
          .then(() => {
            this.$log.debug(`Revoked admin for user ${this.userData.id}`);
            this.userData.role = 'user';
          })
          .catch((error) => {
            this.$log.debug(error);
          });
    },

    /**
     * When clicking the edit icon button then this modal shows
     */
    editUserModel: function () {
      this.$bvModal.show('edit-user-profile');
    },

    /**
     * This hides the edit user modal and refreshes the user's details
     */
    updatedUserHandler: function () {
      this.$bvModal.hide('edit-user-profile');
      this.$bvModal.hide('edit-profile-image');
      this.getUserInfo(this.userData.id);
    },

    /**
     * refreshes the user's details including their profile without
     * closing the edit user modal
     */
    updatedImageHandler: function () {
      this.getUserInfo(this.userData.id);
    },

  },
  computed: {
    /**
     * Return true if the user is looking at their own profile, or is a DGAA/GAA
     */
    userLookingAtSelfOrIsAdmin() {
      return this.userData.id === this.$currentUser.id || this.$currentUser.role === 'defaultGlobalApplicationAdmin' || this.$currentUser.role === 'globalApplicationAdmin';
    },

    toggleAdminButtonVariant() {
      return this.userData.role === 'user' ? 'success' : 'danger';
    },
    /**
     * Get correctly formatted address, deals with privacy level to only display partial address if looking at another
     * users profile and not an admin.
     */
    getAddress: function () {
      if (this.userLookingAtSelfOrIsAdmin) {
        return formatAddress(this.userData.homeAddress, 1);
      } else {
        return formatAddress(this.userData.homeAddress, 3);
      }

    },
    /**
     * Returns the full name of the user, in the format:
     * Firstname Middlename Lastname (Nickname)
     */
    fullName: function () {
      let middleName = this.userData.middleName ? ` ${this.userData.middleName} ` : ' ';
      return  `${this.userData.firstName}${middleName}${this.userData.lastName}`;
    },
    /**
     * User friendly display string for the user role. Converts the user role
     * string given by the api (eg. 'user', 'globalApplicationAdmin') to
     * a more user-friendly string to be displayed (eg. 'User', 'Site Admin')
     */
    userRoleDisplayString: function () {
      switch (this.userData.role) {
        case 'globalApplicationAdmin':
          return "Site Admin";
        case 'defaultGlobalApplicationAdmin':
          return "Default Site Admin";
        default:
          return "User";
      }
    },
    /**
     * Computed function that returns a boolean. True if the user role should be shown in the profile page, false otherwise.
     */
    currentUserAdmin: function () {
      return this.$currentUser.role === 'defaultGlobalApplicationAdmin' || this.$currentUser.role === 'globalApplicationAdmin';
    },
    /**
     * Computed function that returns a boolean. True if the Make/Revoke admin button should be shown in the profile page, false otherwise.
     */
    showToggleAdminButton: function () {
      return this.userData.role !== 'defaultGlobalApplicationAdmin' && this.userData.id !== this.$currentUser.id;
    },
    /**
     * Toggles the button text to add/remove admin privileges on a profile based on the user's role
     */
    adminButtonText: function () {
      if (this.userData.role === 'globalApplicationAdmin') {
        return "Remove Admin";
      } else {  // Button won't even appear if they are default global admin so this is fine
        return "Make Admin";
      }
    },

    /**
     * viewable is user is legal to view other's user information
     **/
    viewable: function () {
      return this.$currentUser.role === 'defaultGlobalApplicationAdmin' || this.$currentUser.id === this.userData.id
          || (this.$currentUser.role === 'globalApplicationAdmin' && this.userData.role !== 'defaultGlobalApplicationAdmin');
    },
  },
  watch: {
    /**
     * When the user navigates from /user/foo to /user/bar, this component is re-used. This watches for those routing
     * changes, and will update the user profile with the data of the user specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      const userId = to.params.id;
      this.launchPage(userId);
    },
  },

}
</script>
