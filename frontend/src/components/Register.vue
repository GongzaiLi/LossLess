<!--
Page for users to input their information for registration
Author: Nitish Singh
Date: 3/3/2021
-->


<template>
  <div class="register">

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
        <input type="search" list="browsers" v-model="homeAddress" onkeypress="" required
          @input="onAddressChange"
          placeholder="Home Address"
          autofocus
          autocomplete="off"
          size=50;
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
  top: 100%;
  height: 20ex;
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

      //return this.$router.go(-1);
    },

    goToLoginPage() {
      console.log("Login Pressed. Redirecting to Login Page....")
      this.$router.push({path: '/login'})

    },

    //gets JSON from photon.komoot.io api with inputed string and returns an array of the results from the search
    async getJson(input) {
      const url = 'https://photon.komoot.io/api/?q=' + input + '&limit=10';
      let returned = await (await fetch(url)).json();
      return returned.features;
    },

    /**
    * Author: Eric Song
    * Sets the address input field to the selected address 
    * and clsoes the autocomplete drop down
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
      let returnQuery = await this.getJson(this.homeAddress);
      this.addressFind = [];

      let returnString = '';
      for (const addresses of returnQuery) {
        console.log(addresses);
        let name = addresses.properties.name || "";
        let houseNumber = addresses.properties.housenumber || "";
        let street = addresses.properties.street || "";
        let district = addresses.properties.district || "";
        let county = addresses.properties.county || "";
        let country = addresses.properties.country || "";

        returnString = houseNumber + ' ' + street + ' ' + district + ' ' + county + ' ' + country;
        if (houseNumber.length === 0) { // Only display name if there's no houes number
          returnString = name + ' ' + returnString;
        }

        if (returnString.trim().length > 1 && !this.addressFind.includes(returnString) ){
          this.addressFind.push(returnString);
        }
      }
      
      this.addressFind.sort(); //sort the the addressFind list
      //console.log(this.addressFind);
    }
  },

  computed: {
    showAutocomplete() {
      return this.addressFind.length > 0 && !this.autoCompleteClosed;
    }
  },
}
</script>