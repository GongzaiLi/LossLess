<!--
Individual User profile Page. Currently displays all user data
to all logged in users, regardless of permissions.
Date: 5/3/2021
-->
<template>

  <div>
    <div v-show="!loading">
      <b-card class="profile-card shadow" no-body
              v-if="userFound"
      >
        <template #header>

          <b-row>
            <b-col md="2">
              <img v-if="userHasProfilePicture" :src="getURL()" alt="User Profile Image" width="75" height="75" class="rounded-circle"
                   style="margin-left: 5px; position: relative">
              <img v-else-if="!userHasProfilePicture" src="../../../public/profile-default.jpg" alt="User Profile Image" width="75" height="75" class="rounded-circle"
                   style="margin-left: 5px; position: relative">
            </b-col>
            <b-col md="10" class="mt-2">
              <b-row>
                <h4 class="md">{{ userData.firstName + " " + userData.lastName }}
                  <b-icon-pencil-fill
                      v-if="(userData.id === $currentUser.id || $currentUser.role === 'defaultGlobalApplicationAdmin' || $currentUser.role === 'globalApplicationAdmin') && userData.role !== 'defaultGlobalApplicationAdmin' && !$currentUser.currentlyActingAs"
                      v-b-tooltip.hover
                      title="Edit Profile"
                      id="editProfile"
                      @click="editUserModel"
                      style="cursor: pointer;"
                  />
                </h4>

              </b-row>
              <b-row>
                Member since:
                <member-since :date="userData.created"/>
              </b-row>
            </b-col>
            <b-col cols="2" sm="auto"
                   v-if="currentUserAdmin">
              <h4>{{ userRoleDisplayString }}</h4>
              <b-button
                  v-bind:variant="toggleAdminButtonVariant"
                  v-if="showToggleAdminButton"
                  @click="toggleAdmin">{{ adminButtonText }}
              </b-button>
            </b-col>

          </b-row>

          <b-row>

            <b-col cols="">

            </b-col>
          </b-row>
        </template>

        <b-list-group>
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
            <h6 v-if="userData.nickName">
              <b-row v-show="userData.nickName.length">
                <b-col cols="0">
                  <b-icon-emoji-smile-fill></b-icon-emoji-smile-fill>
                </b-col>
                <b-col cols="4"><strong>Nickname:</strong></b-col>
                <b-col>{{ userData.nickName }}</b-col>
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
            <h6>
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
            <h6 v-show="viewable">
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
      <UserDetailsModal :is-edit-user="true" :logged-in-user-admin="loggedInUserAdmin" :user-details="userData" v-on:updatedUser="updatedUserHandler"/>
    </b-modal>
  </div>
</template>

<style scoped>
.profile-card {
  max-width: 45rem;
  margin-left: auto;
  margin-right: auto;
}

h6 {
  line-height: 1.4;
}
</style>

<script>
import api from "../../Api";
import memberSince from "../model/MemberSince";
import UserDetailsModal from "./UserDetailsModal";
import EventBus from "../../util/event-bus";

export default {
  components: {
    UserDetailsModal,
    memberSince
  },

  data: function () {
    return {
      userData: {
        id: "",
        role: "",
        firstName: "",
        lastName: "",
        middleName: "",
        nickName: "",
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
      loading: true
    }
  },

  mounted() {
    const userId = this.$route.params.id;
    this.launchPage(userId);

    EventBus.$on('updatedUser', this.updatedUserHandler)
  },

  methods: {
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
    getURL() {
        return api.getImage(this.userData.profileImage.thumbnailFilename);
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
      this.getUserInfo(this.userData.id);
      this.$bvModal.hide('edit-user-profile');
    },

  },
  computed: {
    toggleAdminButtonVariant() {
      return this.userData.role === 'user' ? 'success' : 'danger';
    },
    /**
     * Combine fields of address
     */
    getAddress: function () {
      const address = this.userData.homeAddress;
      return `${address.streetNumber} ${address.streetName}, ${address.suburb}, ` +
          `${address.city} ${address.region} ${address.country} ${address.postcode}`;
    },
    /**
     * Returns the full name of the user, in the format:
     * Firstname Middlename Lastname (Nickname)
     */
    fullName: function () {
      let fullName = this.userData.firstName;
      if (this.userData.middleName) {
        fullName += ' ' + this.userData.middleName;
      }
      fullName += ' ' + this.userData.lastName;
      if (this.userData.nickName) {
        fullName += ` (${this.userData.nickName})`
      }
      return fullName;
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

    /**
     * @user user information returned from backend
     * @return Boolean True if user has a profile picture and False otherwise
     */
    userHasProfilePicture: function () {
      return !!this.userData.profileImage;
    }

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
