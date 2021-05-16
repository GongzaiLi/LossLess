import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/user/Login.vue'
import Register from '../components/user/Register.vue'
import UserProfile from '../components/user/UserProfile'
import UserSearch from "../components/user/UserSearch";
import BusinessProfile from "@/components/business/BusinessProfile";
import ProductCatalogue from "@/components/product/ProductCatalogue";
import CreateBusiness from "../components/business/CreateBusiness";
import BusinessListings from "../components/business/BusinessListings";
import HomePage from "@/components/user/HomePage";
import InventoryPage from "@/components/inventory/InventoryPage";

import {getUser} from '@/auth'

/**
 * This specifies all routing information used by Vue-Router.
 */

Vue.use(Router);

const router = new Router({
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', name: 'login', component: Login },
        { path: '/register', name: 'register', component: Register },
        { path: '/users/search', name: 'user-search', component: UserSearch},
        { path: '/users/:id', name: 'user-profile', component: UserProfile},
        { path: '/homePage', name: 'home-page', component: HomePage},
        { path: '/businesses/:id', name: 'business-profile', component: BusinessProfile},
        { path: '/businesses/:id/listings', name: 'business-listings', component: BusinessListings},
        { path: '/businesses/:id/products', name: 'product-catalogue', component: ProductCatalogue},
        { path: '/businesses', name: 'create-business', component: CreateBusiness},
        { path: '/businesses/:id/inventory', name:'inventory-page', component: InventoryPage}
    ]
});

/**
 * Route guard that redirects users to the login page if they are not authenticated.
 * This applies to all routes except for the login and register routes. 
 */
router.beforeEach((to, _from, next) => {
    console.log(to);
    const currentUser = getUser();
    if (!['login', 'register'].includes(to.name) && currentUser == null) {
        next('/login');
    } else {
        next();
    }
});

export default router;
