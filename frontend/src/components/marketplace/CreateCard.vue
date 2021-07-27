<template>
  <div>
    <h1 style="text-align:center">
      <b-icon-shop/>
      Create Card
    </h1>
    <br>

    <b-input-group-text>
      <b-container>
        <h6 style="text-align:center"><strong> Card Info: </strong></h6>
        <label> Seller Name: {{ cardInfo.fullName }} </label>
        <br>
        <label> Seller Location: {{ cardInfo.location }} </label>
        <br>
        <label> Creation Date: {{ cardInfo.dateCreated }} </label>
      </b-container>
    </b-input-group-text>
    <br>

    <b-form @submit.prevent="createAction">
      <b-input-group>
        <h6><strong>Section*:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-select v-model="createCardForm.section" required>
          <option disabled value=""> Choose ...</option>
          <option value="ForSale"> For Sale</option>
          <option value="Wanted"> Wanted</option>
          <option value="Exchange"> Exchange</option>
        </b-select>
      </b-input-group>
      <br>

      <b-input-group>
        <h6><strong>Title*:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-form-input type="text" maxlength="25" v-model="createCardForm.title" required/>
      </b-input-group>
      <br>

      <b-input-group>
        <h6><strong>Description:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-form-textarea type="text" maxlength="250" v-model="createCardForm.description"/>
      </b-input-group>
      <br>

      <b-input-group>
        <h6><strong>Keywords:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-form-tags input-id="keyword-name" v-model="createCardForm.keywords" name="blah" :tag-validator="tagValidator"
                     :limit=5 :required="tagRequired"/>
      </b-input-group>
      <br>

      <b-alert variant="danger" dismissible :show="showError.length">{{ showError }}</b-alert>

      <div>
        <b-button style="float: right" variant="primary" type="submit"> Create</b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction"> Cancel</b-button>
      </div>

    </b-form>
  </div>

</template>

<script>
import api from "../../Api";

export default {
  name: "CreateCard",
  props: ['cancelAction', 'showError'],
  data() {
    return {
      tagRequired: false,
      cardInfo: {
        fullName: '',
        location: '',
        dateCreated: ''
      },
      createCardForm: {
        section: '',
        title: '',
        description: "",
        keywords: [],
      },
    }
  },
  mounted() {
    const userId = this.$currentUser.id;
    this.setAutofillData(userId);
  },
  methods: {

    /**
     *  Validates the maximum length of each tag to be at most 10 characters and the maximum number of tags to be
     *  at most 5 before being added to the keywords field.
     *
     */
    tagValidator(tag) {
      let tagsInvalid = tag.length > 10;
      this.tagRequired = tagsInvalid;
      const tagInput = document.getElementById('keyword-name');
      tagInput.setCustomValidity(tagsInvalid ? "The keyword can be at most 10 characters" : "");
      return !tagsInvalid;
    },

    /**
     * Gets the user info by making an api request and updates the variables \
     * with the info.
     * @param id  The ID of the currently logged in user.
     */
    setAutofillData(id) {
      this.createCardForm.creatorId = id;
      const currentDate = new Date();
      this.cardInfo.dateCreated = currentDate.getDate() + "/"
          + (currentDate.getMonth() + 1) + "/"
          + currentDate.getFullYear() + " @ "
          + currentDate.getHours() + ":"
          + currentDate.getMinutes() + ":"
          + currentDate.getSeconds();
      api
          .getUser(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.userData = response.data;
            this.cardInfo.fullName = response.data.firstName + " " + response.data.lastName;
            if (response.data.homeAddress.suburb) {
              this.cardInfo.location = response.data.homeAddress.suburb + ", ";
            }
            if (response.data.homeAddress.city) {
              this.cardInfo.location += response.data.homeAddress.city;
            }
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },

    /**
     *  Passes the card form data to the Marketplace component when create card pressed.
     */
    createAction() {
      this.$emit('createAction', this.createCardForm);
    }

  },
}
</script>
