<!--
Individual Business profile Page. Currently displays all business data
Date: 29/03/2021
-->
<template>
  <div v-show="!loading">
    <h4 class="profile-card">
      <b-link v-if="$route.query.fromSearch" variant="info" to="/search">
        <b-icon-arrow-left/>
        Back to results
      </b-link>
    </h4>
    <b-card class="profile-card shadow" no-body
            v-if="businessFound"
    >
      <template #header>
        <b-row>
          <div class="profile-image-container">
            <b-img
                :src="businessData.profileImage ? getURL(businessData.profileImage.fileName) : require('../../../public/business-profile-default.jpeg')"
                alt="User Profile Image" width="75" height="75" class="rounded-circle"
                id="profile-image"
                title="View profile image"
            />
            <b-button v-if="isAdminOfThisBusiness && isAdmin || $currentUser.role !== 'user'"
                      class="edit-business-image" size="sm"
                      @click="$bvModal.show('edit-business-image')"
            >
              <b-icon-image/>
              Edit
            </b-button>
          </div>
          <b-col class="mt-2">
            <h4 class="mb-1">{{ businessData.name }}</h4>
            Registered on:
            <member-since :date="businessData.created"/>
          </b-col>
          <b-icon-pencil-fill v-if="isAdminOfThisBusiness && isAdmin || $currentUser.role !== 'user'"
                              class="close edit-details" @click="$bvModal.show('edit-business-profile')"
                              title="Update Business Details"></b-icon-pencil-fill>
        </b-row>
      </template>

      <b-list-group border-variant="secondary" v-show="businessData.description">
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            {{ businessData.description }}
          </b-card-text>
        </b-list-group-item>
      </b-list-group>

      <b-card-body>
        <b-container>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-tags></b-icon-tags>
              </b-col>
              <b-col cols="4"><strong>Type:</strong></b-col>
              <b-col>{{ businessData.businessType }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-geo-alt></b-icon-geo-alt> <!-- geo-alt-->
              </b-col>
              <b-col cols="4"><strong>Address:</strong></b-col>
              <b-col>{{ getAddress }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row v-if="businessData.email">
              <b-col cols="0">
                <b-icon-envelope></b-icon-envelope>
              </b-col>
              <b-col cols="4"><strong>Email:</strong></b-col>
              <b-col>{{ businessData.email }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row v-if="businessData.phoneNumber">
              <b-col cols="0">
                <b-icon-phone-vibrate></b-icon-phone-vibrate>
              </b-col>
              <b-col cols="4"><strong>Phone Number:</strong></b-col>
              <b-col>{{ businessData.phoneNumber }}</b-col>
            </b-row>
          </h6>

          <router-link v-if="isAdmin" :to="{ name: 'product-catalogue', params: { id: businessData.id }}">
            <b-button class="business-manage-button" type="submit" variant="primary">
              <b-icon-newspaper/>
              Product Catalogue
            </b-button>
          </router-link>
          &nbsp;
          <router-link v-if="isAdmin" :to="{ name: 'inventory-page', params: { id: businessData.id }}">
            <b-button class="business-manage-button" type="submit" variant="primary">
              <b-icon-box-seam/>
              Inventory
            </b-button>
          </router-link>
          &nbsp;
          <router-link :to="{ name: 'listings-page', params: { id: businessData.id }}">
            <b-button class="business-manage-button" type="submit" variant="primary">
              <b-icon-receipt/>
              Sales List
            </b-button>
          </router-link>
          &nbsp;
          <router-link v-if="isAdmin" :to="{ name: 'sales-report-page', params: { id: businessData.id }}">
            <b-button class="business-manage-button" type="submit" variant="primary">
              <b-icon-graph-up/>
              Sales Report
            </b-button>
          </router-link>

        </b-container>
      </b-card-body>


      <b-list-group v-if="isAdminOfThisBusiness && !isAdmin" border-variant="secondary">
        <b-list-group-item>
          <h6><strong>You're an administrator of this business. To view the business inventory
            and catalogue, you must first be acting as this business.</strong>
            <br><br>To do so, click your profile picture on top-right of the screen. Then, select the name of this
            business
            ('{{ businessData.name }}') from the drop-down menu.</h6>
        </b-list-group-item>
      </b-list-group>

      <b-list-group border-variant="secondary" v-if="isAdmin">
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            <h4 class="mb-1">Administrators</h4>
          </b-card-text>
          <b-row class="adminRow">
            <b-col cols="12">
              <b-table hover
                       striped
                       table-class="text-nowrap"
                       responsive="lg"
                       small
                       no-border-collapse
                       stacked="sm"
                       show-empty
                       @row-clicked="rowClickHandler"
                       :fields="fields"
                       :items="businessData.administrators"
                       ref="businessAdministratorsTable">
                <template #empty>
                  <h3 class="no-results-overlay">No results to display</h3>
                </template>
                <template #cell(actions)="row">
                  <div v-if="checkCanRevokeAdmin(row.item.id)">
                    <b-button size="sm" @click="removeAdminClickHandler(row.item.id)" class="mr-1">
                      Revoke Admin
                    </b-button>
                  </div>
                </template>
              </b-table>
            </b-col>
          </b-row>
          <br>
          <h6 class="font-weight-bold">Assign a New Administrator:</h6>
          <b-button @click="showMakeAdminModal()">Add Admin</b-button>
        </b-list-group-item>
      </b-list-group>
    </b-card>


    <!--The business not found-->
    <b-card border-variant="secondary" header-border-variant="secondary"
            style="max-width: 50rem" no-body
            v-if="!businessFound"
    >
      <template #header>
        <h4 class="mb-1">Business not found</h4>
      </template>
      <b-card-body>
        <h6>
          <b-col>
            The business you are looking for does not exist. The account may have been deleted, or you may have typed an
            invalid URL into the address bar.
          </b-col>
          <b-col> Try again
            <router-link to="/businesses">Create a new business here.</router-link>
          </b-col>
        </h6>
      </b-card-body>
    </b-card>

    <b-modal id="admin-modal" hide-header hide-footer size="lg">
      <make-admin-modal :make-admin-action="this.makeAdminAction"/>
      <b-alert :show="makeAdminError.length > 0 ? 120 : 0" variant="danger">{{ makeAdminError }}</b-alert>
    </b-modal>

    <b-modal id="edit-business-profile" title="Update Business Profile" hide-footer scrollable>
      <CreateEditBusiness :is-edit-business="true" :business-details="businessData"/>
    </b-modal>

    <b-modal id="edit-business-image" title="Profile Image" hide-footer>
      <ProfileImage :details="businessData.profileImage"
                    :userLookingAtSelfOrIsAdmin='isAdmin || isAdminOfThisBusiness'
                    :default-image="require('../../../public/business-profile-default.jpeg')"/>
    </b-modal>

    <currency-notification toast-id="business-currency-changed" :oldCurrency="oldCurrency" :new-currency="newCurrency"
                           :is-user="false"/>
  </div>


</template>

<style scoped>

.adminRow {
  cursor: pointer;
}

.profile-card {
  max-width: 50rem;
  margin-left: auto;
  margin-right: auto;
}

.edit-business-image {
  position: absolute;
  bottom: 0;
  left: 15px;
  font-size: 0.7rem
}

.edit-details {
  cursor: pointer;
}

.profile-image-container {
  position: relative;
  text-align: center;
}

#profile-image {
  margin-left: 1rem;
  position: relative;
  object-fit: cover;
}

h6 {
  line-height: 1.4;
}

.business-manage-button {
  margin-bottom: 0.3rem;
}
</style>

<script>
import memberSince from "../model/MemberSince";
import Api from "../../Api";
import makeAdminModal from './MakeAdminModal';
import {formatAddress} from "../../util";
import CreateEditBusiness from "./CreateEditBusiness";
import ProfileImage from "../model/ProfileImage";
import EventBus from "../../util/event-bus";
import CurrencyNotification from "../model/CurrencyNotification";

export default {
  components: {
    CurrencyNotification,
    CreateEditBusiness,
    memberSince,
    makeAdminModal,
    ProfileImage
  },
  data: function () {
    return {
      error: [],
      businessData: {
        email: '',
        phoneNumber: '',
        id: 0,
        primaryAdministratorId: 0,
        name: "",
        description: "",
        address: {
          streetNumber: "",
          streetName: "",
          suburb: "",
          city: "",
          region: "",
          country: "",
          postcode: ""
        },
        administrators: [
          {
            id: 0,
            firstName: '',
            lastName: '',
            middleName: '',
            nickname: '',
            bio: '',
            email: '',
            dateOfBirth: '',
            phoneNumber: '',
            homeAddress: {
              streetNumber: "",
              streetName: "",
              suburb: "",
              city: "",
              region: "",
              country: "",
              postcode: ""
            },
            created: '',
            role: '',
            businessesAdministered: [
              "0"
            ]
          }
        ],
        businessType: "",
        created: "",
      },
      businessFound: true, // not smooth to switch the found or not find.
      loading: true,
      makeAdminAction: null,
      makeAdminError: "",
      oldCurrency: '',
      newCurrency: ''
    }
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.launchPage(businessId);
    EventBus.$on('updatedBusinessDetails', this.updateBusinessHandler);
    EventBus.$on('updatedBusinessImage', this.updateBusinessImageHandler);
    EventBus.$on("currencyChanged", this.showNotification);
  },

  methods: {
    /**
     * When the business's currency has changed, then it will show a notification
     */
    showNotification(oldCurrency, newCurrency) {
      this.oldCurrency = oldCurrency;
      this.newCurrency = newCurrency;
      this.$bvToast.show('business-currency-changed');
    },

    /**
     * set up the page
     **/
    launchPage(businessId) {
      this.loading = true;
      this.getBusinessInfo(businessId);
    },

    /**
     * When called changes page to the profile page based on the id of the user clicked
     */
    rowClickHandler: function (record) {
      this.$router.push({path: `/users/${record.id}`});
    },

    /**
     * Revoke admin from given userID by making api request
     * @param userId the id of the user to revoke admin from
     */
    removeAdminClickHandler: function (userId) {
      if (userId == null) {
        return;
      }

      const revokeAdminRequestData = {
        userId: userId
      }

      Api
          .revokeBusinessAdmin(this.businessData.id, revokeAdminRequestData)
          .then((response) => {
            this.getBusinessInfo(this.$route.params.id)
            this.$log.debug("Response from request to revoke admin: ", response);
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },
    /**
     * this is a get api which can take Specific business to display on the page
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param id
     **/
    getBusinessInfo: function (id) {
      Api
          .getBusiness(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.businessData = response.data;
            this.businessFound = true;
            this.loading = false;
          })
          .catch((error) => {
            this.$log.debug(error);
            this.businessFound = false;
            this.loading = false;
          })
    },

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    },

    /**
     * Check whether the user currently logged can revoke admin from an given administrator in
     * the administrators table or not, for the purposes
     * of displaying the revoke admin button.
     *
     * @param userId    The id of the user to check if admin can be revoked from
     * @returns {boolean} Whether or not the user can revoke admin
     */
    checkCanRevokeAdmin: function (userId) {
      if (userId === this.businessData.primaryAdministratorId) {
        //User is primary administrator
        return false;
      } else if (this.businessData.primaryAdministratorId === this.$currentUser.id) {
        //User is primary admin of business
        return true;
      } else return this.$currentUser.role === "globalApplicationAdmin"
          || this.$currentUser.role === "defaultGlobalApplicationAdmin";
    },

    /**
     * When clicking button Add Admin this modal will show
     */
    showMakeAdminModal: function () {
      this.makeAdminAction = this.makeAdminHandler;
      this.makeAdminError = "";
      this.$bvModal.show('admin-modal');
    },

    /**
     * Method of the API request to make user become admin
     *
     * @param userId ID of the user that is requested to make admin
     */
    makeAdminHandler: async function (userId) {
      const makeAdminRequestData = {
        userId: userId
      }
      await Api
          .makeBusinessAdmin(this.businessData.id, makeAdminRequestData)
          .then((response) => {
            this.$log.debug("Response from request to make admin: ", response);
            this.$bvModal.hide('admin-modal');
            this.getBusinessInfo(this.$route.params.id);
          })
          .catch((error) => {
            this.makeAdminError = this.getErrorMessageFromApiError(error);
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
     * Handles the EventBus emit for 'updatedBusinessDetails'.
     * This hides the edit business modal and refreshes the business's details.
     */
    updateBusinessHandler() {
      this.getBusinessInfo(this.businessData.id);
      this.$bvModal.hide('edit-business-profile');
    },

    /**
     * Handles the EventBus emit for 'updatedBusinessImage'.
     * This hides the edit business modal and refreshes the business's details.
     */
    updateBusinessImageHandler() {
      this.getBusinessInfo(this.businessData.id);
    }
  },

  computed: {
    /**
     * True if the user can should be able to see all business information (ie they are an admin or acting as this business)
     */
    isAdmin: function () {
      return this.$currentUser.role !== 'user' ||
          (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },

    /**
     * Returns true the user is an admin of this business, otherwise returns false
     */
    isAdminOfThisBusiness: function () {
      for (const business of this.$currentUser.businessesAdministered) {
        if (business.id === parseInt(this.$route.params.id)) {
          return true;
        }
      }
      return false;
    },

    /**
     * Formats the address using util function and appropriate privacy level.
     *
     * @return address formatted
     */
    getAddress: function () {
      return formatAddress(this.businessData.address, 1);
    },

    /**
     * set table parameter
     * @returns object
     */
    fields() {
      return [
        {
          key: 'firstName',
          sortable: true
        },
        {
          key: 'lastName',
          sortable: true
        },
        {
          key: 'actions',
          label: 'Actions'
        }
      ];
    }
  },
  watch: {
    /**
     * This watches for those routing changes, and will update the profile with the data of the business specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      const id = to.params.id;
      this.launchPage(id);
    },
  },

}
</script>

