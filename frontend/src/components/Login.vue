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
    <button v-on:click="login"
      style="margin-top:10px">Login</button>
    <p>Don't have an account? <a href="\register">Register here</a></p>
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
    }
  }
}
</script>