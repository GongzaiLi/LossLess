<template>
  <div>
    <h2>Login to Wasteless</h2>
    <p>Email</p>
    <input v-model="email"
        size="30"
        autofocus
        autocomplete="off"
    />
    <p>Password</p>
    <input v-model="password"
        size="30"
        autofocus
        autocomplete="off"
    /> <br>

    <span style="padding-right:10px" align="left">

        <button @click="login" style="margin-top:10px">Login</button>
      </span>

    <div v-if="loginFailed">
          <p style="color:red" >{{loginError}}</p>
    </div>

    <p> Don't have an account?
        <span>
            <button @click="goToRegisterPage" style="margin-top:10px">Register</button>
        </span>
    </p>

    <br><br><br>
    <span>Make logins successful</span> <input type="checkbox" v-model="makeLoginSucceed" checked>
  </div>

</template>

<script>
import api from "../Api";

export default {
  data: function() {
    return {
      makeLoginSucceed: true,
      loginFailed: false,
      loginError: "",
      email: "",
      password: "",
    }
  },
  methods: {
    login: function() {
      let loginData = {
        email: this.email,
        password: this.password
      };
      console.log(loginData);
      api
        .login(loginData)
        .then(() => {
          this.$log.debug("Logged in");
          // Go to profile page
        })
        .catch((error) => {
          if (this.makeLoginSucceed) {
            // Go to profile page
            this.loginFailed = false;
            return;
          }

          this.loginFailed = true;
          this.$log.debug(error);
          if ((error.response && error.response.status === 400) || !this.makeLoginSucceed) {
            this.loginError = "The given username or password is incorrect.";
          } else {
            this.loginError = error.message;
          }
        });
    },
    goToRegisterPage: function() {
      console.log("Redirecting to Register Page");
    }
  }
}
</script>
