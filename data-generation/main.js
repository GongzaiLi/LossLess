/**
 * This script is responsible for generating 10000 random users and
 * registering them with a running backend. If it has not been run before
 * it depends on the https://randomuser.me Api to get random data.
 * Otherwise, it will re-used the preivously generated data (which will be saved
 * in a users.json file)
 */

const fs = require('fs')
const Axios = require('axios');
const FormData = require('form-data');


const NUM_USERS = 10000;
const MAX_GENERATED_USERS_PER_REQUEST = 5000;
const MAX_USERS_PER_API_REQUEST = 96;
const HAS_NICKNAME_PROB = 1 / 10;
const HAS_MIDDLE_NAME_PROB = 4 / 10;
const PROB_USER_LIKES_LISTINGS = 0.5;
const MAX_LIKED_LISTINGS_PER_USER = 10;

const NUM_BUSINESSES = 500;
const NUM_BUSINESSTYPES = 4;
const MAX_BUSSINESSES_PER_USER = 3;
const MAX_PRODUCTS_PER_BUSINESS = 40;
const MIN_PRODUCTS_PER_BUSINESS = 10;
const MAX_PRODUCT_PRICE = 50;
const MAX_QUANTITY_PRODUCT_IN_INVENTORY = 100;
const MIN_QUANTITY_PRODUCT_IN_INVENTORY = 1;
const CHANCE_OF_INVENTORY_FOR_PRODUCT = 0.8;
const MIN_QUANTITY_INVENTORY_IN_LISTING = 1;
const MAX_CARD_PER_USER = 5;
const APPROX_NUM_LISTINGS = NUM_BUSINESSES * ((MAX_PRODUCTS_PER_BUSINESS + MIN_PRODUCTS_PER_BUSINESS) / 2) * CHANCE_OF_INVENTORY_FOR_PRODUCT * 0.8;  // Fudge factor
const LISTING_ID_OF_MAX_LIKED_LISTING = 1;
const MAX_PURCHASES_PER_USER = 3;
const PROB_USER_PURCHASES_LISTING = 0.01;
const NUMBER_OF_USER_IMAGES = 15;
const NUMBER_OF_BUSINESS_IMAGES = 18;

const userBios = require('./bios.json')
const businessNames = require('./businessNames.json')
const businessTypes = require('./businessTypes.json')
const productNames = require('./productNames.json')
const cardDes = require('./cardDescription.json')

// const SERVER_URL = "https://csse-s302g7.canterbury.ac.nz/test/api";
// const SERVER_URL = "https://csse-s302g7.canterbury.ac.nz/prod/api";
const SERVER_URL = "http://localhost:9499";

/**
 * Uses the https://randomuser.me Api to get 10000 randomly generated users.
 * The data will be in their format so it must be first converted to our SENG302
 * API format
 */
async function getApiRandomUserInfo() {
  let users = [];
  for (let i = 0; i < NUM_USERS / MAX_GENERATED_USERS_PER_REQUEST; i++) {
    let usersBatch = (await Axios
      .get(`https://randomuser.me//api/?results=${MAX_GENERATED_USERS_PER_REQUEST}`))
      .data.results;
    console.log(`Got ${(i + 1) * MAX_GENERATED_USERS_PER_REQUEST} / ${NUM_USERS} users from API`)
    users = users.concat(usersBatch);
  }
  return users;
}

/**
 * Given a randomly generated user from https://randomuser.me/, returns the address of the user in our
 * SENG302 API format
 */
function getAddressFromApiUser(user) {
  return {
    streetNumber: user.location.street.number,
    streetName: user.location.street.name,
    city: user.location.city,
    region: user.location.state,
    postcode: user.location.postcode,
    country: user.location.country,
  };
}

/**
 * For all user objects in the given list, ensures that all email fields will be unique.
 * This is done by prepending a unique number to duplicate emails.
 * This is needed as sometimes the https://randomuser.me/ Api will give users with duplicate emails
 */
function makeEmailsUnique(users) {
  let emails = new Set();
  let id = 0;

  for (let user of users) {
    if (emails.has(user.email)) {
      user.email = id + user.email;
      id += 1;
    }
    emails.add(user.email);
  }
}

/**
 * Gets list of random user data from https://randomuser.me/, and returns a list of users
 * converted into the SENG302 API format
 */
