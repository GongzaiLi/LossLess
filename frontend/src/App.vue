<template>
  <b-container id="app">
    <Navbar/>
    <router-view/>
  </b-container>
</template>

<script>
import Login from "./components/Login";
import Register from "./components/Register";
import Navbar from "./components/Navbar";
import UserProfile from "./components/UserProfile";
import UserSearch from "./components/UserSearch";
import HomePage from "@/components/HomePage";

// Vue app instance
// it is declared as a reusable component in this case.
// For global instance https://vuejs.org/v2/guide/instance.html
// For comparison: https://stackoverflow.com/questions/48727863/vue-export-default-vs-new-vue
const app = {
  name: "app",
  components: {
    HomePage,
    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    Login,
    Register,
    UserProfile,
    Navbar,
    UserSearch
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