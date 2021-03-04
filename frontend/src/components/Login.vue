<template>
  <div>
    <h2>Login to Wasteless</h2>
    <p>Email</p>

    <input type="email" v-model="email" required
           size="30"
           autofocus
           autocomplete="off"
    />
    <p>Password</p>

    <input v-model="password" type="password" required
           size="30"
           autofocus
           autocomplete="off"
    /> <br>


    <div v-if="errors.length">
      <ul>
        <li v-for="error in errors" v-bind:key="error">{{ error }}</li>
      </ul>
    </div>

    <span style="padding-right:10px" align="left">

        <button @click="login" style="margin-top:10px">Login</button>
      </span>


    <p> Don't have an account?
      <span>
            <button @click="goToRegisterPage" style="margin-top:10px">Register</button>
        </span>
    </p>

  </div>

</template>

<script>

module.exports = {
  data: function () {
    return {
      errors: [],
      email: null,
      password: ""
    }
  },
  methods: {
    login: function () {

      if (this.email && this.password) {
        return true;
      }
      this.errors = [];

      if (!this.email) {
        this.errors.push("Please Fill In The Email Field");
      }
      if (!this.password) {
        this.errors.push("Please Fill In The Password Field");
      }

      console.log({
        email: this.email,
        password: this.password,
      });
    },
    goToRegisterPage: function () {
      console.log("Redirecting to Register Page");
    }
  }
}
</script>

