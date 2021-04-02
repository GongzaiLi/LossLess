<!--
Individual Business profile Page. Currently displays all business data
Author: Gongzai Li
Date: 29/03/2021
-->
<template>
  <div>
    <b-card border-variant="secondary" header-border-variant="secondary"
            class="profile-card shadow" no-body
    >

      <template #header>
        <b-row>
          <b-col>
            <h4 class="mb-1">{{ businessData.businessName }}</h4>
            <p class="mb-1">{{ businessData.date }}</p>
          </b-col>
        </b-row>
      </template>

      <b-list-group border-variant="secondary">
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            {{ businessData.describe }}
          </b-card-text>
        </b-list-group-item>
      </b-list-group>

      <b-card-body>
        <b-container>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-tags></b-icon-tags>
              </b-col>
              <b-col cols="4"><b>Type:</b></b-col>
              <b-col>{{ businessData.type }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-geo-alt></b-icon-geo-alt> <!-- geo-alt-->
              </b-col>
              <b-col cols="4"><b>Address:</b></b-col>
              <b-col>{{ businessData.address }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-envelope></b-icon-envelope>
              </b-col>
              <b-col cols="4"><b>Email:</b></b-col>
              <b-col>{{ businessData.email }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-phone-vibrate></b-icon-phone-vibrate>
              </b-col>
              <b-col cols="4"><b>Phone Number:</b></b-col>
              <b-col>{{ businessData.phone }}</b-col>
            </b-row>
          </h6>
        </b-container>
      </b-card-body>

      <b-list-group border-variant="secondary">
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            <h4 class="mb-1">Product</h4>
          </b-card-text>
          <b-row v-for="product in businessData.products" :key="product">

            <b-col cols="2" class="mb-1">{{product}}</b-col>
          </b-row>
        </b-list-group-item>
      </b-list-group>
    </b-card>
  </div>


</template>

<style scoped>
.profile-card {
  max-width: 45rem;
  margin-left: auto;
  margin-right: auto;
}

h6 {
  line-height: 1.4;
}
</style>

<script>


export default {
  data: function () {
    return {
      businessData: {
        businessName: 'Business Name',
        describe: 'there are some Business describe',
        address: 'some where',
        type: 'supermarket',
        date: '2021-04-01',
        phone: 1234567,
        email: 'email@email',
        products: ['product1', 'product2', 'product3']
      }
    }
  }
}
</script>
