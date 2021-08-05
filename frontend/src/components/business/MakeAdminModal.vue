<template>

  <b-card
      class="profile-card"
      style="min-width: 1100px"
  >

    <b-card-text style="text-align: left">
    <h3>Assign a New Administrator</h3>
    <h6>Search User by name and select by clicking on the User to assign business administrator role.</h6>
    </b-card-text>
    <user-search :is-make-admin="true" @rowSelect="rowSelect" >
    </user-search>

  </b-card>

</template>

<script>
import UserSearch from '../search/UserSearch';

export default {
  components: {
    UserSearch,
  },
  props: ['makeAdminAction', 'userIdToAdmin'],
  name: "make-admin-modal",

  methods: {
    rowSelect: async function (record) {
      this.makeAdminAction(record.id);
    }
  }
}
</script>