<template>
  <div>
    <p>{{ this.searchQuery }}</p>
  </div>
</template>

<script>
export default {
  data: function () {
    return {
      searchQuery: ''
    }
  },

  mounted() {
    this.searchQuery = this.$route.query.searchQuery;
    this.sendApiRequest();

  },

  methods: {

    sendApiRequest() {
      //IMPLEMENT THIS SO REQUEST SENT ON LOAD WITH SEARCH QUERY
    }

  },
}
</script>
