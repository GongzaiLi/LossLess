<template>
  <div>
    <h1 align="center"><b-icon-shop/> Create Card </h1>
    <br>

    <b-input-group-text>
      <b-container>
        <h6 align="center"> <strong> Card Info: </strong></h6>
        <label> Seller Name: {{sellerData.fullName}} </label>
        <br>
        <label> Seller Location: {{sellerData.location}} </label>
        <br>
        <label> Creation Date: {{createCardForm.dateCreated}} </label>
      </b-container>
    </b-input-group-text>
    <br>

    <b-form>
    <b-input-group>
      <h6><strong>Section*:</strong></h6>
    </b-input-group>
    <b-input-group class="mb-1">
      <b-form-input type="text" maxlength="50" v-model="createCardForm.section" required/>
    </b-input-group>

    <b-input-group>
      <h6><strong>Title*:</strong></h6>
    </b-input-group>
    <b-input-group class="mb-1">
      <b-form-input type="text" maxlength="50" v-model="createCardForm.title" required/>
    </b-input-group>

    <b-input-group>
      <h6><strong>Description:</strong></h6>
    </b-input-group>
    <b-input-group class="mb-1">
      <b-form-input type="text" maxlength="50" v-model="createCardForm.description" required/>
    </b-input-group>

    <b-input-group>
      <h6><strong>Keywords*:</strong></h6>
    </b-input-group>
    <b-input-group class="mb-1">
      <b-form-input type="text" maxlength="50" v-model="createCardForm.keywords" required/>
    </b-input-group>

      <div>
        <b-button style="float: right" variant="primary" type="submit" @click="createAction"> Create </b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction"> Cancel </b-button>
      </div>

    </b-form>
  </div>

</template>

<script>
import api from "@/Api";

export default {
  name: "CreateCard",
  props: ['createAction', 'cancelAction'],
  data() {
    return {
      sellerData: {
        fullName: '',
        location: '',
      },
      createCardForm: {
        creatorId: '',
        section: '',
        title: '',
        description: "",
        keywords: [],
        dateCreated: ''
      }
    }
  },
  mounted() {
    const userId = localStorage.getItem("currentUserId")
    this.setAutofillData(userId);

  },
  methods: {
    /**
     * Gets the user info by making an api request and updates the variables \
     * with the info.
     * @param id  The ID of the currently logged in user.
     */
    setAutofillData(id) {
      this.createCardForm.creatorId = id;
      const currentDate = new Date();
      this.createCardForm.dateCreated = currentDate.getDate() + "/"
          + (currentDate.getMonth()+1)  + "/"
          + currentDate.getFullYear() + " @ "
          + currentDate.getHours() + ":"
          + currentDate.getMinutes() + ":"
          + currentDate.getSeconds();
      api
          .getUser(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.userData = response.data;
            this.sellerData.fullName = response.data.firstName + " " + response.data.lastName;
            if (response.data.homeAddress.suburb) {
              this.sellerData.location = response.data.homeAddress.suburb + ", ";
            }
            if (response.data.homeAddress.city) {
              this.sellerData.location += response.data.homeAddress.city;
            }
            // console.log(response.data);
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