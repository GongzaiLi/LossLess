import Vue from 'vue'
import Router from 'vue-router'
import Login from './../components/Login.vue'
import Register from './../components/Register.vue'
//import Students from './../components/Students.vue'

Vue.use(Router)

export default new Router({
    routes: [
        { path: '/login', component: Login },
        { path: '/', redirect: '/login' },
        { path: '/register', component: Register },
    ]
})