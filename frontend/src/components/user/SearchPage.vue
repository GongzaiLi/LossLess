<template>
  <b-tabs v-model=activeTabIndex align="center" fill>
    <b-tab v-for="[section, sectionName] in sections" :key=section :title=sectionName>

    </b-tab>
  </b-tabs>
</template>

<script>
export default {
  components: {},
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