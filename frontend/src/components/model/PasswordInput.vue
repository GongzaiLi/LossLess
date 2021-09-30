<template>
  <div class="input-group mb-2 mr-sm-2">
    <b-form-input :type="passwordType()" :required="isRequired"
                  v-model="inputPassword"
                  :value="value"
                  @input="$emit('input', inputPassword)"
                  maxLength=100
                  :minLength="minPasswordLength ? minPasswordLength : 0"
                  class="form-control"
                  :id="id"
                  :placeholder="placeHolder"
                  :autocomplete="autocomplete ? autocomplete : 'off'"/>
    <div class="input-group-prepend">
      <div class="input-group-text" @click="showPassword">
        <b-icon-eye-fill v-if="!visiblePassword"/>
        <b-icon-eye-slash-fill v-else-if="visiblePassword"/>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "password-input",
  props:['value', 'id', 'placeHolder', 'isRequired', 'autocomplete', 'minPasswordLength'],
  data() {
    return {
      visiblePassword: false,
      inputPassword: '',
    }
  },
  methods: {
    //Password can hidden or shown by clicking button
    showPassword: function () {
      this.visiblePassword = !this.visiblePassword;
    },
    //if the password can visible to be text else be password
    passwordType() {
      return this.visiblePassword ? "text" : "password";
    },
  },
}
</script>
