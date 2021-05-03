<template>
  <div id="app">
    <Navbar/>
    <keep-alive include="UserSearch"> <!-- The keep-alive prevents the UserSearch component from being re-rendered each time, so you can got back to your search results -->
      <router-view/>
    </keep-alive>
  </div>
</template>

<script>
import Login from "./components/user/Login";
import Register from "./components/user/Register";
import Navbar from "./components/model/Navbar";
import UserProfile from "./components/user/UserProfile";
import UserSearch from "./components/user/UserSearch";
import HomePage from "@/components/user/HomePage";
import CreateBusiness from "./components/business/CreateBusiness";
import BusinessProfile from "./components/business/BusinessProfile";

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
    Navbar,
    HomePage,
    CreateBusiness,
    UserSearch,
    BusinessProfile
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
