<template>
  <div class="register">

    <h2> Sign Up to Wasteless </h2>

    <p> Full Name * </p>

    <input v-model="fullName" placeholder="Full Name" autocomplete="off" size=30 />  //create variable for fields

    <p> it is {{ fullName }} </p>

    <p> Nickname </p>


    <input
        placeholder="Nick Name"
        autofocus
        autocomplete="off"
        size=30;

    />

    <p> Bio </p>
    <textarea
        placeholder="Enter your Bio"
        autofocus
        autocomplete="off"
        style="width:240px;height:80px;resize:none;font-family:Arial"
    />

    <p> Email * </p>
    <input
        placeholder="Email"
        autofocus
        autocomplete="off"
        size=30;


    />

    <p> Date of Birth * </p>
    <input
        placeholder="Date of Birth"
        autofocus
        autocomplete="off"
        size=30;
    />

    <p> Phone </p>
    <input
        placeholder="Phone"
        autofocus
        autocomplete="off"
        size=30;
    />

    <p> Home Address * </p>
     <textarea
     placeholder="Home Address"
        autofocus
        autocomplete="off"
        style="width:240px;height:80px;resize:none;font-family:Arial"
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