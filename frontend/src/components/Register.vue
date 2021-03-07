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
      <input v-model="firstName" required placeholder="First Name" autocomplete="off" size=30 />
      <span> //Test: {{ firstName }} </span>

      <p> Middle Name * </p>
      <input v-model="middleName" required placeholder="Middle Name" autocomplete="off" size=30 />
      <span> //Test: {{ middleName }} </span>


      <p> Last Name * </p>
      <input v-model="lastName" required placeholder="Last Name" autocomplete="off" size=30 />
      <span> //Test: {{ lastName }} </span>

      <p> Nickname </p>
      <input v-model="nickname" placeholder="Nick Name" autofocus autocomplete="off" size=30;/>

      <p> Bio </p>
      <textarea v-model="bio" placeholder="Enter your Bio" autofocus autocomplete="off" style="width:240px;height:80px;resize:none;font-family:Arial" />

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
      <p>{{addressSearch(homeAddress)}}</p>
      <input v-model="homeAddress"
             placeholder="HomeAddress"
             autofocus
             autocomplete="off"
             size=30;
      />

      <!--<input list="browsers" v-model="homeAddress" required
             placeholder="Home Address"
             autofocus
             autocomplete="on"
             style="width:240px;height:80px;resize:none;font-family:Arial"

      />

      <datalist id="browsers">
        <option v-for="index in this.addressArray" v-bind:key="index"  value="index">{{index}}</option>
      </datalist>
      -->




    </form>




    <div v-if="errors.length">
      <p style="color:red" v-for="error in errors" v-bind:key="error" id="error-txt">{{ error }}  </p>
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

  data: function() {
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
      addressArray: [],
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
      if (requiredFields.every(function(e) { return e;})) {
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
      console.log( "Login Pressed. Redirecting to Login Page....")
      this.$router.push({ path: '/login' })

    },

    //currently just gets json data from json function and prints it to the console. Is constantly updated while typing
    async addressSearch(input) {
      let returnQuery = await(this.getJson(input));
      //console.log(returnQuery);
      this.addressArray=[]
      let returnString =''

      for (let addresses of returnQuery) {
        let houseNumber = addresses['properties']['housenumber'];
        let street = addresses['properties']['street'];
        let district = addresses['properties']['district'];
        let county = addresses['properties']['county'];


        returnString = houseNumber + ' ' + street + ' ' + district + ' ' + county;
        returnString = returnString.replaceAll("undefined", "");
        // console.log(returnString);
        if (returnString.trim().length > 1) {
          this.addressArray.push(returnString);
        }


        //console.log(this.addressArray);
      }
      console.log(this.addressArray);
      return this.addressArray;


    },

    //gets JSON from photon.komoot.io api with inputed string and returns an array of the results from the search
    async getJson(input){
      const url = 'https://photon.komoot.io/api/?q='+input;
      let returned = await (await fetch(url)).json();
      return returned['features'];
    }



  }
}
</script>