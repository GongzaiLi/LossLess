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
          Seller: {{cardInfo.listerName}}
          {{cardInfo.listerLocation}}
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
  methods: {

  },
  computed: {
    formatTags() {
      return this.cardInfo.tags.join(", ");
    }
  }
}
</script>

