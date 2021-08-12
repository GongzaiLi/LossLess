import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/user/Login.vue'
import Register from '../components/user/Register.vue'
import UserProfile from '../components/user/UserProfile'
import SearchPage from "../components/search/SearchPage";
import BusinessProfile from "@/components/business/BusinessProfile";
import ProductCatalogue from "@/components/product/ProductCatalogue";
import CreateBusiness from "../components/business/CreateBusiness";
import ListingsPage from "../components/listing/ListingsPage";
import HomePage from "@/components/user/HomePage";
import InventoryPage from "@/components/inventory/InventoryPage";
import Marketplace from "@/components/marketplace/Marketplace";
import ListingSearchPage from "../components/listing/ListingSearchPage";
import ListingFullPage from "@/components/listing/ListingFullPage";



import {getCurrentUser} from '@/auth';

/**
 * This specifies all routing information used by Vue-Router.
 */

Vue.use(Router);

const router = new Router({
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', name: 'login', component: Login },
        { path: '/register', name: 'register', component: Register },
        { path: '/search', name: 'search', component: SearchPage},
        { path: '/users/:id', name: 'user-profile', component: UserProfile},
        { path: '/homePage', name: 'home-page', component: HomePage},
        { path: '/businesses/:id', name: 'business-profile', component: BusinessProfile},
        { path: '/businesses/:id/listings', name: 'listings-page', component: ListingsPage},
        { path: '/businesses/:id/products', name: 'product-catalogue', component: ProductCatalogue},
        { path: '/businesses', name: 'create-business', component: CreateBusiness},
        { path: '/businesses/:id/inventory', name:'inventory-page', component: InventoryPage},
        { path: '/marketPlace', name: 'market-place', component: Marketplace},
        { path : '/listingSearch', name: 'listings-search', component: ListingSearchPage},
        { path : '/listing/:id', name: 'listings-full', component: ListingFullPage}

    ]
});

/**
 * Route guard that redirects users to the login page if they are not authenticated.
 * This applies to all routes except for the login and register routes. 
 */
router.beforeEach((to, _from, next) => {
    const currentUser = getCurrentUser();
    if (!['login', 'register'].includes(to.name) && currentUser == null) {
        next('/login');
    } else if (to.name === 'market-place' && currentUser.currentlyActingAs) {
        next('/homePage');
    } else {
        next();
    }
});

export default router;
