<template>
    <b-card
        :title="cardInfo.title"
        class="mt-3"
    >
      <b-card-text class="marketplace-card-short-description truncate-fade">
        <h6 style="line-height: 1.2em;">{{cardInfo.description}}</h6>
      </b-card-text>
      <b-card-text>
        Tags: {{ formatTags }}
      </b-card-text>
      <b-card-text>
        Seller: {{cardInfo.creator.firstName}} {{cardInfo.creator.lastName}}
      </b-card-text>
      <b-card-text>
        Location: {{ formatAddress }}
      </b-card-text>
    </b-card>
</template>

<style scoped>
/*
This clamps the descriptions to three liens with a nice fade effect when truncated
See https://css-tricks.com/line-clampin/#the-fade-out-way for how this works
*/
.marketplace-card-short-description {
  overflow: hidden;
}
.truncate-fade {
  position: relative;
  height: 3.8em;
}
.truncate-fade:after {
  content: "";
  text-align: right;
  position: absolute;
  bottom: 0;
  right: 0;
  width: 60%;
  height: 1.2em;
  background: linear-gradient(to right, rgba(255, 255, 255, 0), rgba(255, 255, 255, 1) 80%);
}
</style>

<script>

export default {
  name: "MarketplaceCard",
  props: ["cardInfo"],
  data: function () {
    return {
      formattedTags: ""
    }
  },
  methods: {

  },
  computed: {

    /**
     * Combine list of keywords into one string
     */
    formatTags() {
      return this.cardInfo.keywords.join(", ");
    },

    /**
     * Combine fields of address
     */
    formatAddress: function () {
      const address = this.cardInfo.creator.homeAddress;
      return `${address.streetNumber} ${address.streetName}, ${address.suburb}, ` +
          `${address.city} ${address.region} ${address.country} ${address.postcode}`;
    }
  }
}
</script>

