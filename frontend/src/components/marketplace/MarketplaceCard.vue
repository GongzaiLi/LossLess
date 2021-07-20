<template>
    <b-card
        class="mt-3"
    >
      <h4 class="card-title single-line-clamped">{{cardInfo.title}}</h4>
      <b-card-text>
        <p class="single-line-clamped" style="line-height: 1.2em;">{{cardInfo.description}}</p>
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
This clamps to one line with ellipsis when overflowed
*/
.single-line-clamped {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

