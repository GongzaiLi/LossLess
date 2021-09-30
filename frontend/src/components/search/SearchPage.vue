<template>
  <b-card class="shadow">
    <h1><b-icon-search/> Search </h1>
    <b-tabs v-model=activeTabIndex align="center" fill style="padding-top: 16px">
    <b-tab title="User" :title-item-class="'tab-title-class'">
      <user-search/>
    </b-tab>
    <b-tab title="Business" :title-item-class="'tab-title-class'">
      <business-search/>
    </b-tab>
  </b-tabs>
  </b-card>
</template>

<style>

.tab-title-class {
  font-size: 24px;
}
</style>

<script>
import UserSearch from "./UserSearch";
import BusinessSearch from "./BusinessSearch";
export default {
  components: {UserSearch, BusinessSearch},
  name: "SearchPage", // DO NOT DELETE!!! The <keep-alive include="SearchPage"> in App.vue only matches component names so we register a name here.
  data: function () {
    return {
      sections: [['UserSearch', 'User Search'], ['BusinessSearch', 'Business Search']],
      error: "",
      activeTabIndex: 0,
      showDismissibleAlert: false
    }
  },
}
</script>