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

    <b-img v-bind:src=productCard.image center thumbnail rounded="circle" width="250" height="250"/>

    <b-card-body>

      <div>
        <b-input-group>
          <h6><b>Id:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled v-model="productCard.id"/>
        </b-input-group>

        <b-input-group>
          <h6><b>Name:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled v-model="productCard.name"/>
        </b-input-group>

        <b-input-group>
          <h6><b>Recommended Retail Price:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled v-model="productCard.recommendedRetailPrice"/>
        </b-input-group>

        <b-input-group>
          <h6><b>Created:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-input type="text" v-bind:disabled=disabled v-model="productCard.created"/>
        </b-input-group>

        <hr style="width:100%">

        <b-input-group>
          <h6><b>Description:</b></h6>
        </b-input-group>
        <b-input-group class="mb-1">
          <b-form-textarea rows="5" type="text" v-bind:disabled=disabled v-model="productCard.description"/>
        </b-input-group>
      </div>

    </b-card-body>
  </b-card>

</template>

<script>
export default {
  name: "product-detail-card",
  props: ['product'],
  data() {
    return {
      productCard: {
        id: '',
        name: '',
        description: '',
        recommendedRetailPrice: 0,
        created: '',
        image: '',
      },
      disabled: true
    }
  },
  mounted() {
    this.productCard = this.product;
    this.productCard.created = new Date(this.productCard.created).toUTCString();
  }
}
</script>