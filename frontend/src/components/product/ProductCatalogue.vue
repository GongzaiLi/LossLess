<!--
Page that stores table show the business products
Author: Gongzai Li
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
    </div>

    <div v-if="!productFound" class="no-results-overlay">
      <h4>No Product to display</h4>
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

h4 {
  color: #880000;
}
</style>

<script>
import api from "../../Api";
import Vue from 'vue';


export default {
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
      currentPage: 1
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
              created: "2021-03-14T13:01:58.660Z"
            },
            {
              id: "WATT-420-BEANS2",
              name: "Watties Baked Beans - 410g can",
              description: "Baked Beans as they should be.Baked Beans as they should " +
                "be.Baked Beans as they should be.Baked Beans as they should be." +
                "Baked Beans as they should be.Baked Beans as they should be.",
              recommendedRetailPrice: 2.4,
              created: "2021-04-14T13:01:58.660Z"
            },
            {
              id: "WATT-420-BEANS3",
              name: "Watties Baked Beans - 460g can",
              description: "Caked Beans as they should be.",
              recommendedRetailPrice: 2.7,
              created: "2021-05-14T13:01:58.660Z"
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
     * modify the description only keep 10 words and then add ...
     * @param description
     * @return string
     */
    setDescription: function (description) {
      const showTenWordDescription = description.split(' ').slice(0, 10).join(' ');
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
      //console.log(product, 12212321312321321);
      const productCard = this.$createElement
      // Using HTML string
      const titleVNode = productCard('div', {
        domProps: {
          innerHTML: '<h3>Product Detail</h3>'
        }
      })
      // More complex structure
      const messageVNode = productCard('div', {class: ['foobar']}, [

        productCard('b-img', {
          props: {
            src: 'https://mk0kiwikitchenr2pa0o.kinstacdn.com/wp-content/uploads/2016/05/Watties-Baked-Beans-In-Tomato-Sauce-420g.jpg',
            thumbnail: true,
            center: true,
            fluid: true,
            rounded: 'circle',
            width: 250,
            height: 250
          }
        }),
        productCard('b-card', {class: ['profile-card shadow']}, [
          productCard('b-card-body', [
            productCard('b-container', [
              productCard('h6', [
                productCard('b-row', [
                  productCard('b-col', {
                    props: {
                      cols: '4'
                    }
                  }, [
                    productCard('b', [
                      'Name:'
                    ])
                  ]),
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    '--',
                    product.name,
                  ])])
              ]),
              productCard('h6', [
                productCard('b-row', [
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    productCard('b', [
                      'Recommended Retail Price:'
                    ])
                  ]),
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    '--',
                    product.recommendedRetailPrice,
                  ])])
              ]),
              productCard('h6', [
                productCard('b-row', [
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    productCard('b', [
                      'Created:'
                    ])
                  ]),
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    '--',
                    product.created,
                  ])])
              ]),
              productCard('h6', [
                productCard('b-row', [
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    productCard('b', [
                      'Description:'
                    ])
                  ]),
                  productCard('b-col', {
                    props: {
                      cols: '10'
                    }
                  }, [
                    '--',
                    product.description,
                  ])])
              ])
            ])
          ])
        ]),
        //productCard('p', {class: ['text-center']}, [productCard('b-spinner')]),
      ])
      // We must pass the generated VNodes as arrays
      this.$bvModal.msgBoxOk([messageVNode], {
        title: [titleVNode],
        buttonSize: 'sm',
        centered: true,
        size: 'md'
      })
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