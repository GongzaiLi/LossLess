<!--
A toast to show the business or user's currency changes.
Date: sprint_6
-->
<template>
  <div>
    <b-toast :id="toastId" variant="primary" solid auto-hide-delay="30000">
      <template #toast-title>
        <div class="d-flex flex-grow-1 align-items-baseline">
          <b-icon-globe/>
          <strong class="mr-auto m-1"> Currency changed </strong>
        </div>
      </template>
      By updating your country your currency has changed from <strong>{{ oldCurrency }} to {{ newCurrency }}</strong>.
      <br>
      <br>
      All of your listings are now in this currency, and any future listings you create will be in this currency.
      <br>
      <br>
      {{ notificationText }}

    </b-toast>
  </div>
</template>

<script>
export default {
  name: "currencyNotification",
  props: ['toastId', "oldCurrency", "newCurrency", "isUser"],
  data() {
    return {
      notificationText: ''
    }
  },
  mounted() {
    this.notificationText = this.isUser ?
        "This will not affect the currency of products in your administered business unless you also modify the address of the business." : ''
  }
}
</script>
