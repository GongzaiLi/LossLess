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
import Api from "../Api.js";

const instance = new Vue({
    data: { $currentUser: null }    // We store the $currentUser in a vue instance to make it reactive
});

/**
 * Given a key, returns the corresponding object stored with that key in localstorage
 * @param key The key to look up in localstorage
 * @returns {any|null} The JSON parsed object stored with the given key, or null if not found
 */
export const getFromLocalStorage = function(key) {
    const storedJSON = localStorage.getItem(key);
    return (storedJSON) ? JSON.parse(storedJSON) : null;
}

/**
 * Given a user object and a business id, returns the business object that the id refers to
 * @param user A user which should administer the business with the given id
 * @param businessId The id of the business to be returned
 * @returns {null|*} The business object that the id refers to, or null if the user does not administer a business with the given id
 */
export const getBusinessActingAs = function(user, businessId) {
    for (const business of user.businessesAdministered) {
        if (businessId === business.id) {
            return business;
        }
    }
    return null;
}
/**
 * Initializes the Auth plugin. MAKE SURE YOU CALL THIS BEFORE CREATING THE VUE APP!
 * This will attempt to retrieve the user id from local storage, then query the api for that user data.
 * This allows us to persist user sessions even when the user hard refreshes the page.
 */
export const initializeAuth = async function() {
    const userId = getFromLocalStorage('currentUserId');
    if (userId) {
        try {
            Vue.set(instance.$data, '$currentUser', (await Api.getUser(userId)).data);
        } catch (e) {
            // We were probably logged out, so just leave $currentUser as null
            instance.$data.$currentUser = null;
            return;
        }
        const actingAsId = getFromLocalStorage('currentlyActingAsId');
        if (actingAsId) {
            const actingAsObject = getBusinessActingAs(instance.$data.$currentUser, actingAsId)
            setCurrentlyActingAs(actingAsObject);
        } else {
            setCurrentlyActingAs(null);
        }
    }
}

export const getCurrentUser = function () {
    return instance.$data.$currentUser;
}

/**
 * Sets the 'currentlyActingAs' field of $currentUser.
 * Use this method instead of $currentUser=foo, as only this method
 * will properly trigger Vue to update the value of $currentUser
 */
export const setCurrentlyActingAs = function (actingAs) {
    Vue.set(instance.$data.$currentUser, 'currentlyActingAs', actingAs);
    localStorage.setItem('currentlyActingAsId', actingAs ? JSON.stringify(actingAs.id) : null);
};

//this is a plugin not a mixin so use Vue.use();
export default function install(Vue) {
    Vue.mixin({
        computed: {
            $currentUser: {
                /**
                 * Getter for the current user.
                 */
                get: function () {
                    return instance.$data.$currentUser;
                },
                /**
                 * Setter for the current user. It will update the current user variable, which is a reactive dependency
                 * for this computed property so will cause any components using this property to refresh. It will
                 * also store the id of the new value (or null if the value is null) in localstorage.
                 * This is so the next time the app is started we can retrieved the user data from localstorage.
                 * Otherwise the user will be logged out whenever they hard refresh.
                 */
                set: function (newUser) {
                    localStorage.setItem('currentUserId', newUser ? JSON.stringify(newUser.id) : null);
                    instance.$data.$currentUser = newUser;
                },
            },
        },
    });
}
