<template>
  <div>
    <h1 align="center"><b-icon-shop/> Create Card </h1>
    <br>

    <b-form>
      <b-input-group>
        <h6><strong>Section*:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-select v-model="createCardForm.section" required>
          <option disabled value=""> Choose ...</option>
          <option value="ForSale"> For Sale </option>
          <option value="Wanted"> Wanted </option>
          <option value="Exchange"> Exchange </option>
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
        <b-form-textarea type="text" maxlength="250" v-model="createCardForm.description" required/>
      </b-input-group>
      <br>

      <b-input-group>
        <h6><strong>Keywords*:</strong></h6>
      </b-input-group>
      <b-input-group class="mb-1">
        <b-form-tags v-model="createCardForm.keywords" required/>
      </b-input-group>
      <br>

      <div>
        <b-button style="float: right" variant="primary" type="submit" @click="createAction"> Create </b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction"> Cancel </b-button>
      </div>

    </b-form>
  </div>

</template>

<script>
export default {
  name: "CreateCard",
  props: ['createAction', 'cancelAction'],
  data() {
    return {
      createCardForm: {
        creatorId: '',
        section: '',
        title: '',
        description: "",
        keywords: []
      }
    }
  }
}
</script>