async function getUsers() {
  console.log('Getting random user info from Api...');
  const apiUsers = await getApiRandomUserInfo(); // Sonarlint says that the await is redundant. Sonarlint is stupid.
  console.log('Got all random users from Api.');

  const names = []; // Collect all names for middle name generation later
  const users = []

  for (let user of apiUsers) {
    names.push(user.name.first);
    names.push(user.name.last);

    users.push({
      firstName: user.name.first,
      lastName: user.name.last,
      nickname: (Math.random() < HAS_NICKNAME_PROB) ? user.login.username.replace(/[0-9]/g, '') : undefined,
      bio: userBios[Math.floor(Math.random() * userBios.length)],
      email: user.email.replace('\'', '').replace('..', '.'),  // some data comes back malformed with ' (single quote) characters or .. (double dots), so we remove them here
      dateOfBirth: user.dob.date,
      phoneNumber: user.phone,
      password: user.login.password,
      homeAddress: getAddressFromApiUser(user)
    })
  }

  // Add middle names
  for (let user of users) {
    if (Math.random() < HAS_MIDDLE_NAME_PROB) {
      user.middleName = names[Math.floor(Math.random() * names.length)];
    }
  }

  makeEmailsUnique(users);

  fs.writeFile("users.json", JSON.stringify(users), () => {
    console.log('Finished writing users');
  });

  return users;
}

/**
 * Creates list of random business data from the given files, and returns a list of businesses
 * converted into the SENG302 API format
 */
async function getBusinesses() {

  const businesses = []

  for (let i = 0; i < NUM_BUSINESSES; i++) {

    const businessTypesHolder = businessTypes[Math.floor(Math.random() * NUM_BUSINESSTYPES)];
    const desc = businessNames[i] + " is a high quality business in the field of " + businessTypesHolder + ". We " +
      "have a wide variety of products for you to view and purchase."

    businesses.push({
      primaryAdministratorId: null,
      name: businessNames[i],
      description: desc,
      address: null,
      businessType: businessTypesHolder
    })
  }
  return businesses;
}

/**
 * likes listings for non business owning users
 * all non business users like a specific listing to load test that listing
 * then a percentage of these users like a random amount of random listings
 * some users may unlike the high load listing but this is good for notifications of unliking
 * @param instance for sending api requests
 * @returns {Promise<void>}
 */
async function likeListings(instance) {
  try {
    await instance.put(`${SERVER_URL}/listings/${LISTING_ID_OF_MAX_LIKED_LISTING}/like`, null, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
      }
    });
  } catch (e) {
    console.log(`Tried to like listing Id ${LISTING_ID_OF_MAX_LIKED_LISTING} for max liked listing, but listing does not exist.`);
    console.log(`Error message was: ${e}`)
  }
  if (Math.random() < PROB_USER_LIKES_LISTINGS) {
    for (let i = 0; i <= Math.random() * MAX_LIKED_LISTINGS_PER_USER; i++) {
      const listingId = Math.floor(Math.random() * APPROX_NUM_LISTINGS) + 1;
      try {
        await instance.put(`${SERVER_URL}/listings/${listingId}/like`, null, {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
          }
        });
      } catch (err) {
        if (err.response.status !== 406) {
          console.log(err.message, err.response.data);
        }
      }
    }
  }
}

/**
 * Uses axios to make an api called to our backend purchase endpoint,
 * purchases will only begin being made once all listings have been created,
 * each user can like up to a maximum number of listings with a specific probability
 * of liking a listing each time.
 *
 * @param instance for sending api requests
 * @returns {Promise<void>}
 */
async function purchaseListing(instance) {

  for (let i = 0; i <= Math.random() * MAX_PURCHASES_PER_USER; i++) {
    if (Math.random() < PROB_USER_PURCHASES_LISTING) {
      const listingId = Math.floor(Math.random() * APPROX_NUM_LISTINGS) + 1;
      try {
        await instance.post(`${SERVER_URL}/listings/${listingId}/purchase`, null, {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
          }
        });
      } catch (err) {
        if (err.response.status !== 406) {
          console.log(err.message, err.response.data);
        }
      }
    }
  }
}

/**
 * Uses axios to make a post request to our backend to create a new user.
 */
