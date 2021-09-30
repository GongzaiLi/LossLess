<template>
    <b-card
        style="height: 96%"
        @click="this.cardClicked"
        class="marketplace-card shadow-sm"
    >
      <h5 class="card-title single-line-clamped">{{cardInfo.title}}</h5>
      <p class="sub-title">Ends: {{ formatExpiry }}</p>
      <hr>
      <b-card-text class="marketplace-full-card-description">
        <p v-if="cardInfo.description" class="dual-line-clamped" style="line-height: 1.2em;">{{cardInfo.description}}</p>
        <p v-else class="sub-title" style="line-height: 1.2em;">No Description</p>
      </b-card-text>

      <hr>
      <b-card-text class="single-line-clamped">
        Tags: <b-badge v-for="keyword in this.cardInfo.keywords" :key="keyword" class="ml-1">{{keyword}}</b-badge>
      </b-card-text>
      <b-card-text>
        <b-icon-person-fill/> {{cardInfo.creator.firstName}} {{cardInfo.creator.lastName}}
        <br>
        <b-icon-house-door-fill/> {{ getAddress }}
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

.dual-line-clamped {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

p.sub-title {
  font-style: italic;
  color: grey;
  font-size: 13px;
}

.marketplace-full-card-description {
  white-space: pre-line;
}

</style>

<script>
import {formatAddress, getMonthName} from "../../util";

export default {
  name: "MarketplaceCard",
  props: ["cardInfo"],
  data: function () {
    return {
      formattedTags: ""
    }
  },
  methods: {

    /**
     * Emits an event 'cardClicked' when the card is clicked
     * which is listened to by the marketplace.
     */
    cardClicked() {
      this.$emit('cardClicked', this.cardInfo)
    },

  },
  computed: {
    /**
     * Formats the address using util function and appropriate privacy level.
     *
     * @return address formatted
     */
    getAddress: function () {
      return formatAddress(this.cardInfo.creator.homeAddress, 3);
    },

    /**
     * Format User's date of birth date
     */
    formatExpiry: function () {
      const expiryDate =  new Date(this.cardInfo.displayPeriodEnd)
      return expiryDate.getDate() + " " + getMonthName(expiryDate.getMonth()) + " " + expiryDate.getFullYear();
    },
  }
}
</script>

