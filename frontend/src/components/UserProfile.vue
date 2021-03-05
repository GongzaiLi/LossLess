<template>
  <div>
    <h2>Profile Page</h2>
    <h3>Some image in here</h3>
    <p>Member since: {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
    <div>
      <ul>
        <li>Name: {{ userDetail.firstName + " " + userDetail.middleName + userDetail.lastName }}</li>
        <li>Nick Name: {{ userDetail.nickname }}</li>
        <li>Date Of Birth: {{ userDetail.dateOfBirth }}</li>
        <li>Email: {{ userDetail.email }}</li>
        <li>Phone Number: {{ userDetail.phoneNumber }}</li>
        <li>Home Address: {{ userDetail.homeAddress }}</li>
        <li>Describe: {{ userDetail.bio }}</li>
      </ul>
    </div>


    <button @click="loginOut" style="margin-top:10px">Login out</button>


  </div>

</template>

<script>
export default {
  data: function () {
    return {
      dateR: {
        year: 0,
        month: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
        day: 0
      },
      registrationTime: 0,
      userDetail: {
        firstName: "",
        lastName: "",
        middleName: " ",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: "",

      }

    }
  },
  methods: {
    loginOut: function () {

    }
  }
}
</script>

<style scoped>

</style>