async function registerUser(user, listingsReady = true) {
  const instance = Axios.create({
    baseURL: SERVER_URL,
    timeout: 180000,// set 2 mins
    withCredentials: true
  });

  const response = await instance.post(`${SERVER_URL}/users`, user, {
    headers: {
      'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
    }
  });
  instance.defaults.headers.Cookie = response.headers["set-cookie"];

  await uploadUserImage(response.data.id, instance);

  await addCard(instance);
  if (listingsReady) await likeListings(instance);
  if (listingsReady) await purchaseListing(instance);
  return [response, instance];
}

/**
 * Uses axios to make a post request to our backend to create a new user and a number of businesses with that user
 */
async function registerUserWithBusinesses(user, businesses, numBusinesses) {
  const [response, instance] = await registerUser(user, false);
  for (let i = 0; i < numBusinesses; i++) {
    const businessResponse = await registerBusiness(businesses[i], instance, response.data.id, user);

    const businessId = businessResponse.data.businessId;

    await uploadBusinessImage(businessId, instance);

    await addProduct(businessId, instance, businesses[i]);

    let adminIds = [];
    for (let j = 0; j < Math.min(Math.floor(Math.random() * (6)) + 1, response.data.id - 1); j++) {
      const id = (Math.floor(Math.random() * (response.data.id)) + 1);
      if (id !== response.data.id && !adminIds.includes(id)) {
        adminIds.push(id);
      }
    }

    for (const adminId of adminIds) {
      await makeUserBusinessAdmin(businessResponse.data.businessId, instance, adminId);
    }

    console.log(`Registered business with products and inventory and listings. Id: ${businessResponse.data.businessId}`);
  }

}

/**
 * Given a list of users in SENG302 API format, registers all of them to the backend.
 * This is done in batches of MAX_USERS_PER_API_REQUEST to maximise efficiency
 */
async function registerUsers(users, businesses) {

  let businessesRegistered = 0;

  for (let i = 0; i < users.length / MAX_USERS_PER_API_REQUEST; i++) {
    const promises = []
    if (businessesRegistered < NUM_BUSINESSES) {
      for (let j = 0; j < MAX_USERS_PER_API_REQUEST; j++) {
        let numBusinessesForUser = Math.floor(Math.random() * MAX_BUSSINESSES_PER_USER)
        if (businessesRegistered + numBusinessesForUser > NUM_BUSINESSES) {
          numBusinessesForUser = NUM_BUSINESSES - businessesRegistered;
        }
        promises.push(registerUserWithBusinesses(users[i * MAX_USERS_PER_API_REQUEST + j], businesses.slice(businessesRegistered, businessesRegistered + numBusinessesForUser), numBusinessesForUser))
        businessesRegistered += numBusinessesForUser;
      }
    } else {
      for (let j = 0; j < MAX_USERS_PER_API_REQUEST; j++) {
        promises.push(registerUser(users[i * MAX_USERS_PER_API_REQUEST + j]));
      }
    }
    try {
      await Promise.all(promises);
    } catch (e) {
      console.log(e.message, e.response.data);
      throw e;
    }
    console.log(`Registered ${(i + 1) * MAX_USERS_PER_API_REQUEST} users.`);

  }
}

/**
 *  Sets the businesses primary admin id to the user and the address of the business to the user's
 *  address that has just been registered and makes the post request to our backed to register the business
 */
async function registerBusiness(business, instance, userId, user) {

  business.primaryAdministratorId = userId;
  business.address = user.homeAddress;
  business.description += " Our business headquarters are in " + user.homeAddress.city + ", " + user.homeAddress.country + ".";

  return await instance
    .post(`${SERVER_URL}/businesses`, business, {
      headers: {
        'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
      }
    })


}

async function makeUserBusinessAdmin(businessId, instance, userId) {
  const data = {
    userId
  }

  return await instance
    .put(`${SERVER_URL}/businesses/${businessId}/makeAdministrator`,
      data,
      {
        headers: {
          'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
        }
      }
    )
}

/**
 * Given a random product name returns the products in our
 * SENG302 API format
 */
