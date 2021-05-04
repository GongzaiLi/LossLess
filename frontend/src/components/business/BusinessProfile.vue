<!--
Individual Business profile Page. Currently displays all business data
Author: Gongzai Li
Date: 29/03/2021
-->
<template>
  <div v-show="!loading">
    <b-card border-variant="secondary" header-border-variant="secondary"
            class="profile-card shadow" no-body
            v-if="businessFound"
    >

      <template #header>
        <b-row>
          <b-col>
            <h4 class="mb-1">{{ businessData.name }}</h4>
            Registered on: <member-since :date="businessData.created"/>
          </b-col>
        </b-row>
      </template>

      <b-list-group border-variant="secondary">
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
              <b-col cols="4"><b>Type:</b></b-col>
              <b-col>{{ businessData.businessType }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-geo-alt></b-icon-geo-alt> <!-- geo-alt-->
              </b-col>
              <b-col cols="4"><b>Address:</b></b-col>
              <b-col>{{ getAddress }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row v-if="businessData.email">
              <b-col cols="0">
                <b-icon-envelope></b-icon-envelope>
              </b-col>
              <b-col cols="4"><b>Email:</b></b-col>
              <b-col>{{ businessData.email }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row v-if="businessData.phoneNumber">
              <b-col cols="0">
                <b-icon-phone-vibrate></b-icon-phone-vibrate>
              </b-col>
              <b-col cols="4"><b>Phone Number:</b></b-col>
              <b-col>{{ businessData.phoneNumber }}</b-col>
            </b-row>
          </h6>
        </b-container>
      </b-card-body>

      <b-list-group border-variant="secondary" >
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            <h4 class="mb-1">Administrators</h4>
          </b-card-text>
          <b-row>
            <b-col cols="12"><!--responsive sticky-header="500px"-->


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
                <template #cell(actions)="row" >
                  <div v-if="checkCanRevokeAdmin(row.item.id)">
                    <b-button size="sm" @click="removeAdminClickHandler(row.item.id)"  class="mr-1">
                      Revoke Admin
                    </b-button>
                  </div>
                </template>
              </b-table>


            </b-col>
          </b-row>
          <br>
          <h6 class="font-weight-bold">Assign a New Administrator:</h6>
          <b-button @click="showMakeAdminModal()">Add Amin</b-button>
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

    <b-modal id="admin-modal" hide-header hide-footer size="xl">
      <make-admin-modal :make-admin-action="this.makeAdminAction"/>
      <b-alert :show="makeAdminError.length > 0 ? 120 : 0" variant="danger">{{ makeAdminError }}</b-alert>
    </b-modal>
  </div>


</template>

<style scoped>
.profile-card {
  max-width: 50rem;
  margin-left: auto;
  margin-right: auto;
}

h6 {
  line-height: 1.4;
}
</style>

<script>
import memberSince from "../model/MemberSince";
import api from "../../Api";
import makeAdminModal from './MakeAdminModal';



export default {
  components: {
    memberSince,
    makeAdminModal
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
            nickName: '',
            bio: '',
            email: '',
            dateOfBirth: '',
            phoneNumber: '',
            homeAddress: {
              streetNumber: "",
              streetName: "",
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
      makeAdminAction: () => {},
      makeAdminError: "",
    }
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.launchPage(businessId);
  },

  methods: {

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

      api
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
      api
        .getBusiness(id)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.setResponseData(response.data);
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
     * set the response data to businessData
     * @param data
     */
    //todo may need split the data from response.
    setResponseData: function (data) {
      this.businessData = data;
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
      } else if (this.$currentUser.role === "globalApplicationAdmin"
          || this.$currentUser.role === "defaultGlobalApplicationAdmin") {
        //User is DGAA or GAA
        return true;
      } else {
        return false;
      }
    },

    /**
     * When clicking button Add Admin this modal will show
     */
    showMakeAdminModal: function () {
      this.makeAdminAction = this.makeAdminHandler;
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
      await api
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
        return error.response.data;
      } else if ((error.response && error.response.status === 403)) {
        return "Forbidden. You are not an authorized administrator";
      } else if (error.request) {  // The request was made but no response was received, see https://github.com/axios/axios#handling-errors
        return "No Internet Connectivity";
      } else {
        return "Server error";
      }
    },
  },

  computed: {

    /**
     * the street number, street name, city, region,
     * country and postcode join with space between the echo to a string
     * @return {string}
     */
    getAddress: function () {
      return Object.values(this.businessData.address).join(' ');
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
  }

}
</script>

