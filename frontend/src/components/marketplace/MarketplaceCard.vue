<template>
    <b-card
        :title="cardInfo.title"
        style="height: 96%"
        @click="this.cardClicked"
        class="marketplace-card"
    >
      <b-card-body>
        <b-card-text>
          <h6>{{cardInfo.description}}</h6>
        </b-card-text>
        <b-card-text>
          Tags: {{ formatTags }}
        </b-card-text>
        <b-card-text>
          Seller: {{cardInfo.listerName}}
          {{cardInfo.listerLocation}}
        </b-card-text>
      </b-card-body>
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
    formatTags() {
      return this.cardInfo.tags.join(", ");
    }
  }
}
</script>

