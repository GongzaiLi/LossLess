<template>

  <b-card
      class="profile-card"
      style="min-width: 1000px"
  ><!--header-border-variant="secondary" border-variant="secondary"  background-color: rgba(0,255,0,0.4);-->

    <b-card-text style="text-align: center">
    <h2>Assign a New Administrator</h2>
    <h6>Search and select by clicking on the user you would like to make admin to your business</h6>
    </b-card-text>
    <br>
    <user-search>
    </user-search>

  </b-card>

</template>

<script>
import userSearch from '../user/UserSearch';

export default {
  components: {
    userSearch,
  },
  props: ['makeAdminAction'],
  name: "make-admin-modal",
  data() {
    return {
      }
  },
  mounted() {
    this.$vueEventBus.$on("rowSelect", this.rowSelect);
  },

  methods: {
    rowSelect: async function (record) {
      this.makeAdminAction(record.id);
    }
  }
}
</script>