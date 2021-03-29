/**
 * Author: Caleb, Eric
 * below code adapted from stack overflow (link included)
 * creates a global variable by creating another instance of vue
 * mixin makes updates to this variable availble to all Vue instances and components
 * The mixin only makes the data available within Vue components, but we still need
 * to access get and set User in other modules (eg. router, tests). Thus getUser and setCurrentUser
 * is needed as exports
https://stackoverflow.com/questions/49256765/change-vue-prototype-variable-in-all-components
 **/

/**
 * Attempts to retrieve the current user from localstorage. A null value
 * signifies that no current user is logged in.
 */
export const getUser = function () {
    return JSON.parse(localStorage.getItem('currentUser'));
};

/**
 * Attempts to set the current user to localstorage.
 */
export const setCurrentUser = function (newUser) {
    localStorage.setItem('currentUser', JSON.stringify(newUser));
}

//this is a plugin not a mixin so use Vue.use();
export default function install(Vue) {
    Vue.mixin({
        methods: {
            $getCurrentUser: getUser,
            $setCurrentUser: setCurrentUser,
        }
    });
}
