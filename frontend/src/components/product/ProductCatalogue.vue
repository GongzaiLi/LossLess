<!--
Page that stores table show the business products
Author: Gongzai Li，Arish Abalos
Date: 15/4/2021
-->
<template>

  <div>
    <h2>Product Catalogue</h2>
    <div>
      <b-form-group>
        <createButton :businessId="$route.params.id" class="float-right"></createButton>
      </b-form-group>
      <b-table
        striped hovers
        responsive="lg"
        no-border-collapse
        bordered
        show-empty
        @row-clicked="tableRowClick"
        :fields="fields"
        :items="items"
        :per-page="perPage"
        :current-page="currentPage"
        :busy="tableLoading"
      > <!--stacked="sm" table-class="text-nowrap"-->
        <template #empty>
          <div class="no-results-overlay">
            <h4>No Product to display</h4>
          </div>
        </template>
        <template #table-busy>
          <div class="no-results-overlay">
            <h4>Loading...</h4>
          </div>
        </template>
      </b-table>
      <pagination v-if="items.length>0" :per-page="perPage" :total-items="totalItems" v-model="currentPage"/>

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
import pagination from '../Pagination';


export default {
  components: {
    productDetailCard,
    pagination
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
      perPage: 10,
      currentPage: 1,
      productSelect: {},
      tableLoading: true,
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
          this.tableLoading = false;
        })
        .catch((error) => {
          this.$log.debug(error);
          //

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
*/
          this.tableLoading = false;
          //
        });
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
     * modify the description only keep 20 characters and then add ...
     * @param description
     * @return string
     */
    setDescription: function (description) {
      const showTenWordDescription = description.slice(0, 20).trim();
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
    totalItems: function () {
      return this.items.length;
    }
  }

}
</script>