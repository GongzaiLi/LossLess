import Vue from 'vue'
import Router from 'vue-router'
import Login from './../components/Login.vue'
import Register from './../components/Register.vue'
import UserProfile from './../components/UserProfile'
//import Students from './../components/Students.vue'

/**
 * This specifies all routing information used by Vue-Router.
 */

Vue.use(Router)

export default new Router({
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', component: Login },
        { path: '/register', component: Register },
        { path: '/user/:id', component: UserProfile},
    ]
})