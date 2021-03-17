<template>
  <div class="address-input">
    <b-form-textarea
      v-model="address"
      @input="onAddressChange"
      placeholder="Enter Address"
      v-bind:value="value"
      required
    />

    <b-list-group
      class="address-options-list"
      v-if="showAutocompleteDropdown && addressResults.length > 0"
    >

      <b-list-group-item button
         v-for="address in addressResults"
         v-bind:key="address"
         @click="selectAddressOption(address)"
      >
        {{ address }}
      </b-list-group-item>

      <b-list-group-item button
        class="address-option address-close"
        @click="showAutocompleteDropdown = false"
      >
        &#10060; Close Suggestions
      </b-list-group-item>

    </b-list-group>
  </div>
</template>

<style scoped>

.address-input {
  position: relative;
}

.address-close {
  text-align: center;
}

.address-options-list {
  width: 100%;
  position: absolute;
  z-index: 9999;
  max-height: 16em;
  overflow-y: scroll;
}

</style>

<script>

export default {
  name: "address-input",
  // This prop is needed for parent components to use v-model on this.
  // See https://vuejs.org/v2/guide/components.html#Using-v-model-on-Components
  props: ['value'],

  data: function () {
    return {
      address: "",
      addressResults: [],
      showAutocompleteDropdown: false,
      awaitingSearch: false,
    }
  },

  // Add click event listener to document root that closes the autocomplete dropdown
  // This is needed to close the dropdown when you click outside it
  mounted: function () {
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
    getStringFromPhotonAddress (address) {
      let addressString = "";
      let addressValid = false;

      if (address.country) {
        addressString = address.country;
        if (address.county || address.district || address.city) {
          addressString = (address.county || address.district || address.city) + ', ' + addressString;
          if (address.housenumber && address.street) {
            addressString = address.housenumber + ' ' + address.street + ', ' + addressString;
            addressValid = true;
          } else if (address.name) {
            addressString = address.name + ', ' + addressString;
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
      const url = 'https://photon.komoot.io/api/?q=' + input + '&limit=10';
      let returned = await (await fetch(url)).json();
      return returned.features;
    },

    /**
     * Author: Eric Song
     * Sets the address input field to the selected address
     * and closes the autocomplete drop down
     */
    selectAddressOption (address) {
      this.address = address;
      this.addressResults = [];

      // Emit input event as the address has changed. This is needed for parent components to use v-model on this component.
      // See https://vuejs.org/v2/guide/components.html#Using-v-model-on-Components
      this.$emit('input', address);
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
      this.$emit('input', this.address);
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
      if (this.address.trim().length <= 3) {
        this.addressResults = [];
        return;
      }
      this.showAutocompleteDropdown = true;
      const addressQueryString = this.address.replace(/\s/gm," ");  // Replace newlines and tabs with spaces, otherwise Photon gets confused
      let returnQuery = await this.getPhotonJsonResults(addressQueryString);
      this.addressResults = [];

      for (const result of returnQuery) {
        const addressString = this.getStringFromPhotonAddress(result.properties);

        if (addressString != null && !this.addressResults.includes(addressString)) {
          this.addressResults.push(addressString);
        }
      }
    }
  }
}
</script>