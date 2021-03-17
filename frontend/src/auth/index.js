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

/**
 * Attempts to retrieve the current user from localstorage. A null value
 * signifies that no current user is logged in.
 */
export const getUser = function () {
    return JSON.parse(localStorage.getItem('currentUser'));
};

export default {
    install(Vue) {
        Vue.mixin({
            computed: {
                $currentUser: {
                    get: getUser,
                    set: function (newUser) {
                        localStorage.setItem('currentUser', JSON.stringify(newUser));
                    }
                }
            }
        });
    }
};
