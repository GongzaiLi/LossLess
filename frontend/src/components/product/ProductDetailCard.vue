<!--
Page show product detail card
Author: Gongzai Li
Date: 19/4/2021
-->
<template>
  <b-card
    class="profile-card"
    style="max-width: 550px"
  ><!--header-border-variant="secondary" border-variant="secondary"  background-color: rgba(0,255,0,0.4);-->
    <b-form
        @submit="okAction"
    >
    <b-card-body>
        <h6><b>ID*:</b></h6>
        This will be automatically changed into the correct format.
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled placeholder="PRODUCT-ID" v-model="productCard.id" autofocus required/>
        </b-input-group>

        <b-input-group>
          <h6><b>Name*:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled v-model="productCard.name" required/>
        </b-input-group>

        <b-input-group>
          <h6><b>Manufacturer:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled v-model="productCard.manufacturer"/>
        </b-input-group>

        <b-input-group>
          <h6><b>Recommended Retail Price*:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <template #prepend>
            <b-input-group-text >{{currency.symbol}}</b-input-group-text>
          </template>
          <b-form-input type="number" v-bind:disabled=disabled v-model="productCard.recommendedRetailPrice" required/>
          <template #append>
            <b-input-group-text >{{currency.code}}</b-input-group-text>
          </template>
        </b-input-group>

        <div v-if="disabled"><!--Only show if we're not in the 'modify' or 'create' mode-->
          <b-input-group>
            <h6><b>Created:</b></h6>
          </b-input-group>
          <b-input-group class="mb-1">
            <b-form-input type="text" disabled v-model="productCard.created"/>
          </b-input-group>
        </div>

        <hr style="width:100%">

        <b-input-group>
          <h6><b>Description:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-textarea rows="5" type="text" v-bind:disabled=disabled v-model="productCard.description"/>
        </b-input-group>
    </b-card-body>
    <hr style="width:100%">
      <div v-if="!disabled">
        <b-button style="float: right" variant="primary" type="submit">OK</b-button>
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