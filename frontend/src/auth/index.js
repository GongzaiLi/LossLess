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
import Vue from 'vue'


const instance = new Vue({
    data: { $currentUser: null }    // We store the $currentUser in a vue instance to make it reactive
});

/**
 * Attempts to retrieve the current user from localstorage. A null value
 * signifies that no current user is logged in.
 */
export const getUser = function () {
    const storedJSON = localStorage.getItem('currentUser');
    return (storedJSON) ? JSON.parse(storedJSON) : null;
};

/**
 * Sets the 'currentlyActingAs' field of $currentUser.
 * Use this method instead of $currentUser=foo, as only this method
 * will properly trigger Vue to update the value of $currentUser
 */
export const setCurrentlyActingAs = function (actingAs) {
    instance.$set(instance.$data.$currentUser, 'currentlyActingAs', actingAs);
    localStorage.setItem('currentUser', JSON.stringify(instance.$data.$currentUser));
};

/**
 * Gets the 'currentlyActingAs' field of the current user from localstorage.
 */
export const getCurrentlyActingAs = function () {
    const user = getUser();
    return (user) ? user.currentlyActingAs : null;
};

//this is a plugin not a mixin so use Vue.use();
export default function install(Vue) {
    Vue.mixin({
        computed: {
            $currentUser: {
                /**
                 * Getter for the current user. It will attempt to retrieve the user from local storage
                 * if the current user variable is null - this persists the user data even after the user refreshes.
                 * If the current user has already been retrieved it will not access local storage for performance reasons
                 */
                get: function () {
                    if (instance.$data.$currentUser === null) {
                        instance.$data.$currentUser = getUser();
                    }
                    return instance.$data.$currentUser;
                },
                /**
                 * Setter for the current user. It will update the current user variable, which is a reactive dependency
                 * for this computed property so will cause any components using this property to refresh. It will
                 * also store the new value in localstorage so it will persist even if the user refreshes.
                 */
                set: function (newUser) {
                    localStorage.setItem('currentUser', JSON.stringify(newUser));
                    instance.$data.$currentUser = newUser;
                },
            },
        },
    });
}
