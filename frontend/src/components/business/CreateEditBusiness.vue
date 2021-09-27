<!--
Page for users to input business information for registration of a Business
Authors: Nitish Singh, Arish Abalos
Date: 26/3/2021
-->


<template>
  <div>
    <b-card class="shadow" v-if="canCreateBusiness || isEditBusiness">
      <b-form
          @submit="submit"
      >
        <h1 v-if="!isEditBusiness"> Create a Business </h1>
        <h5 v-else> Business Details</h5>

        <b-form-group>
          <strong>Business name *</strong>
          <b-form-input v-model="businessData.name" required placeholder="Business Name" maxlength="50" autofocus></b-form-input>
        </b-form-group>

        <b-form-group>
          <strong>Description</strong>
          <b-form-textarea v-model="businessData.description" placeholder="Description" maxlength="250"></b-form-textarea>
        </b-form-group>

        <b-form-group>
          <strong>Business Address *</strong>
          <address-input v-model="businessData.address" :address="businessData.address"/>
        </b-form-group>

        <b-form-group>
          <strong>Business Type *
          </strong>
          <div class="input-group mb-xl-5">

            <b-select v-model="businessData.businessType" required>
              <option disabled value=""> Choose ...</option>
              <option> Accommodation and Food Services</option>
              <option> Retail Trade</option>
              <option> Charitable organisation</option>
              <option> Non-profit organisation</option>

            </b-select>

          </div>
        </b-form-group>

        <b-row>
          <b-col cols="auto" class="mr-auto p-3">
            <b-button v-show="isEditBusiness" id="cancel-button" @click="$bvModal.hide('edit-business-profile')" >Cancel</b-button>
          </b-col>
          <b-col cols="auto" class="p-3">
            <b-button variant="primary" type="submit" id="submit-button">{{(isEditBusiness)?'Confirm':'Create'}}</b-button>
          </b-col>
        </b-row>
      </b-form>

      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">
          {{ error }}
        </b-alert>
      </div>
    </b-card>

    <b-card id="create-business-locked-card" v-else-if="this.$currentUser.currentlyActingAs && !isEditBusiness">
      <b-card-title>
        <b-icon-lock/>
        Can't create business while acting as a business
      </b-card-title>
      <h6><strong>You can't create a business while acting as a business. You must be acting as a user</strong>
        <br><br>To do so, click your profile picture on top-right of the screen. Then, select your name
        ('{{ $currentUser.firstName }}') from the drop-down menu.</h6>
    </b-card>

    <b-card v-else>
      <b-card-title>
        Not old enough to create a business
        <b-iconstack font-scale="3" style="float:right">
          <b-icon
              stacked
              icon="calendar2-x"
              variant="danger"
              scale="0.75"
          ></b-icon>
        </b-iconstack>

        <b-iconstack font-scale="3" style="float:right">
          <b-icon
              stacked
              icon="building"
              variant="info"
              scale="0.75"
          ></b-icon>
          <b-icon
              stacked
              icon="x-circle"
              variant="danger"
          ></b-icon>
        </b-iconstack>
      </b-card-title>
      <hr>

      <h4>You must be at least <strong>16 years old</strong> to create a business.</h4>
    </b-card>
  </div>
</template>

<style>
#cancel-button {
  align-self: end;
  margin-top: 0.7rem;
}

#submit-button {
  margin-top: 0.7rem;
}
</style>

<script>
import api from "../../Api";
import AddressInput from "../model/AddressInput";
import {getMonthsAndYearsBetween} from '../../util';
import EventBus from "../../util/event-bus";

const MIN_AGE_TO_CREATE_BUSINESS = 16;

export default {
  components: {
    AddressInput
  },

  props: {
    isEditBusiness: {
      type: Boolean,
      default: false,
    },
    businessDetails: {
      type: Object
    }
  },

  data: function () {
    return {
      businessData: {
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
        businessType: "",
      },
      errors: [],
    }
  },

  beforeMount() {
    this.$log.debug(this.businessDetails)
    if (this.isEditBusiness) {
      this.businessData = JSON.parse(JSON.stringify(this.businessDetails));
    }
  },

  methods: {

    /**
     * Handles submit event if isEditBusiness then updateBusiness is called else createBusiness is called
     */
    async submit(event) {
      event.preventDefault();
      if (this.isEditBusiness) {
        await this.updateBusiness()
      } else {
        await this.createBusiness()
      }
    },

    /**
     * Makes a request to the API to register a business with the form input.
     * Then, will redirect to the business page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    async createBusiness() {
      try {
        const businessResponse = (await api.postBusiness(this.businessData)).data;
        this.$currentUser = (await api.getUser(this.$currentUser.id)).data;
        await this.$router.push({path: `/businesses/${businessResponse.businessId}`});
      } catch (error) {
        this.pushErrors(error);
      }
    },

    /**
     * Makes a put API request to update a business's details taken from the user inputs
     * from the modal.
     */
    async updateBusiness() {
      await api.modifyBusiness(this.businessData, this.businessData.id)
          .then(() => {
            EventBus.$emit("updatedBusiness", this.businessData.id) })
          .catch((error) => {
            this.errors = [];
            this.$log.debug(error);
            if (error.response) {
              this.errors.push(`Updating business failed: ${error.response.data.message}`);
            } else {
              this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
            }
          });
    },

    /**
     * Pushes errors to errors list to be displayed as response on the screen,
     * if there are any.
     */
    pushErrors(error) {
      this.errors.push(error.message);
    }
  },
  computed: {
    canCreateBusiness: function () {
      const monthsAndYearsBetween = getMonthsAndYearsBetween(new Date(this.$currentUser.dateOfBirth), Date.now());
      return monthsAndYearsBetween.years >= MIN_AGE_TO_CREATE_BUSINESS && !this.$currentUser.currentlyActingAs;
    }
  }
}
</script>