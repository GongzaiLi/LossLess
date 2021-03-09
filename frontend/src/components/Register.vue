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
      <input type="search" list="browsers" v-model="homeAddress" onkeypress="" required
             placeholder="Search Address"
             autofocus
             autocomplete="off"
             size=30;
             style="font-family:Arial"
      />
     <button v-on:click="clearAddress" style="margin-top:10px" >Clear</button>
      <p>
        <p>
      FIX 1: Big Box with line breaks for better view.
    </p>
      <p>
      <textarea cols="33" type="search" list="browsers" rows="4" v-model="homeAddress " onkeypress="" required
                placeholder="Manually Type Home Address"
                autofocus
                autocomplete="off"
                style="font-family:Arial"
      />
      </p>

      <br> <br> <br>
      <p>
        FIX 2: Have Address lines instead of labels for each type of address field to avoid showing incorrect data.
      </p>

      <p> House
      <span>
        <input type="search" list="browsers" v-model="house" onkeypress="" required
                    placeholder="house"
                    autofocus
                    autocomplete="off"
                    size=30;
                    style="font-family:Arial"
      />
      </span>
        </p>
      <p>
        <span>
       Street
        <input type="search" list="browsers" v-model="street" onkeypress="" required
               placeholder="Street"
               autofocus
               autocomplete="off"
               size=30;
               style="font-family:Arial"
        />
      </span>
      </p>

      <p>
         <span>
       District
        <input type="search" list="browsers" v-model="district" onkeypress="" required
               placeholder="District"
               autofocus
               autocomplete="off"
               size=30;
               style="font-family:Arial"
        />
      </span>

      </p>
      <p>
         <span>
       County
        <input type="search" list="browsers" v-model="county" onkeypress="" required
               placeholder="County"
               autofocus
               autocomplete="off"
               size=30;
               style="font-family:Arial"
        />
      </span>
      </p>

      <p>
         <span>
       Country
        <input type="search" list="browsers" v-model="country" onkeypress="" required
               placeholder="Country"
               autofocus
               autocomplete="off"
               size=30;
               style="font-family:Arial"
        />
      </span>
      </p>



      <datalist id="browsers">
        <option v-for="address in addressFind" v-bind:key="address" select>{{ address }}</option>

      </datalist>

      <!--
      <input type="search" v-model="homeAddress" onkeypress="" required
             placeholder="Home Address"
             autofocus
             autocomplete="off"
             style="width:240px;height:80px;resize:none;font-family:Arial"/>
      <div v-show="addressFind.length" v-for="address in addressFind" v-bind:key="address">{{address}}</div>

      <p>{{ addressFind }}</p> -->


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
    /* Author: Nitish Singh
    Redirects to login page when login button is pressed.
    */

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

    /* Author: Nitish Singh
    For FIX 1: Autofills the address into separate fields for better view, especially on mobile.
    */
    autofillAddress(numberOrName, street, district, county, country) {

      this.house = numberOrName;
      this.street = street;
      this.district = district;
      this.county = county;
      this.country = country;

    },
    /* Author: Nitish Singh
    Clears the address input box so user can start over.
    */
    clearAddress() {
      console.log("clear");
      this.homeAddress = "";
      this.autofillAddress("", "", "", "", "");
    }
  },

  watch: {
    //currently just gets json data from json function and prints it to the console. Is constantly updated while typing
    async homeAddress(input) {
      let returnQuery = await this.getJson(input);
      this.addressFind = [];

      let returnString = '';
      for (const addresses of returnQuery) {
        let name = addresses.properties.name || "";
        let houseNumber = addresses.properties.housenumber || "";
        let street = addresses.properties.street || "";
        let district = addresses.properties.district || "";
        let county = addresses.properties.county || "";
        let country = addresses.properties.country || "";



          returnString = houseNumber + ' ' + street + ' , ' + district + ' , ' + county + ' , ' + country;
          if (houseNumber.length === 0) { // Only display name if there's no house number
            returnString = name + ' ' + returnString;
            this.autofillAddress(name, street, district, county, country);
          }

          if (returnString.trim().length > 1 && !this.addressFind.includes(returnString)) {
            this.addressFind.push(returnString);
          }
        this.autofillAddress(houseNumber, name, street, district, county, country);

      }


      //console.log(this.addressFind);
    }
  }
}
</script>