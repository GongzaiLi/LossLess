<template>
    <b-card
        :title="cardInfo.title"
        style="height: 96%"
    >
      <b-card-body>
        <b-card-text>
          <h6>{{cardInfo.description}}</h6>
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
        <b-card-text>
          Created: {{  formatCreated }}
        </b-card-text>
      </b-card-body>
    </b-card>
</template>

<script>

export default {
  name: "MarketplaceCard",
  props: ["cardInfo"],
  data: function () {
    return {
      formattedTags: ""
    }
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
    },

    /**
     * format created date
     */
    formatCreated: function () {
      return new Date(this.cardInfo.created).toUTCString().split(" ").slice(0, 4).join(" ");
    }
  }
}
</script>

