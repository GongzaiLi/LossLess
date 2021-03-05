<template>
  <div id="userBox">
    <h2>{{ userDetail.nickname }}'s Profile Page</h2>
    <div>
      <p id="member-since">Member since: {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
    </div>
    <hr>
    <div>
      <p><b>Name:</b> {{ userDetail.firstName + " " + userDetail.middleName + " " + userDetail.lastName }}</p>
      <p><b>Date Of Birth:</b> {{ userDetail.dateOfBirth }}</p>
      <p><b>Email:</b> {{ userDetail.email }}</p>
      <p><b>Phone Number:</b> {{ userDetail.phoneNumber }}</p>
      <p><b>Home Address:</b> {{ userDetail.homeAddress }}</p>
      <p><b>Bio:</b> {{ userDetail.bio }}</p>
    </div>

    <hr>

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
        firstName: "Firstname",
        lastName: "Lastname",
        middleName: "Middlename",
        nickname: "Someone",
        bio: "Likes long walks on the beach",
        email: "johnsmith99@gmail.com",
        dateOfBirth: "1999-04-27",
        phoneNumber: "+64 3 555 0129",
        homeAddress: "4 Rountree Street, Upper Riccarton",

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

#userBox {
  border: 2px solid #a1a1a1;
  padding: 10px 40px;
  background: floralwhite;
  width: 400px;
  border-radius: 10px;
}

#member-since {
  margin-top: -10px;
}

</style>