<template>
  <div>
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

<style scoped>

</style>