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


    <p> Don't have an account?
        <span>
            <button @click="goToRegisterPage" style="margin-top:10px">Register</button>
        </span>
    </p>

  </div>

</template>

<script>
module.exports = {
  data: function() {
    return {
      email: "",
      password: "",
    }
  },
  methods: {
    login: function() {
      console.log({
        email: this.email,
        password: this.password
      });
    },
    goToRegisterPage: function() {
    console.log("Redirecting to Register Page");
    }
  }
}
</script>
