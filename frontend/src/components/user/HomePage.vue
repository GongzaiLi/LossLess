<template>
  <b-card border-variant="secondary" header-border-variant="secondary">
    <h1>{{ userData.firstName + "'s Home Page" }}</h1>
    <router-link :to="{ name: 'user-profile', params: { id: this.$currentUser.id }}">
      <h4>My profile page</h4>
    </router-link>
  </b-card>
</template>

<script>
import api from "../../Api";

export default {

  data: function () {
    return {
      userData: {
        id: "",
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: "",
      }
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);
  },

  methods: {
    /**
     * this is a get api which can take Specific user to display on the page
     * The function id means user's id, if the serve find -
     * -the user's id will response the data and keep the data into this.userData
     */
    getUserInfo: function (id) {
      api
        .getUser(id)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.userData = response.data;
        })
        .catch((error) => {
          this.$log.debug(error);
        })
    }
  }
}
</script>

<style scoped>

</style>
