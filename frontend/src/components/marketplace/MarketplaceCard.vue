<template>
  <b-card
      style="height: 96%"
      @click="this.cardClicked"
      class="marketplace-card"
  >
    <h5 class="card-title single-line-clamped">{{ cardInfo.title }}</h5>
    <hr>
    <b-card-text>
      <p class="single-line-clamped" style="line-height: 1.2em;">{{ cardInfo.description }}</p>
    </b-card-text>
    <hr>
    <b-card-text class="single-line-clamped">
      Tags:
      <b-badge v-for="keyword in this.cardInfo.keywords" :key="keyword" class="ml-1">{{ keyword }}</b-badge>
    </b-card-text>
    <b-card-text>
      Seller: {{ cardInfo.creator.firstName }} {{ cardInfo.creator.lastName }}
      <br>
      Location: {{ formatAddress }}
    </b-card-text>
    <b-card-text>
      Created: {{ formatCreated }}
    </b-card-text>
  </b-card>
</template>

<style scoped>
.marketplace-card {
  cursor: pointer;
}

.marketplace-card:hover {
  -webkit-box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.18) !important;
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.18) !important;
}

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
    cardClicked() {
      this.$emit('cardClicked', this.cardInfo)
    },

  },
  computed: {

    /**
     * Combine fields of address
     */
    formatAddress: function () {
      const address = this.cardInfo.creator.homeAddress;
      return `${address.suburb ? address.suburb + ', ' : ''}${address.city}`;
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

