<template>
  <div id="app">
    <Navbar v-if="!['login', 'register'].includes($route.name)"/>
    <!-- The keep-alive prevents the SearchPage component from being re-rendered each time, so you can got back to your search results -->
    <keep-alive v-if="$currentUser" include="SearchPage,ListingsSearchPage,HomePage"> <!-- The v-if means that when the currentUser is null (ie logged out) the keep-alive gets destroyed, so the cached components are cleared -->
      <router-view/>
    </keep-alive>
    <router-view v-else/>
  </div>
</template>

<script>
import Login from "./components/user/Login";
import UserDetailsModal from "./components/user/UserDetailsModal";
import Navbar from "./components/model/Navbar";
import UserProfile from "./components/user/UserProfile";
import SearchPage from "./components/search/SearchPage";
import HomePage from "./components/user/HomePage";
import CreateEditBusiness from "./components/business/CreateEditBusiness";
import BusinessProfile from "./components/business/BusinessProfile";
import InventoryPage from "./components/inventory/InventoryPage";
import ListingSearchPage from "./components/listing/ListingSearchPage";
import SalesReportPage from "./components/sales-report/SalesReportPage";

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
    UserDetailsModal,
    UserProfile,
    Navbar,
    HomePage,
    CreateEditBusiness,
    SearchPage,
    BusinessProfile,
    InventoryPage,
    ListingSearchPage,
    SalesReportPage

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

body {
  max-width: 1260px !important;
}

.card {
  max-width: 850px;
  margin-left: auto;
  margin-right: auto;
}
</style>
