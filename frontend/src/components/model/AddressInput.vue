<!--
address input to be inserted into registration fields
Date: sprint_1
-->
<template>
  <div class="address-input">
    <b-card >

      <b-form-group>

        <b-row>
          <b-col><strong>Street Address *</strong></b-col>
        </b-row>

        <b-row>
          <b-col>
            <b-input-group>
              <b-form-input
                style="max-width: 6em"
                v-model="homeAddress.streetNumber"
                placeholder="Number"
                maxLength=50
                v-bind:value="value"
                required
              />
              <b-form-input
                v-model="homeAddress.streetName"
                @input="onAddressChange"
                placeholder="Street"
                maxLength=50
                v-bind:value="value"
                required
              />
            </b-input-group>
          </b-col>
        </b-row>
        <div
          class="address-options-wrapper"
          v-if="showAutocompleteDropdown && addressResults.length > 0"
        >
          <b-list-group class="address-options-list">
            <b-list-group-item button
                               v-for="address in addressResults"
                               v-bind:key="address"
                               @click="selectAddressOption(address)"
            >
              {{ address }}
            </b-list-group-item>
          </b-list-group>

          <b-list-group-item button
                             class="address-option address-close"
                             @click="showAutocompleteDropdown = false"
          >
            &#10060; Close Suggestions
          </b-list-group-item>
        </div>
      </b-form-group>


      <b-form-group>
        <b-row>
          <b-col><strong>Suburb</strong></b-col>
          <b-col><strong>City *</strong></b-col>
          <b-col><strong>Region</strong></b-col>
        </b-row>
        <b-row>
          <b-col>
            <b-form-input
              v-model="homeAddress.suburb"
              maxLength=50
              placeholder="Suburb"
              v-bind:value="value"
            />
          </b-col>
          <b-col>
            <b-form-input
              v-model="homeAddress.city"
              maxLength=50
              placeholder="City"
              v-bind:value="value"
              required
            />
          </b-col>
          <b-col>
            <b-form-input
              v-model="homeAddress.region"
              maxLength=50
              placeholder="Region"
              v-bind:value="value"
            />
          </b-col>
        </b-row>
      </b-form-group>

      <b-form-group>
        <b-row>
          <b-col><strong>Country *</strong></b-col>
          <b-col><strong>Postcode *</strong></b-col>
        </b-row>
        <b-row>
          <b-col md="6">
            <b-form-input
              v-model="homeAddress.country"
              maxLength=50
              placeholder="Country"
              v-bind:value="value"
              required
            />
          </b-col>
          <b-col md="4">
            <b-form-input
              v-model="homeAddress.postcode"
              maxLength=50
              placeholder="Postcode"
              v-bind:value="value"
              required
            />
          </b-col>
          <b-col md="2">
            <b-button variant="secondary" style="float:right" @click="clearAddress">Clear</b-button>
          </b-col>
        </b-row>
      </b-form-group>

    </b-card>


  </div>
</template>

<style scoped>

.address-input {
  position: relative;
}

.address-close {
  text-align: center;
}

.address-options-wrapper {
  width: calc(100% - 2 * 1.25rem);
  position: absolute;
  z-index: 9999;
  top: 78px;
}

.address-options-list {
  max-height: 12.7em;
  overflow-y: scroll;
}

</style>

<script>