function createProductObject(name, business) {

  const productId = name.replace(/\s/g, "-").replace(/\'/g, "").toUpperCase();
  const manufacturer = businessNames[Math.floor(Math.random() * businessNames.length)]
  const desc = "This is a very tasty product called " + name + ". It is well priced and a high quality is ensured by " +
    manufacturer + "."

  return {
    id: productId,
    name: name,
    description: desc,
    manufacturer: manufacturer,
    recommendedRetailPrice: (Math.random() * MAX_PRODUCT_PRICE).toFixed(2),
  };
}

/**
 * Create an inventory item in the SENG302 API format with random dates.
 */
function createInventoryObject(product) {

  const now = new Date(new Date().setHours(new Date().getHours() + 1));
  const end = new Date(new Date().setDate(new Date().getDate() + 3 * 52 * 7));
  const lastMonth = new Date(new Date().setDate(new Date().getDate() - 31));

  const manufactured = new Date(lastMonth.getTime() + Math.random() * (now.getTime() - lastMonth.getTime()));
  const sellBy = new Date(now.getTime() + Math.random() * (end.getTime() - now.getTime()));
  const bestBefore = new Date(sellBy.getTime() + Math.random() * (end.getTime() - sellBy.getTime()));
  const expires = new Date(bestBefore.getTime() + Math.random() * (end.getTime() - bestBefore.getTime()));
  const quantity = Math.floor(Math.random() * (MAX_QUANTITY_PRODUCT_IN_INVENTORY - MIN_QUANTITY_PRODUCT_IN_INVENTORY) +
    MIN_QUANTITY_PRODUCT_IN_INVENTORY);
  const totalPrice = quantity * product.recommendedRetailPrice;

  return {
    productId: product.id,
    quantity: quantity,
    pricePerItem: product.recommendedRetailPrice,
    totalPrice: totalPrice.toFixed(2),
    manufactured: manufactured,
    sellBy: sellBy,
    bestBefore: bestBefore,
    expires: expires
  };
}


/**
 *  Add a random number of products to a business using our backend endpoint
 */
async function addInventory(businessId, instance, product) {

  const inventory = createInventoryObject(product);

  const inventoryResponse = await instance
    .post(`${SERVER_URL}/businesses/${businessId}/inventory`, inventory, {
      headers: {
        'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
      }
    })
  return [inventoryResponse.data.inventoryItemId, inventory];

}

/**
 * Create an listing item in the SENG302 API format with random dates.
 */
function createListingObject(inventory, inventoryItemId) {

  const quantity = Math.floor(Math.random() * (inventory.quantity - MIN_QUANTITY_INVENTORY_IN_LISTING) +
    MIN_QUANTITY_INVENTORY_IN_LISTING);

  const price = Math.random() * parseFloat(inventory.pricePerItem) + parseFloat(inventory.pricePerItem);

  const discount = Math.floor(Math.random() * 10) * 10; // random number between 0 to 10
  const moreInfo = discount > 0 ? `${discount}% DISCOUNT` : "No DISCOUNTS!";

  return {
    inventoryItemId: inventoryItemId,
    quantity: quantity,
    price: price.toFixed(2),
    moreInfo: moreInfo,
    closes: inventory.expires
  };
}

/**
 *  Add a random number of listing to a business using our backend endpoint
 */
async function addListing(businessId, instance, inventory, inventoryItemId) {

  const listing = createListingObject(inventory, inventoryItemId);

  await instance
    .post(`${SERVER_URL}/businesses/${businessId}/listings`, listing, {
      headers: {
        'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
      }
    })

}


/**
 *  Add a random number of products to a business using our backend endpoint
 *  On creation of product a history of purchases is also randomly created
 */
async function addProduct(businessId, instance, business) {

  const numProducts = Math.floor(Math.random() * (MAX_PRODUCTS_PER_BUSINESS - MIN_PRODUCTS_PER_BUSINESS));

  const offset = Math.floor(Math.random() * (productNames.length - numProducts));
  for (let i = 0; i < numProducts; i++) {
    try {
      const product = createProductObject(productNames[i + offset], business);
      const productResponse = await instance
        .post(`${SERVER_URL}/businesses/${businessId}/products?generateSalesData=true`, product, {
          headers: {
            'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
          }
        })

      product.id = productResponse.data.productId;
      await addProductImages(businessId, instance, product.id);

      if (Math.random() < CHANCE_OF_INVENTORY_FOR_PRODUCT) {
        const [inventoryItemId, inventory] = await addInventory(businessId, instance, product);
        await addListing(businessId, instance, inventory, inventoryItemId);
      }

    } catch (err) {
      console.log(err.message, err.response.data);
    }

  }
}

/**
 *  Creates a card object with the random sample data.
 * @returns A card Object with the fields and data.
 */
function createCardObject() {

  const sections = ["ForSale", "Wanted", "Exchange"];
  const section = sections[Math.floor(Math.random() * sections.length)];
  const title = productNames[Math.floor(Math.random() * productNames.length)];
  const description = cardDes[Math.floor(Math.random() * cardDes.length)]
  const keywords = ["food", "hungry", "delicious", "yummy", "fresh"]

  return {
    section: section,
    title: title,
    description: description,
    keywords: keywords.slice(0, Math.floor(Math.random() * keywords.length)),
  };
}


/**
 *  Populates the database with the sample card data for different users.
 * @param instance An instance of axios
 * @returns {Promise<void>}
 */
async function addCard(instance) {

  for (let i = 0; i < Math.floor(Math.random() * MAX_CARD_PER_USER); i++) {
    const card = createCardObject();
    await instance.post(`${SERVER_URL}/cards`, card, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
      }
    });
  }

}

