<template>
  <div id="userBox">
    <h2>{{ userDetail.nickname }}'s Profile Page</h2>
    <div>
      <p id="imgBox">image in here</p>
      <p>Member since: {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
    </div>

    <div>
      <p>Name: {{ userDetail.firstName + " " + userDetail.middleName + userDetail.lastName }}</p>
      <p>Date Of Birth: {{ userDetail.dateOfBirth }}</p>
      <p>Email: {{ userDetail.email }}</p>
      <p>Phone Number: {{ userDetail.phoneNumber }}</p>
      <p>Home Address: {{ userDetail.homeAddress }}</p>
      <p>Describe: {{ userDetail.bio }}</p>
    </div>


    <button id="buttonLog" @click="logOut" style="margin-top:10px">Log out</button>


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
        nickname: "Someone",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: "",

      }

    }
  },
  methods: {
    logOut: function () {

    }
  }
}
</script>

<style scoped>
#imgBox {
  border-style: double;
  width: 200px;
  height: 200px;
}

#userBox {
  border: 2px solid #a1a1a1;
  padding: 10px 40px;
  background: floralwhite;
  width: 300px;
  border-radius: 10px;
}

</style>