<!--
Page for users to input business information for registration of a Business
Authors: James
Date: 21/5/21
-->


<template>
  <div>
    <b-card class="shadow">
      <h1><b-icon-shop/> Market Place </h1>
      <b-input-group>
        <b-form-text style="margin-right: 7px">
          Table View
        </b-form-text>
        <b-form-checkbox v-model="isCardFormat" switch/>
        <b-form-text>
          Card View
        </b-form-text>
      </b-input-group>
      <b-tabs v-model=activeTabIndex align="center" fill>
        <b-tab title="For Sale">
          <marketplace-section
            :cards="marketplaceCards.forSaleCards"
            :is-card-format="isCardFormat"
          />
        </b-tab>

        <b-tab title="Wanted">
          <marketplace-section
              :cards="marketplaceCards.wantedCards"
              :is-card-format="isCardFormat"
          />
        </b-tab>

        <b-tab title="Exchange">
          <marketplace-section
              :cards="marketplaceCards.exchangeCards"
              :is-card-format="isCardFormat"
          />
        </b-tab>

      </b-tabs>
    </b-card>
  </div>
</template>

<script>

import MarketplaceSection from "@/components/marketplace/MarketplaceSection";
export default {
  components: {MarketplaceSection},
  data: function () {
    return {
      errors: [],
      activeTabIndex: 0,
      isCardFormat: true,
      marketplaceCards: {
        forSaleCards: [
          {
            title: "Clown shoes",
            description: "Giving away premium clown shoes. " +
                "Purchased them 8 years ago but they dont get " +
                "used anymore since my wife left me. Pick up riccarton",
            tags: ["Free", "Dress up", "Footwear"],
            listerName: "James Harris",
            listerLocation: "Riccarton, Christchurch"
          },
          {
            title: "2002 Toyota Corolla",
            description: "Selling the old beast, 250,000km but goes hard still, looking for $1000 ono",
            tags: ["Car", "Nissan"],
            listerName: "Scooter Hartley",
            listerLocation: "Epsom, Auckland"
          },
          {
            title: "Coconut Water",
            description: "Selling premium coconut water, $5000 a litre",
            tags: ["Water"],
            listerName: "Fabian Gilson",
            listerLocation: "Christchurch"
          },
          {
            title: "Apple Airpods",
            description: "Selling my old apple ear-pods, well worn, $125",
            tags: ["Headphones", "Apple"],
            listerName: "Michael Steven",
            listerLocation: "Kaitaia, Northland"
          }, {
            title: "Bunnings Cap",
            description: "Rare bunnings cap, seen some good times so has some damage, looking for $30",
            tags: ["Hat", "Bunnings", "Collectable"],
            listerName: "Ricky Ponting",
            listerLocation: "Island Bay, Wellington"
          },
        ],
        wantedCards: [],
        exchangeCards: [],
      }
    }
  },
  methods: {
    /**
     * Pushes errors to errors list to be displayed as response on the screen,
     * if there are any.
     */
    pushErrors(error) {
      this.errors.push(error.message);
    },
  },
  computed: {
  }
}
</script>
