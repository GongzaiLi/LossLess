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
        <label> Creator: {{ cardInfo.fullName }} </label>
        <br>
        <label> Location: {{ cardInfo.location }} </label>
        <br>
        <label> Creation Date: {{ cardInfo.dateCreated }} </label>
      </b-container>
    </b-input-group-text>
    <br>

    <b-form @submit.prevent="createAction" @input="setCustomValidities">
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
        <b-form-input type="text" maxlength="25" v-model="createCardForm.title" required trim/>
      </b-input-group>
      <br>

      <b-input-group>
        <h6><strong>Description:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-form-textarea id="market-description" type="text" maxLength=250 trim v-model="createCardForm.description"
                         :required="descriptionRequired"/>
      </b-input-group>
      <br>
      <b-input-group>
        <h6><strong>Keywords:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-form-tags input-id="keyword-name" v-model="createCardForm.keywords" name="blah" :tag-validator="tagValidator"
                     invalid-tag-text="Tag too long - Max length allowed is 10"
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
import {formatAddress, getMonthName} from "../../util";

export default {
  name: "CreateCard",
  props: ['cancelAction', 'showError', 'defaultSection'],
  data() {
    return {
      descriptionRequired: false,
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
    this.createCardForm.section = this.defaultSection || '';
  },
  methods: {

    /**
     * Uses HTML constraint validation to set custom validity rules checks:
     * check the createCard description can not be more than 10 lines
     **/
    setCustomValidities() {
      this.descriptionRequired = this.createCardForm.description.split('\n').length > 10;
      const descriptionInput = document.getElementById('market-description');
      descriptionInput.setCustomValidity(this.descriptionRequired ? "Description can be at most 10 lines and 250 characters" : "");
    },

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

      this.cardInfo.dateCreated = currentDate.getDate() + " " + getMonthName(currentDate.getMonth()) + " " +
          currentDate.getFullYear() + " @ "
          + currentDate.getHours() + ":"
          + this.addLeadingZero(currentDate.getMinutes());
      api
          .getUser(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.userData = response.data;
            this.cardInfo.fullName = response.data.firstName + " " + response.data.lastName;
            this.cardInfo.location = formatAddress(response.data.homeAddress, 3);
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
    },

    /**
     * Adds a leading zero before a number if it is below 10
     * Used for formatting hours and seconds nicely when displaying times
     * @param number The number to potentially have a 0 put in front of
     * @returns {string} A string of the input number with an added 0 if below 10
     */
    addLeadingZero(number) {
      if (number < 10) {
        return '0' + number.toString()
      } else {
        return number.toString()
      }
    }

  },
}
</script>
