<!--
Page show product detail card
Author: Gongzai Li
Date: 19/4/2021
-->
<template>

  <b-card border-variant="secondary"
          header-border-variant="secondary"
          class="profile-card shadow"
          style="max-width: 550px"
          bg-variant="success"><!--light-->

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
          <h6><b>description:</b></h6>
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
        id: "WATT-420-BEANS1",
        name: "Watties Baked Beans - 430g can",
        description: "Baked Beans as they should be.Baked Beans as they should " +
          "be.Baked Beans as they should be.Baked Beans as they should be." +
          "Baked Beans as they should be.Baked Beans as they should be.",
        recommendedRetailPrice: 2.2,
        created: "2021-03-14T13:01:58.660Z",
        image: 'https://mk0kiwikitchenr2pa0o.kinstacdn.com/wp-content/uploads/2016/05/Watties-Baked-Beans-In-Tomato-Sauce-420g.jpg',
      },
      disabled: true
    }
  },
  mounted() {
    this.productCard = this.product;
  }
}
</script>

<style scoped>

</style>