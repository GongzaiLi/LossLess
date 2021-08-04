<template>
  <b-card class="shadow" style="max-width: 85%">
    <h1><b-icon-search/> Search </h1>
    <b-tabs v-model=activeTabIndex align="center" fill style="padding-top: 16px">
    <b-tab title="User" :title-item-class="'tab-title-class'">
      <user-search>
      </user-search>
    </b-tab>
    <b-tab title="Business" :title-item-class="'tab-title-class'">
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
export default {
  components: {UserSearch},
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