<!--
Individual Business profile Page. Currently displays all business data
Author: Gongzai Li
Date: 29/03/2021
-->
<template>
  <div>
    <b-card border-variant="secondary" header-border-variant="secondary"
            class="profile-card shadow" no-body
    > <!-- v-if="businessFind" -->

      <template #header>
        <b-row>
          <b-col>
            <h4 class="mb-1">{{ businessData.name }}</h4>
            <p class="mb-1">{{ memberSince }}</p>
          </b-col>
        </b-row>
      </template>

      <b-list-group border-variant="secondary">
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            {{ businessData.description }}
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
              <b-col>{{ businessData.businessType }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-geo-alt></b-icon-geo-alt> <!-- geo-alt-->
              </b-col>
              <b-col cols="4"><b>Address:</b></b-col>
              <b-col>{{ Object.values(businessData.address).join(' ') }}</b-col>
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
              <b-col>{{ businessData.phoneNumber }}</b-col>
            </b-row>
          </h6>
        </b-container>
      </b-card-body>

      <b-list-group border-variant="secondary">
        <b-list-group-item>
          <b-card-text style="text-align: justify">
            <h4 class="mb-1">Product</h4>
          </b-card-text>
          <b-row v-for="product in products" :key="product">
            <b-col cols="2" class="mb-1">{{ product }}</b-col>
          </b-row>
        </b-list-group-item>
      </b-list-group>
    </b-card>
  </div>


</template>

<style scoped>
.profile-card {
  max-width: 50rem;
  margin-left: auto;
  margin-right: auto;
}

h6 {
  line-height: 1.4;
}
</style>

<script>
import dateCalculation from "./model/DateCalculation";
import api from "@/Api";

export default {

  //Todo still has errors because I Did not connect to Navbar, and I check all errors and all show Navbar issues.

  data: function () {
    return {
      businessData: {
        email: 'may have email',
        phoneNumber: '+64 3 555 0129',
        id: 1,
        primaryAdministratorId: 20,
        name: "Lumbridge General Store",
        description: "A one-stop shop for all your adventuring needs",
        address: {
          streetNumber: "3/24",
          streetName: "Ilam Road",
          city: "Christchurch",
          region: "Canterbury",
          country: "New Zealand",
          postcode: "90210"
        },
        businessType: "Accommodation and Food Services",
        created: "2020-07-14T14:52:00Z"
      },
      memberSince: '',
      products: ['products1', 'products2', 'products3'],
      businessFind: true
    }
  },

  mounted() {
    const businessId = this.$route.params.id;
    this.getBusinessInfo(businessId);

    //todo When is the getBusinessInfo can work, there can delete.
    this.memberSince = dateCalculation.memberSince(this.businessData.created);
  },

  methods: {
    /**
     * this is a get api which can take Specific business to display on the page
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param id
     **/
    //Todo need check the api is work.
    getBusinessInfo: function (id) {
      api
        .getBusinesses(id)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.setResponseData(response.data);
          this.businessFind = true;
        })
        .catch((error) => {
          this.$log.debug(error);
          this.businessFind = false;
        })
    },
    /**
     * set the response data to businessData
     * @param data
     */
    //todo may need split the data from response.
    setResponseData: function(data) {
      this.businessData = data;
      //this.memberSince = (this.businessFind)? dateCalculation.memberSince(this.businessData.created) : '';
      this.memberSince = dateCalculation.memberSince(this.businessData.created);
    }
  }
}
</script>
