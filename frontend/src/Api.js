/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Declare all available services here
 */
import axios from 'axios'
import {getCurrentlyActingAs} from './auth'


const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const businessActingAs = getCurrentlyActingAs();
let businessActingAsId = businessActingAs ? businessActingAs.id : null;

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000,
  headers: {'X-Business-Acting-As': businessActingAsId}
});

let currencyCache = {};

export default {
  login: (loginData) => instance.post('login', loginData, {withCredentials: true}),
  register: (registerData) => instance.post('users', registerData, {withCredentials: true}),
  getUser: (id) => instance.get(`users/${id}`, {withCredentials: true}),
  makeUserAdmin: (id) => instance.put(`users/${id}/makeAdmin`, null, {withCredentials: true}),
  revokeUserAdmin: (id) => instance.put(`users/${id}/revokeAdmin`, null, {withCredentials: true}),
  searchUser: (searchParameter) => instance.get(`users/search?searchQuery=${searchParameter}`, {withCredentials: true}),
  getBusiness: (id) => instance.get(`/businesses/${id}`, {withCredentials: true}),
  getProducts: (id) => instance.get(`/businesses/${id}/products`, {withCredentials: true}),
  postBusiness: (businessData) => instance.post('businesses', businessData, {withCredentials: true}),
  setBusinessActingAs: (businessId) => businessActingAsId = businessId,
  makeBusinessAdmin: (id, makeAdminData) => instance.put(`/businesses/${id}/makeAdministrator`, makeAdminData, {withCredentials: true}),
  revokeBusinessAdmin: (id, revokeAdminData) => instance.put(`/businesses/${id}/removeAdministrator`, revokeAdminData, {withCredentials: true}),
  createProduct: (id,productData) => instance.post(`/businesses/${id}/products`,productData, {withCredentials: true}),
  modifyProduct: (businessId, productId, editProductData) => instance.put(`/businesses/${businessId}/products/${productId}`, editProductData, {withCredentials:true}),
  createInventory: (id,inventoryData) => instance.post(`/businesses/${id}/inventory`, inventoryData, {withCredentials: true}),
  getInventory: (id) => instance.get(`/businesses/${id}/inventory`, {withCredentials: true}),

  /**
   * Given the name of the user's country, gets currency data for that country.
   * Uses the restcountries API. Currency data is a JS object in the format:
   * {"code":"<code>","name":"<name>","symbol":"<symbol>"}.
   * Returns null if there are no results for the user's country.
   * @param countryName Name of the country to be queried
   * @returns {Promise<any>} Promise that resolves to the currency data object
   */
  getUserCurrency: (countryName) => {
    if (countryName in currencyCache) {
      return Promise.resolve(currencyCache[countryName]);
    }

    return fetch(`https://restcountries.eu/rest/v2/name/${encodeURIComponent(countryName)}?fields=currencies`)
      .then(resp => resp.json())
      .then(data => {
        if (data.status === 404 || !data[0].currencies || data[0].currencies.length === 0) {
          return null;
        }
        const currency = data[0].currencies[0];
        if (!currency.code || !currency.name || !currency.symbol) { // Sometimes we get garbage data like {"code":"(none)","name":null,"symbol":null}
          return null;
        } else {
          currencyCache[countryName] = currency;
          return currency;
        }
      })
  }
}

