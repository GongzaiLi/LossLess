<!--
Page for users to input their information for registration
Author: Nitish Singh
Date: 3/3/2021
-->


<template>
  <div class="register" style="margin: 20px ">

    <h2> Sign Up to Wasteless </h2>

    <form action="" :class="errors.length > 0 ? 'errors' : false">
      <p> First Name * </p>
      <input v-model="firstName" required placeholder="First Name" autocomplete="off" size=30/>
      <span> //Test: {{ firstName }} </span>

      <p> Middle Name * </p>
      <input v-model="middleName" required placeholder="Middle Name" autocomplete="off" size=30/>
      <span> //Test: {{ middleName }} </span>


      <p> Last Name * </p>
      <input v-model="lastName" required placeholder="Last Name" autocomplete="off" size=30/>
      <span> //Test: {{ lastName }} </span>

      <p> Nickname </p>
      <input v-model="nickname" placeholder="Nick Name" autofocus autocomplete="off" size=30;/>

      <p> Bio </p>
      <textarea v-model="bio" placeholder="Enter your Bio" autofocus autocomplete="off"
                style="width:240px;height:80px;resize:none;font-family:Arial"/>

      <p> Email * </p>
      <input required v-model="email" placeholder="Email" autofocus autocomplete="off" size=30;/>

      <p> Password * </p>
      <input type="password" required v-model="password" placeholder="Password" autofocus autocomplete="off" size=30;/>

      <p> Date of Birth * </p>
      <input type="date" v-model="dateOfBirth" required
             placeholder="Date of Birth"
             autofocus
             autocomplete="off"
             size=30;
      />

      <p> Phone </p>
      <input type="number" v-model="phoneNumber"
             placeholder="Phone"
             autofocus
             autocomplete="off"
             size=30;
      />

      <p> Home Address * </p>

      <div class="address-input">
        <textarea type="search" v-model="homeAddress" onkeypress=""
          @input="onAddressChange"
          placeholder="Enter Address"
          autofocus
          autocomplete="off"
          cols=30;
          style="font-family:Arial"
        />

        <div class="address-options-list"
          v-if="addressFind.length > 0">
          <div class="address-option"
            v-for="address in addressFind"
            v-bind:key="address"
            @click="selectAddressOption(address)"
            >{{ address }}</div>
          <div class="address-option address-close"
            @click="addressFind=[]">&#10060; Close Suggestions</div>
        </div>
      </div>

    </form>

    <div v-if="errors.length">
      <p style="color:red" v-for="error in errors" v-bind:key="error" id="error-txt">{{ error }} </p>
    </div>

    <p>
      <button v-on:click="register" style="margin-top:10px" id="register-btn">Register</button>
    </p>

    <p> Already have an account?
      <span>
       <button v-on:click="goToLoginPage" style="margin-top:10px">Login</button>
    </span>
    </p>
  </div>

</template>

<style>
form.errors :invalid {
  outline: 2px solid red;
}

.address-input {
  position: relative;
  display: inline-block;
}

.address-option {
  border-top: 2px solid gray;
}

.address-option:hover {
  background-color: lightgray;
}

.address-close {
  text-align: center;
}

.address-options-list {
  border-left: 2px solid gray;
  border-right: 2px solid gray;
  border-bottom: 2px solid gray;
  background-color: #FCFCFC;
  width: 100%;
  position: absolute;
  z-index: 9999;
  max-height: 20ex;
  overflow-y: scroll;
}
</style>

<script>

module.exports = {

  data: function () {
    return {
      errors: [],
      "firstName": "",
      "lastName": "",
      "middleName": "",
      "nickname": "",
      "bio": "",
      "email": "",
      "dateOfBirth": "",
      "phoneNumber": "",
      "homeAddress": "",
      "password": "",
      addressSearchQuery: "",
      addressFind: [],
    }
  },
  methods: {

    /* Author: Caleb Sim
    Register function first has list of all mandatory fields, checks email contains @
    Checks if any required field is empty if so print message else print to console Api format
    */
    register() {
      const requiredFields = [this.firstName, this.lastName, this.middleName, this.email, this.password, this.dateOfBirth,
        this.homeAddress];
      this.errors = [];
      if (!this.email.includes("@")) {
        this.errors.push("Email address is invalid, please make sure it contains an @ sign");
      }
      if (requiredFields.every(function (e) {
        return e;
      })) {
        console.log({
          firstName: this.firstName,
          lastName: this.lastName,
          middleName: this.middleName,
          nickname: this.nickname,
          bio: this.bio,
          email: this.email,
          dateOfBirth: this.dateOfBirth,
          phoneNumber: this.phoneNumber,
          homeAddress: this.homeAddress,
          password: this.password
        })
      } else {
        this.errors.push("Highlighted fields are Mandatory, please fill them in");
      }

    },

    /* Author: Nitish Singh
    Redirects to login page when login button is pressed.
    */
    goToLoginPage() {
      console.log("Login Pressed. Redirecting to Login Page....")
      this.$router.push({path: '/login'})

    },

    //gets JSON from photon.komoot.io api with inputted string and returns an array of the results from the search
    async getJson(input) {
        const url = 'https://photon.komoot.io/api/?q=' + input + '&limit=10';
        let returned = await (await fetch(url)).json();
        return returned.features;

    },

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
     * Author: Eric Song
     * Sets the address input field to the selected address
     * and closes the autocomplete drop down
     */
    selectAddressOption (address) {
      this.homeAddress = address;
      this.addressFind = [];
    },

    /**
     * Authors: Phil Taylor, Gongzai Li, Eric Song
     * Queries the Photon Komoot API with the home address and puts the
     * results into addressFind.
     * This method is called whenever the address input is changed
     * by the user's typing (as it is bound to @input).
     * This is not and should not be called when an autocomplete option is
     * clicked, otherwise the suggestions will pop up again when it is clicked.
     */
    async onAddressChange() {
      const addressQueryString = this.homeAddress.replace(/\s/gm," ");  // Replace newlines and tabs with spaces, otherwise Photon gets confused
      if (this.homeAddress.length > 3) {
        let returnQuery = await this.getJson(addressQueryString);

      this.addressFind = [];

      for (const result of returnQuery) {
        const addressString = this.getStringFromPhotonAddress(result.properties);

        if (addressString != null && !this.addressFind.includes(addressString)) {
          this.addressFind.push(addressString);
        }
      }
      }
    }
  },
}
</script>