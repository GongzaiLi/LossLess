<template>
  <b-container id="app">
    <b-navbar toggleable="lg" type="dark" variant="dark" fixed="top">
      <b-navbar-brand href="#">NavBar</b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav class="ml-auto">
          <b-nav-item-dropdown right>
            <template #button-content>
              <em>User</em>
            </template>
            <b-dropdown-item href="#">Profile</b-dropdown-item>
            <b-dropdown-item @click="logOut">Log Out</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <router-view/>
  </b-container>
</template>

<script>
import Login from "./components/Login";
import Register from "./components/Register";
import Students from "./components/Students";
import UserProfile from "./components/UserProfile";


// Vue app instance
// it is declared as a reusable component in this case.
// For global instance https://vuejs.org/v2/guide/instance.html
// For comparison: https://stackoverflow.com/questions/48727863/vue-export-default-vs-new-vue
const app = {
  name: "app",
  components: {
    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    Login,
    Register,
    UserProfile,
    Students
  },
  methods: {
    /**
     * Logs out the current user and redirects to the login page.
     * Currently does nothing with managing cookies, this needs to be implemented later.
     */
    logOut() {
      this.$router.push('/login');
    }
  },
  // app initial state
  // https://vuejs.org/v2/guide/instance.html#Data-and-Methods
  data: () => {
    return {};
  },
};

// make the 'app' available
export default app;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>