<!--
Page that stores table show the business products
Author: Gongzai Li，Arish Abalos
Date: 15/4/2021
-->
<template>

  <div>
    <h2>Product Catalogue</h2>
    <div v-if="productFound">
      <b-form-group>
        <createButton :businessId="$route.params.id" class="float-right"></createButton>
      </b-form-group>
      <b-table
        striped hovers
        responsive="lg"
        no-border-collapse
        bordered
        @row-clicked="tableRowClick"
        :fields="fields"
        :items="items"
        :per-page="perPage"
        :current-page="currentPage"
      > <!--stacked="sm" table-class="text-nowrap"-->
      </b-table>
      <b-row>
        <b-col>
          <b-pagination v-model="currentPage" :total-rows="totalResults" :per-page="perPage"></b-pagination>
        </b-col>
        <b-col style="text-align: right; margin-top: 6px">
          Displaying {{ itemsRangeMin }} - {{ itemsRangeMax }} of total {{ totalResults }} results.
        </b-col>
      </b-row>

      <b-modal id="product-card" hide-header centered>
        <!--
        <template #modal-header>
          <small class="text-muted">Product Card</small>
        </template>
        -->
        <product-detail-card :product="productSelect"/>
        <!--
        <template #modal-footer>
          <small class="text-muted">Product Card</small>
        </template>
        -->
      </b-modal>

    </div>

    <div v-if="!productFound" class="no-results-overlay">
      <h4 style="color: #880000">No Product to display</h4>
      <createButton :businessId="$route.params.id"></createButton>
    </div>
  </div>


</template>

<style>
.no-results-overlay {
  margin-top: 7em;
  margin-bottom: 7em;
  text-align: center;
}

h2 {
  text-align: center;
}

</style>

<script>
import api from "../../Api";
import Vue from 'vue';
import productDetailCard from './ProductDetailCard';


export default {
  components: {
    productDetailCard
  },
  component: {
    createButton: Vue.component('createButton', {
      props: ['value'],
      template: `
        <b-button @click="goToCreateProduct">
        <b-icon-plus-square-fill animation="fade"/>
        Create
        </b-button>`,
      methods: {
        goToCreateProduct: function () {
          this.$router.push({path: `/businesses/${this.value}/products/createProduct`});
        }
      }
    })
  },
  data: function () {
    return {
      items: [],
      productFound: true,
      perPage: 10,
      currentPage: 1,
      productSelect: {},
    }
  },
  mounted() {
    const businessId = this.$route.params.id;
    this.getProducts(businessId);

  },
  methods: {
    /**
     * this is a get api which can take Specific business product data keep in the items and then show in the table
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param businessId
     */
    //todo need check the api is work
    getProducts: function (businessId) {
      api
        .getProducts(businessId)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.setResponseData(response.data);
          this.productFound = true;
        })
        .catch((error) => {
          this.$log.debug(error);
          //
          this.productFound = false;

          // fake date can use be test.
          /*
          this.items = [
            {
              id: "WATT-420-BEANS1",
              name: "Watties Baked Beans - 430g can",
              description: "Aaked Beans as they should be.",
              recommendedRetailPrice: 2.2,
              created: "2021-03-14T13:01:58.660Z",
              image: 'https://mk0kiwikitchenr2pa0o.kinstacdn.com/wp-content/uploads/2016/05/Watties-Baked-Beans-In-Tomato-Sauce-420g.jpg',
            },
            {
              id: "WATT-420-BEANS2",
              name: "Apple",
              description: "Baked Beans as they should be.Baked Beans as they should " +
                "be.Baked Beans as they should be.Baked Beans as they should be." +
                "Baked Beans as they should be.Baked Beans as they should be.",
              recommendedRetailPrice: 2.4,
              created: "1077-04-14T13:01:58.660Z",
              image: 'https://i2.wp.com/ceklog.kindel.com/wp-content/uploads/2013/02/firefox_2018-07-10_07-50-11.png?w=641&ssl=1',
            },
            {
              id: "WATT-420-BEANS3",
              name: "Tip Top Super Soft Toast Bread White Superthick",
              description: "Made in New Zealand with imported & local ingredients.\n" +
                "\n" +
                "Tip top supersoft white super thick is a kiwi classic. Delicious and soft white bread perfect for any occasion.",
              recommendedRetailPrice: 2.7,
              created: "2077-05-14T13:01:58.660Z",
              image: 'https://static.countdown.co.nz/assets/product-images/zoom/9415142003740.jpg',
            }
          ];
          this.productFound = true;

          */
        })
    },

    /**
     * set the response data to items
     * @param data
     */
    //todo may need rebuilt the data form.
    setResponseData: function (data) {
      this.items = data;
    },

    /**
     * modify the description only keep 5 words and then add ...
     * @param description
     * @return string
     */
    setDescription: function (description) {
      const showTenWordDescription = description.split(' ').slice(0, 5).join(' ');
      return `${showTenWordDescription}${showTenWordDescription.endsWith('.') ? '..' : '...'}`;
    },

    /**
     * modify the date to day/month/year.
     * @param created
     * @return string
     */
    setCreated: function (created) {
      const date = new Date(created);
      return `${date.getUTCDate() > 9 ? '' : '0'}${date.getUTCDate()}/` +
        `${date.getUTCMonth() + 1 > 9 ? '' : '0'}${date.getUTCMonth() + 1}/` +
        `${date.getUTCFullYear()}`;
    },

    /**
     * when click the table will show a new model card.
     * @param product object
     */
    tableRowClick(product) {
      this.productSelect = product;
      this.$bvModal.show('product-card');
    }

  },

  computed: {

    /**
     * set table parameter
     * @returns object
     */
    fields: function () {
      return [
        {
          key: 'id',
          label: 'ID',
          sortable: true
        },
        {
          key: 'name',
          label: 'Name',
          sortable: true
        },
        {
          key: 'description',
          label: 'Description',
          formatter: (value) => {
            return this.setDescription(value);
          },
          sortable: true
        },
        {
          key: 'recommendedRetailPrice',
          label: 'RRP',
          sortable: true
        },
        {
          key: 'created',
          label: 'Created',
          formatter: (value) => {
            return this.setCreated(value);
          },
          sortable: true
        }];
    },

    /**
     * The totalResults function just computed how many pages in the search table.
     * @returns number
     */
    totalResults: function () {
      return this.items.length;
    },

    /**
     * Computes the min range of product displaying on the table at the current page.
     * @returns number
     */
    itemsRangeMin: function () {
      return this.currentPage === 1 ? 1 : (this.currentPage - 1) * this.perPage + 1;
    },

    /**
     * Computes the max range of product displaying on the table at the current page.
     * @returns number
     */
    itemsRangeMax: function () {
      return this.currentPage * this.perPage <= this.totalResults ? this.currentPage * this.perPage : this.totalResults;
    }
  }

}
</script>