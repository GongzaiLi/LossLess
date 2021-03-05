<template>
  <div>
    <h2>{{ userDetail.nickname }}'s Profile Page</h2>
    <div>
      <p id="imgBox">image in here</p>
      <p>Member since: {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
    </div>

    <div id="userBox">
      <ol>
        <li>Name: {{ userDetail.firstName + " " + userDetail.middleName + userDetail.lastName }}</li>
        <li>Date Of Birth: {{ userDetail.dateOfBirth }}</li>
        <li>Email: {{ userDetail.email }}</li>
        <li>Phone Number: {{ userDetail.phoneNumber }}</li>
        <li>Home Address: {{ userDetail.homeAddress }}</li>
        <li>Describe: {{ userDetail.bio }}</li>
      </ol>
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

#userBox ol {
  list-style-type: none;
}

#userBox {
  border: 2px solid #a1a1a1;
  padding: 10px 40px;
  background: floralwhite;
  width: 300px;
  border-radius: 25px;
}

</style>