/**
 *  Add a random number of images to a product
 */
async function addProductImages(businessId, instance, productId) {

  const startImageId = Math.floor(Math.random() * (28) + 1);
  const numImagesForProduct = Math.floor(Math.random() * (3) + 1);

  let imagePromises = [];

  try {
    for (let i = 0; i < numImagesForProduct; i++) {
      imagePromises.push(uploadProductImage(businessId, productId, instance, startImageId + i));
    }
    await Promise.all(imagePromises);
  } catch (err) {
    console.log(err.message, err.response.data);
  }
}

/**
 * Uploads an image for a product.
 */
async function uploadProductImage(businessId, productId, instance, startImageId) {
  // See https://github.com/axios/axios/issues/710 for how this works
  let formData = new FormData();
  formData.append("filename", fs.createReadStream(`./exampleImages/product/${startImageId}.jpg`));
  return instance.post(`${SERVER_URL}/businesses/${businessId}/products/${productId}/images`, formData,
    {headers: formData.getHeaders()});
}


/**
 * Uploads an image for a user.
 * https://commons.wikimedia.org/w/index.php?search=profile&title=Special:MediaSearch&go=Go&type=image
 * The user images come from the link.
 */
async function uploadUserImage(userId, instance) {
  if (Math.random() > 0.25) {
    let formData = new FormData();
    const imageIndex = Math.floor(Math.random() * (NUMBER_OF_USER_IMAGES - 1) + 1); // 15 is number of images in user file
    formData.append("filename", fs.createReadStream(`./exampleImages/user/${imageIndex}.png`));
    return instance.post(`${SERVER_URL}/users/${userId}/image`, formData,
      {headers: formData.getHeaders()});
  }
}

/**
 * Uploads an image for a business.
 * https://spark.adobe.com/express-apps/logo-maker/
 * The business images are generate by the link
 */
async function uploadBusinessImage(businessId, instance) {
    if (Math.random() > 0.25) {
        let formData = new FormData();
        const imageIndex = Math.floor(Math.random() * (NUMBER_OF_BUSINESS_IMAGES - 1) + 1); // 18 is number of images in business file
        formData.append("filename", fs.createReadStream(`./exampleImages/business/image_${imageIndex}.jpeg`));
        return instance.post(`${SERVER_URL}/businesses/${businessId}/image`, formData,
            {headers: formData.getHeaders()});
    }
}

async function main() {
  let users;
  let businesses = await getBusinesses();
  if (process.argv.length === 3 && process.argv[2] === 'regenerateData') {
    users = await getUsers(); // Sonarlint says that the await is redundant. Sonarlint is stupid.
  } else if (process.argv.length === 2) {
    // Check if there is previously generated data
    if (fs.existsSync("users.json")) {
      let rawdata = fs.readFileSync('users.json');
      users = JSON.parse(rawdata);
    } else {
      users = await getUsers();
    }
  } else {
    console.log("Invalid command line arguments passed.\n" +
      "Usage: \n" +
      "'npm run start regenerateData' to re-generate random user data (requires internet connection to https://randomuser.me)\n" +
      "'npm run start' to run normally (re-uses user data if generate previously)");
    process.exit();
  }

  await registerUsers(users, businesses);
}

main().then(() => console.log('ALL DONE'));
