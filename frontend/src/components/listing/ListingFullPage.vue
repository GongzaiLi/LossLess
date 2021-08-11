<template>
  <div>
    <b-row>
      <b-link variant="info" class="back-to-search-link" to="/listingSearch">
        <strong><h4><b-icon-arrow-left/> Back to results </h4></strong>
      </b-link>
      <b-card class="listing_card shadow">
        <div style="float:left; margin-right: 10px">
          <b-carousel
              id="carousel-1"
              :controls="listingItem.images.length > 1"
              indicators
              :interval="0"
              ref="image_carousel"
              v-model="slideNumber"
              v-if="listingItem.images.length > 0"
          >
            <b-carousel-slide v-for="image in listingItem.images" :key="image.id">
              <template #img>
                <!-- The class .d-block prevent browser default image alignment -->
                <img
                    class="product-image d-block w-100 rounded"
                    alt="Product image"
                    :src="getURL(image.fileName)"
                >
              </template>
            </b-carousel-slide>
          </b-carousel>
        </div>
        <div style="float:left; margin-left: 10px">
          <h1 style="text-align: center">
            {{ listingItem.name }}
          </h1>
          <h6 style="text-align: center"> Listed on: {{listingItem.created}}</h6>
          <b-card no-body id="infobox-1">
            <template #header>
              <h5> Quantity: {{listingItem.quantity}} </h5>
              <h5> Closes: {{listingItem.closes}} </h5>
              <h6 style="word-wrap: normal; font-size: 12px"> {{listingItem.description}} </h6>
              <h2> {{listingItem.price}} </h2>
            </template>
          </b-card>
        </div>
      </b-card>
    </b-row>
  </div>
</template>

<style>

.listing_card {
  max-width: 55rem;
  margin-left: auto;
  margin-right: auto;
}

@media only screen and (min-width: 1250px) {
  .back-to-search-link {
    position: absolute;
    left: 250px;
  }
}

.carousel-control-prev-icon , .carousel-control-next-icon {
  background-color: black !important;
  border-radius: 20%;
  padding:4px;
  color: white;
}

#carousel-1{
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
  max-width: 25rem;
  margin-top: 15px;
}

#infobox-1{
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
  max-width: 25rem;
}

</style>

<script>
import Api from "../../Api";

export default {
  name: "listing-full",
  data() {
    return {
      slideNumber: 0,
      listingItem: {
        id: '',
        name: 'Chocolate Bar',
        quantity: 4,
        closes: '11/11/2021',
        description: 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean ' +
            'commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis,',
        manufacturer: '',
        price: '$20.00',
        created: '11/11/2000',
        images: [{
          fileName: 'media/images/cec425c1-ce03-4a95-ae35-93908e098be1.png'
        }, {
          fileName: 'media/images/cec425c1-ce03-4a95-ae35-93908e098be1.png'
        }, {
          fileName: 'media/images/cec425c1-ce03-4a95-ae35-93908e098be1.png'
        }],
      },
      imageError: ""
    }
  },
  methods: {

    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return Api.getImage(imageFileName);
    }
  },
}
</script>
