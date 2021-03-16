<template>
  <b-navbar v-if="!['login', 'register'].includes($route.name)"
    toggleable="lg" type="dark" variant="dark" fixed="top"
  >
    <b-navbar-brand href="#">Wasteless</b-navbar-brand>

    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav>
        <b-nav-item v-on:click="my_profile">My Profile</b-nav-item>
        <b-nav-item to="/userSearch">User Search</b-nav-item>
      </b-navbar-nav>
      <b-navbar-nav class="ml-auto">
        <b-nav-item-dropdown right>
          <template #button-content>
            <em>{{getUserRole()}}</em>
          </template>
          <b-dropdown-item @click="logOut">Log Out</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script>
/**
 * A navbar for the site that contains a brand link and navs to user profile and logout.
 * Will not be shown if is current in the login or register routes. This is done by checking
 * that the names (not paths!) of the current route are not 'login' or 'register'. Names of routes
 * are set in router/index.js
 */
export default {
  name: "Navbar.vue",
  methods: {
    my_profile: function () {
      this.$router.push({path: `/user/${this.$currentUser.id}`});
    },
    getUserRole: function () {
      return this.$currentUser.role;
    },
    /**
     * Logs out the current user and redirects to the login page.
     * Currently does nothing with managing cookies, this needs to be implemented later.
     */
    logOut() {
      this.$currentUser = null;
      this.$router.push('/login');
    }
  },
}
</script>