import Vue from 'vue'

/**
 * Author: Caleb, Eric
 * below code adapted from stack overflow (link included)
 * creates a global variable by creating another instance of vue
 * mixin makes updates to this variable availble to all Vue instances and components
 * getInstance() method is needed so that the router can access the 'global' data. 
 * The mixin only makes the data available within Vue components, but the router is not 
 * a real component. Thus getInstance() is needed to get the Vue instance with the data.
https://stackoverflow.com/questions/49256765/change-vue-prototype-variable-in-all-components
 **/

const instance = new Vue({
    data: { $currentUser: null }
});

export const getInstance = () => instance;

export default {
    install(Vue) {
        Vue.mixin({
            computed: {
                $currentUser: {
                    get: function () { return instance.$data.$currentUser },
                    set: function (newUser) { instance.$data.$currentUser = newUser; }
                }
            }
        });
    }
};
