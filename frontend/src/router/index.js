import Vue from 'vue'
import Router from 'vue-router'
import Login from './../components/Login.vue'
import Register from './../components/Register.vue'
import UserProfile from './../components/UserProfile'
import UserSearch from "../components/UserSearch";
import BusinessProfile from "@/components/BusinessProfile";
import ProductCatalogue from "@/components/ProductCatalogue";
//import {getUser} from '@/auth'

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
        { path: '/businesses/:id', name: 'business-profile', component: BusinessProfile},
        { path: '/businesses/:id/products', name: 'product-catalogue', component: ProductCatalogue}
    ]
});

/**
 * Route guard that redirects users to the login page if they are not authenticated.
 * This applies to all routes except for the login and register routers.

router.beforeEach((to, _from, next) => {
    if (!['login', 'register'].includes(to.name) && getUser() == null) {
        next('/login');
    } else {
        next();
    }
});*/

export default router;