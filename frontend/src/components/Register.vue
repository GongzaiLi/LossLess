<template>
  <div class="register">

    <h2> Sign Up to Wasteless </h2>

    <p> First Name * </p>
    <input v-model="firstName" placeholder="First Name" autocomplete="off" size=30 />
    <span> //Test: {{ firstName }} </span>

    <p> Middle Name * </p>
    <input v-model="middleName" placeholder="Middle Name" autocomplete="off" size=30 />
     <span> //Test: {{ middleName }} </span>


    <p> Last Name * </p>
    <input v-model="lastName" placeholder="Last Name" autocomplete="off" size=30 />
    <span> //Test: {{ lastName }} </span>

    <p> Nickname </p>
    <input v-model="nickname" placeholder="Nick Name" autofocus autocomplete="off" size=30;/>

    <p> Bio </p>
    <textarea v-model="bio" placeholder="Enter your Bio" autofocus autocomplete="off" style="width:240px;height:80px;resize:none;font-family:Arial" />

    <p> Email * </p>
    <input v-model="email" placeholder="Email" autofocus autocomplete="off" size=30;/>

     <p> Password * </p>
      <input v-model="password" placeholder="Password" autofocus autocomplete="off" size=30;/>

    <p> Date of Birth * </p>
    <input v-model="dateOfBirth"
        placeholder="Date of Birth"
        autofocus
        autocomplete="off"
        size=30;
    />

    <p> Phone </p>
    <input v-model="phoneNumber"
        placeholder="Phone"
        autofocus
        autocomplete="off"
        size=30;
    />

    <p> Home Address * </p>
     <textarea v-model="homeAddress"
     placeholder="Home Address"
        autofocus
        autocomplete="off"
        style="width:240px;height:80px;resize:none;font-family:Arial"
      />
     <p>
        <button @click="register" style="margin-top:10px">Register</button>
    </p>

    <p> Already have an account?
    <span>
       <button @click="goToLoginPage" style="margin-top:10px">Login</button>
    </span>
    </p>


  </div>

</template>

<script>
module.exports = {

data: function() {
    return {
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

    //return this.$router.go(-1);
    },
    goToLoginPage() {
        console.log( "Login Pressed. Redirecting to Login Page....")

    }
}
}
</script>