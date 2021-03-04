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
       <textarea v-model="homeAddress" required
       placeholder="Home Address"
          autofocus
          autocomplete="off"
          style="width:240px;height:80px;resize:none;font-family:Arial"
        />
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
        "password": ""
    }
  },
methods: {
    register() {
    const requiredFields = [this.firstName, this.lastName, this.middleName, this.email, this.password, this.dateOfBirth,
    this.homeAddress];
    this.errors = [];
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
      if (!this.email.includes("@")) {
        this.errors.push("Email address is invalid, please make sure it contains an @ sign");
      }
      this.errors.push("One or more mandatory fields are empty!");
    }

    //return this.$router.go(-1);
    },
    goToLoginPage() {
        console.log( "Login Pressed. Redirecting to Login Page....")

    }
}
}
</script>