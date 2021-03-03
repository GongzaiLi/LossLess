<template>
  <div class="register">

    <h2>Sign Up to Wasteless</h2>

    <p>Full Name * </p>

    <input
        autofocus
        autocomplete="off"
    />

    <p> Nickname </p>


    <input
        autofocus
        autocomplete="off"
    />

    <p> Bio </p>
    <textarea
        autofocus
        autocomplete="off"
        style="width:170px;height:80px;resize:none;font-family:Arial"
    />

    <p> Email * </p>
    <input
        autofocus
        autocomplete="off"
    />

    <p> Date of Birth * </p>
    <input
        autofocus
        autocomplete="off"
    />

    <p> Phone </p>
    <input
        autofocus
        autocomplete="off"
    />

    <p> Home Address * </p>
     <textarea
             autofocus
             autocomplete="off"
             style="width:170px;height:80px;resize:none;font-family:Arial"
      />
     <p>
        <button style="margin-top:10px">Register</button>
    </p>

    <p> Already have an account?
    <span>
       <button @click="gotToLoginPage" style="margin-top:10px">Login</button>
    </span>
    </p>


  </div>

</template>

<script>
export default {
methods: {
    goToLoginPage() {
    return this.$router.go(-1);
    }
}
}
</script>