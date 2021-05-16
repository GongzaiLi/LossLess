<!--
Page show product detail card
Author: Gongzai Li
Date: 19/4/2021
-->
<template>
  <b-card
    class="profile-card"
    style="max-width: 550px"
  >
    <b-form
        @submit="okAction"
    >
    <b-card-body>
        <h6><strong>ID*:</strong></h6>
        <p v-bind:hidden=disabled style="margin:0">Ensure there are no special characters (e.g. "/","?").
          <br>This will be automatically changed into the correct format.</p>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" pattern="[a-zA-Z0-9\d\-_\s]{0,100}"  v-bind:disabled=disabled placeholder="PRODUCT-ID" v-model="productCard.id" autofocus required/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Name*:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" v-bind:disabled=disabled v-model="productCard.name" required/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Manufacturer:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" maxlength="50" v-bind:disabled=disabled v-model="productCard.manufacturer"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Recommended Retail Price:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <template #prepend>
            <b-input-group-text >{{currency.symbol}}</b-input-group-text>
          </template>
          <b-form-input type="number" step=".01" min=0 v-bind:disabled=disabled v-model="productCard.recommendedRetailPrice" required/>
          <template #append>
            <b-input-group-text >{{currency.code}}</b-input-group-text>
          </template>
        </b-input-group>

        <div v-if="disabled"><!--Only show if we're not in the 'modify' or 'create' mode-->
          <b-input-group>
            <h6><strong>Created:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-1">
            <b-form-input type="text" maxlength="50" disabled v-model="productCard.created"/>
          </b-input-group>
        </div>

        <hr style="width:100%">

        <b-input-group>
          <h6><strong>Description:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-textarea rows="5" type="text" maxlength="250" v-bind:disabled=disabled v-model="productCard.description "/>
        </b-input-group>
    </b-card-body>
    <hr style="width:100%">
      <div>
        <b-button  v-if="!disabled" style="float: right" variant="primary" type="submit">OK</b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
      </div>
    </b-form>
  </b-card>

</template>

<script>
export default {
  name: "product-detail-card",
  props: ['product', 'disabled', 'currency', 'okAction', 'cancelAction'],
  data() {
    return {
      productCard: {
        id: '',
        name: '',
        description: '',
        manufacturer: '',
        recommendedRetailPrice: 0,
        created: '',
        image: '',
      },
    }
  },
  mounted() {
    this.productCard = this.product;
    // Sometimes the product passed in should not have a 'created' attribute, eg. if it is a new object for creation.
    if (this.productCard.created) {
      this.productCard.created = new Date(this.productCard.created).toUTCString();
    }
  },
}
</script>