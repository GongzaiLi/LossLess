<template>

  <b-card
      class="profile-card"
      style="min-width: 1100px"
  ><!--header-border-variant="secondary" border-variant="secondary"  background-color: rgba(0,255,0,0.4);-->

    <b-card-text style="text-align: left">
    <h3>Assign a New Administrator</h3>
    <h6>Search User by name and select by clicking on the User to assign business administrator role.</h6>
    </b-card-text>
    <user-search :is-make-admin="true" @rowSelect="rowSelect" >
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

  methods: {
    rowSelect: async function (record) {
      this.makeAdminAction(record.id);
    }
  }
}
</script>