export default {
  name: "address-input",
  // This prop is needed for parent components to use v-model on this.
  // See https://vuejs.org/v2/guide/components.html#Using-v-model-on-Components
  props:['value', 'address'],
  data: function () {
    return {
      addressResults: [],
      addressObject: [],
      showAutocompleteDropdown: false,
      awaitingSearch: false,
      homeAddress: {
        streetNumber: "",
        streetName: "",
        suburb: "",
        city: "",
        region: "",
        country: "",
        postcode: ""
      }
    }
  },

  // Add click event listener to document root that closes the autocomplete dropdown
  // This is needed to close the dropdown when you click outside it
  mounted: function () {
    this.homeAddress = this.address;
    document.addEventListener('click', () => {
      this.showAutocompleteDropdown = false;
    });
  },

  methods: {
    /**
     * Authors: Eric Song, Caleb Sim
     * Given the address properties of an object returned by Photon Komoot, attempts to convert
     * it into an address string. Returns null if the address is not valid.
     * Rules for a valid address are:
     * - Address must have a country field
     * - Address must have either a county, district, or city field
     * - Address must have either a house number and street, or a name field
     */
    getStringFromPhotonAddress(address) {
      let addressString = "";
      let addressValid = false;

      if (address.country) {
        addressString = address.country;
        if (address.county || address.city) {
          addressString = (address.county ||  address.city) + ', ' + addressString;
          if (address.district) addressString = address.district + ', ' + addressString;
          if (address.housenumber && address.street) {
            addressString = address.housenumber + ' ' + address.street + ', ' + addressString;
            addressValid = true;
          }
        }
      }
      if (addressValid) {
        return addressString;
      } else {
        return null;
      }
    },

    /**
     * Author: Phil Taylor
     * Sends a request to photon.komoot.io api with inputted string and returns an array of the raw
     * JSON results. A limit of 10 is placed on the number of addresses returned.
     */
    async getPhotonJsonResults(input) {
      const url = 'https://photon.komoot.io/api/?q=' + input;// + '&limit=10'
      let returned = await (await fetch(url)).json();
      return returned.features;
    },

    /**
     * Author: Eric Song
     * Sets the address input field to the selected address
     * and closes the autocomplete drop down
     */
    selectAddressOption(address) {
      const addressIndex = this.addressResults.indexOf(address);
      this.splitAddress(this.addressObject[addressIndex]);
      this.emptyAddressArray();
      // Emit input event as the address has changed. This is needed for parent components to use v-model on this component.
      // See https://vuejs.org/v2/guide/components.html#Using-v-model-on-Components
      this.$emit('input', this.homeAddress);
    },

    /**
     * take an address object and assign its values to the required fields for the home address object
     * this is because the home address object is to the api spec
     */
    splitAddress(addressObject) {
      this.homeAddress.streetNumber = addressObject.housenumber || '';
      this.homeAddress.streetName = addressObject.street || '';
      this.homeAddress.suburb = addressObject.district || '';
      this.homeAddress.city = addressObject.county || addressObject.city || ''; // order is to give better addresses to southern hemisphere locations
      this.homeAddress.region = addressObject.state || addressObject.region || "";
      this.homeAddress.country = addressObject.country;
      this.homeAddress.postcode = addressObject.postcode || "";
    },

    /**
     * Author: Nitish Singh
     * Send a query to Photon API after a debounced delay i.e. 500 ms.
     * This method is called whenever the address input is changed
     * by the user's typing (as it is bound to @input).
     * This is not and should not be called when an autocomplete option is
     * clicked, otherwise the suggestions will pop up again when it is clicked.
     */
    async onAddressChange() {
      // Emit input event as the address has changed. This is needed for parent components to use v-model on this component.
      // See https://vuejs.org/v2/guide/components.html#Using-v-model-on-Components
      this.$emit('input', this.homeAddress);

      if (!this.awaitingSearch) {
        setTimeout(() => {
          this.queryAddressAutocomplete();
          this.awaitingSearch = false;
        }, 500);
      }
      this.awaitingSearch = true;
    },


    /**
     * Authors: Phil Taylor, Gongzai Li, Eric Song
     * Queries the Photon Komoot API with the home address and puts the
     * results into addressFind.
     */
    async queryAddressAutocomplete() {
      if (this.homeAddress.streetName.trim().length <= 2) {
        this.emptyAddressArray();
        return;
      }
      this.showAutocompleteDropdown = true;

      const addressQueryString = this.homeAddress.streetNumber.replace(/\s/gm, " ") + " " +
        this.homeAddress.streetName.replace(/\s/gm, " ") + " " +
        this.homeAddress.suburb.replace(/\s/gm, " ") + " " +
        this.homeAddress.city.replace(/\s/gm, " ") + " " +
        this.homeAddress.region.replace(/\s/gm, " ") + " " +
        this.homeAddress.country.replace(/\s/gm, " ") + " " +
        this.homeAddress.postcode.replace(/\s/gm, " ");  // Replace newlines and tabs with spaces, otherwise Photon gets confused

      let returnQuery = await this.getPhotonJsonResults(addressQueryString);


      this.emptyAddressArray();

      for (const result of returnQuery) {
        const addressString = this.getStringFromPhotonAddress(result.properties);

        if (addressString != null && !this.addressResults.includes(addressString)) {
          this.addressResults.push(addressString);
          this.addressObject.push(result.properties);

        }
      }

    },

    /**
     * clean address array
     * clean address Object
     */
    emptyAddressArray() {
      this.addressResults = [];
      this.addressObject = [];
    },

    /**
     * clear all address to be empty string.
     */
    clearAddress() {
      Object.keys(this.homeAddress).forEach(key => this.homeAddress[key] = '');
    }
  }
}
</script>
