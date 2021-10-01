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
import {formatDate} from "./util";

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
  searchUser: (searchParameter, count = 10, offset = 0, sortBy = "NAME", sortDirection = "ASC") => instance.get(`users/search?searchQuery=${searchParameter}&count=${count}&offset=${offset}&sortBy=${sortBy}&sortDirection=${sortDirection}`, {withCredentials: true}),
  getBusiness: (id) => instance.get(`/businesses/${id}`, {withCredentials: true}),
  getProducts: (id, count, offset, sortBy = "ID", sortDirection = "ASC", searchQuery = "") => instance.get(`/businesses/${id}/products?count=${count}&offset=${offset}&sortBy=${sortBy}&sortDirection=${sortDirection}&searchQuery=${searchQuery}`, {withCredentials: true}),
  postBusiness: (businessData) => instance.post('/businesses', businessData, {withCredentials: true}),
  modifyBusiness: (editBusinessData, businessId) => instance.put(`/businesses/${businessId}`, editBusinessData, {withCredentials: true}),
  makeBusinessAdmin: (id, makeAdminData) => instance.put(`/businesses/${id}/makeAdministrator`, makeAdminData, {withCredentials: true}),
  revokeBusinessAdmin: (id, revokeAdminData) => instance.put(`/businesses/${id}/removeAdministrator`, revokeAdminData, {withCredentials: true}),
  createProduct: (id, productData) => instance.post(`/businesses/${id}/products`, productData, {withCredentials: true}),
  modifyProduct: (businessId, productId, editProductData) => instance.put(`/businesses/${businessId}/products/${productId}`, editProductData, {withCredentials: true}),
  createInventory: (id, inventoryData) => instance.post(`/businesses/${id}/inventory`, inventoryData, {withCredentials: true}),
  getInventory: (id, count = 10, offset = 0, sortBy = "id", sortDirection = "ASC", searchQuery = "") => instance.get(`/businesses/${id}/inventory?sort=${sortBy},${sortDirection}&page=${offset}&size=${count}&searchQuery=${searchQuery}`, {withCredentials: true}),
  modifyInventory: (businessId, inventoryId, editInventoryData) => instance.put(`/businesses/${businessId}/inventory/${inventoryId}`, editInventoryData, {withCredentials: true}),
  createListing: (businessId, listing) => instance.post(`businesses/${businessId}/listings`, listing, {withCredentials: true}),
  getListings: (businessId, count, offset, sortBy, sortDirection) => instance.get(`/businesses/${businessId}/listings?size=${count}&page=${offset}&sort=${sortBy},${sortDirection}`, {withCredentials: true}),
  purchaseListing: (listingId) => instance.post(`/listings/${listingId}/purchase`, null, {withCredentials: true}),
  getListing: (listingId) => instance.get(`/listings/${listingId}`, {withCredentials: true}),
  getPurchaseListing: (id) => instance.get(`/purchase/${id}`, {withCredentials: true}),
  getImage: (imageName) => {
    return `${SERVER_URL}/images?filename=${imageName}`
  },
  deleteImage: (businessId, productId, imageId) => instance.delete(`/businesses/${businessId}/products/${productId}/images/${imageId}`, {withCredentials: true}),
  setPrimaryImage: (businessId, productId, imageId) => instance.put(`/businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`, null, {withCredentials: true}),
  createCard: (cardData) => instance.post("/cards", cardData, {withCredentials: true}),
  getCardsBySection: (section, currentPage, perPage, sortBy, sortOrder) => {
    let query = `/cards?section=${section}&page=${currentPage}&size=${perPage}`;
    if (sortBy === "location") {
      query += `&sort=creator.homeAddress.city,${sortOrder}&sort=creator.homeAddress.suburb,${sortOrder}`;
    } else {
      query += `&sort=${sortBy},${sortOrder}`;
    }
    query += "&sort=id";
    return instance.get(query, {withCredentials: true});
  },
  getFullCard: (cardId) => instance.get(`/cards/${cardId}`, {withCredentials: true}),
  deleteCard: (cardId) => instance.delete(`/cards/${cardId}`, {withCredentials: true}),
  getExpiredCards: (id) => instance.get(`/cards/${id}/expiring`, {withCredentials: true}),
  getNotifications: (tags, archived=false) => instance.get(`/users/notifications?archived=${archived}${tags != null ? `&tags=${tags}`: ''}`, {withCredentials: true}),
  patchNotification: (id, data) => instance.patch(`/notifications/${id}`, data, {withCredentials: true}),
  deleteNotification: (id) => instance.delete(`/notifications/${id}`, {withCredentials: true}),
  clearHasCardsExpired: (userId) => instance.put(`/users/${userId}/clearHasCardsExpired`, null, {withCredentials: true}),
  extendCardExpiry: (id) => instance.put(`/cards/${id}/extenddisplayperiod`, {}, {withCredentials: true}),
  searchBusiness: (searchParameter, type = "", size = 10, page = 0, sortBy = "name", sortDirection = "ASC") => instance.get(`businesses/search?searchQuery=${searchParameter}&size=${size}&page=${page}&sort=${sortBy},${sortDirection}&type=${type}`, {withCredentials: true}),
  searchListings: (searchQuery, priceLower, priceUpper, businessName, businessTypes, businessLocation, closingDateStart, closingDateEnd, sort, size = 10, page = 0) => {
    const params = new URLSearchParams({
      searchQuery: searchQuery,
      priceLower: priceLower,
      priceUpper: priceUpper,
      businessName: businessName,
      closingDateStart: closingDateStart,
      closingDateEnd: closingDateEnd,
      address: businessLocation,
      size: size,
      page: page
    });

    for (const sortOrder of sort) {
      params.append("sort", sortOrder);
    }
    params.append("sort", "id,asc");

    for (const type of businessTypes) {
      params.append("businessTypes", type);
    }
    return instance.get(`/listings/search`,
      {
        withCredentials: true,
        params: params,
      })
  },
  likeListing: (listingId) => instance.put(`/listings/${listingId}/like`, {}, {withCredentials: true}),
  modifyUser: (editUserData, userId) => instance.put(`/users/${userId}`, editUserData, {withCredentials: true}),
  deleteUserProfileImage: (userId) => instance.delete(`/users/${userId}/image`, {withCredentials: true}),
  getSalesReport: (businessId, startDate, endDate, period) => instance.get(`/businesses/${businessId}/salesReport/totalPurchases`,
    {
      withCredentials: true,
      params: {
        startDate: startDate,
        endDate: endDate,
        period: period
      }
    }),

  getProductsReport: (businessId, startDate, endDate, sortBy, order) => instance.get(`/businesses/${businessId}/salesReport/productsPurchasedTotals`,
      {
        withCredentials: true,
        params: {
          startDate: startDate,
          endDate: endDate,
          sortBy: sortBy,
          order: order,
          size: 100000 // For some reason pagination defaults to a size of 20, but we want all results
        }
      }),

  getManufacturersReport: (businessId, startDate, endDate, sortBy, order) => instance.get(`/businesses/${businessId}/salesReport/manufacturersPurchasedTotals`,
      {
        withCredentials: true,
        params: {
          startDate: startDate,
          endDate: endDate,
          sortBy: sortBy,
          order: order,
          size: 100000
        }
      }),

  getListingDurations: (businessId, granularity, startDate, endDate) => instance.get(`/businesses/${businessId}/salesReport/listingDurations`,
    {
      withCredentials: true,
      params: {
        granularity,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      }
    }),
  postMessage: (message) => instance.post(`/messages`, message,{withCredentials: true}),
  getMessages: (cardId) => instance.get(`/messages/${cardId}`, {withCredentials: true}),


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
   * Uploads one image file to a user's Profile. Will send a POST request to the user image
   * endpoint. Each image is sent as multipart/form-data with the param name "file".
   * @param userId Id of the user
   * @param imageFile Image file object to be uploaded.
   */
  uploadProfileImage: (userId, imageFile) => {
    // See https://github.com/axios/axios/issues/710 for how this works
    let formData = new FormData();
    formData.append("filename", new Blob([imageFile], {type: `${imageFile.type}`}));
    return instance.post(`/users/${userId}/image`, formData, {withCredentials: true});
  },

  /**
   * Uploads one image file to a business's Profile. Will send a POST request to the business image
   * endpoint. Each image is sent as multipart/form-data with the param name "file".
   * @param businessId Id of the business
   * @param imageFile Image file object to be uploaded.
   */
  uploadBusinessProfileImage: (businessId, imageFile) => {
    // See https://github.com/axios/axios/issues/710 for how this works
    let formData = new FormData();
    formData.append("filename", new Blob([imageFile], {type: `${imageFile.type}`}));
    return instance.post(`/businesses/${businessId}/image`, formData, {withCredentials: true});
  },

  deleteBusinessProfileImage: (businessId) => instance.delete(`/businesses/${businessId}/image`, {withCredentials: true}),


  getSalesReportCsv: (businessId) => instance.get(`/businesses/${businessId}/salesReport/csv`, {withCredentials: true, responseType: 'blob' }),

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

    const controller = new AbortController();  // see https://stackoverflow.com/questions/46946380/fetch-api-request-timeout
    setTimeout(() => controller.abort(), 2000);
    
    return fetch(`https://restcountries.com/v2/name/${encodeURIComponent(countryName)}?fields=currencies`, { signal: controller.signal })
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
      .catch(() => {
        return {
          symbol: '$',
          code: 'NZD',
          name: 'New Zealand Dollar'
        }
      })
  }
}

