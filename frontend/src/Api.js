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

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 500000,
});

let currencyCache = {};

export default {
  login: (loginData) => instance.post('login', loginData, {withCredentials: true}),
  register: (registerData) => instance.post('users', registerData, {withCredentials: true}),
  getUser: (id) => instance.get(`users/${id}`, {withCredentials: true}),
  makeUserAdmin: (id) => instance.put(`users/${id}/makeAdmin`, null, {withCredentials: true}),
  revokeUserAdmin: (id) => instance.put(`users/${id}/revokeAdmin`, null, {withCredentials: true}),
  searchUser: (searchParameter, count=10, offset=0, sortBy="NAME", sortDirection="ASC") => instance.get(`users/search?searchQuery=${searchParameter}&count=${count}&offset=${offset}&sortBy=${sortBy}&sortDirection=${sortDirection}`, {withCredentials: true}),
  getBusiness: (id) => instance.get(`/businesses/${id}`, {withCredentials: true}),
  getProducts: (id) => instance.get(`/businesses/${id}/products`, {withCredentials: true}),
  postBusiness: (businessData) => instance.post('/businesses', businessData, {withCredentials: true}),
  makeBusinessAdmin: (id, makeAdminData) => instance.put(`/businesses/${id}/makeAdministrator`, makeAdminData, {withCredentials: true}),
  revokeBusinessAdmin: (id, revokeAdminData) => instance.put(`/businesses/${id}/removeAdministrator`, revokeAdminData, {withCredentials: true}),
  createProduct: (id, productData) => instance.post(`/businesses/${id}/products`, productData, {withCredentials: true}),
  modifyProduct: (businessId, productId, editProductData) => instance.put(`/businesses/${businessId}/products/${productId}`, editProductData, {withCredentials: true}),
  createInventory: (id, inventoryData) => instance.post(`/businesses/${id}/inventory`, inventoryData, {withCredentials: true}),
  getInventory: (id) => instance.get(`/businesses/${id}/inventory`, {withCredentials: true}),
  modifyInventory: (businessId, inventoryId, editInventoryData) => instance.put(`/businesses/${businessId}/inventory/${inventoryId}`, editInventoryData, {withCredentials: true}),
  createListing: (businessId, listing) => instance.post(`businesses/${businessId}/listings`, listing, {withCredentials:true}),
  getListings: (businessId) => instance.get(`/businesses/${businessId}/listings`, {withCredentials: true}),
  getImage: (imageName) => {return `${SERVER_URL}/images?filename=${imageName}`},
  deleteImage: (businessId, productId, imageId) => instance.delete(`/businesses/${businessId}/products/${productId}/images/${imageId}`, {withCredentials: true}),
  setPrimaryImage: (businessId, productId, imageId) => instance.put(`/businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`, null,{withCredentials: true}),
  getCardsBySection: (section) => instance.get(`/cards?section=${section}`, {withCredentials: true}),
  getFullCard: (cardId) => instance.get(`/cards/${cardId}`, {withCredentials: true}),
  getExpiringCards: (id) => instance.get(`/cards/${id}/expiring`, {withCredentials: true}),

  /**
   * Uploads one image file to a product. Will send a POST request to the product images
   * endpoint. Each image is sent as multipart/form-data with the param name "file".
   * @param businessId Id of the business that owns the product
   * @param productId Id of existing product images are to be uploaded for
   * @param imageFile Image file object to be uploaded.
   */
  uploadProductImage: (businessId, productId, imageFile) => {
    // See https://github.com/axios/axios/issues/710 for how this works
    let formData = new FormData();
    formData.append("filename", new Blob([imageFile], {type: `${imageFile.type}`}));
    return instance.post(`/businesses/${businessId}/products/${productId}/images`, formData, {withCredentials: true});
  },

  /**
   * Given the name of the user's country, gets currency data for that country.
   * Uses the restcountries API. Currency data is a JS object in the format:
   * {"code":"<code>","name":"<name>","symbol":"<symbol>"}.
   * Defaults to data for USD if there are no results for the user's country.
   * @param countryName Name of the country to be queried
   * @returns {Promise<any>} Promise that resolves to the currency data object
   */
  getUserCurrency: (countryName) => {
    if (countryName in currencyCache) {
      return Promise.resolve(currencyCache[countryName]);
    }

    const USD = {
      symbol: '$',
      code: 'USD',
      name: 'United States Dollar'
    };

    return fetch(`https://restcountries.eu/rest/v2/name/${encodeURIComponent(countryName)}?fields=currencies`)
      .then(resp => resp.json())
      .then(data => {
        if (data.status === 404 || !data[0].currencies || data[0].currencies.length === 0) {
          return USD;
        }
        const currency = data[0].currencies[0];
        if (!currency.code || !currency.name || !currency.symbol) { // Sometimes we get garbage data like {"code":"(none)","name":null,"symbol":null}
          return USD;
        } else {
          currencyCache[countryName] = currency;
          return currency;
        }
      })
  }
}

