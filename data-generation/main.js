/**
 * This script is responsible for generating 10000 random users and
 * registering them with a running backend. If it has not been run before
 * it depends on the https://randomuser.me Api to get random data.
 * Otherwise, it will re-used the preivously generated data (which will be saved
 * in a users.json file)
 */

const fs = require('fs')
const Axios = require('axios');

const NUM_USERS = 10000;
const MAX_GENERATED_USERS_PER_REQUEST = 5000;
const MAX_USERS_PER_API_REQUEST = 100;
const HAS_NICKNAME_PROB = 1/10;
const HAS_MIDDLE_NAME_PROB = 4/10;

const NUM_BUSINESSES = 100;
const NUM_BUSINESSTYPES = 4;
const MAX_BUSSINESSES_PER_USER = 3;

const userBios = require('./bios.json')
const businessNames = require('./businessNames.json')
const businessDesc = require('./businessDescs.json')
const businessTypes = require('./businessTypes.json')

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
      nickname: (Math.random() < HAS_NICKNAME_PROB) ? user.login.username.replace(/[0-9]/g, ''): undefined,
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

  for (let i=0; i < NUM_BUSINESSES; i++) {
    businesses.push({
      primaryAdministratorId: null,
      name: businessNames[i],
      description: businessDesc[i],
      address: null,
      businessType: businessTypes[Math.floor(Math.random() * NUM_BUSINESSTYPES)]
    })
  }
  return businesses;
}

/**
 * Uses axios to make a post request to our backend to create a new user.
 */
async function registerUser(user) {
  await Axios.post(`http://localhost:9499/users`, user, {
    withCredentials: true,
    headers: {
      'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
    }
  });
}

/**
 * Uses axios to make a post request to our backend to create a new user and a number of businesses with that user
 */
async function registerUserWithBusinesses(user, businesses, numBusinesses) {
  const instance = Axios.create({
    baseURL: SERVER_URL,
    timeout: 50000,
    withCredentials: true
  });

  const response = await instance.post(`http://localhost:9499/users`, user, {
    headers: {
      'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
    }
  });

  instance.defaults.headers.Cookie = response.headers["set-cookie"];

  for (let i=0; i < numBusinesses; i++) {
    await registerBusiness(businesses[i], instance, response.data.id, user);
  }
}

/**
 * Given a list of users in SENG302 API format, registers all of them to the backend.
 * This is done in batches of MAX_USERS_PER_API_REQUEST to maximise efficiency
 */
async function registerUsers(users, businesses) {

  let businessesRegistered = 0;

  for (let i=0; i < users.length / MAX_USERS_PER_API_REQUEST; i++) {
    const promises = []
    for (let j=0; j < MAX_USERS_PER_API_REQUEST; j++) {
      if (businessesRegistered < NUM_BUSINESSES) {
        let numBusinessesForUser = Math.floor(Math.random() * MAX_BUSSINESSES_PER_USER)
        if (businessesRegistered + numBusinessesForUser > NUM_BUSINESSES) {
          numBusinessesForUser = NUM_BUSINESSES - businessesRegistered;
        }
        promises.push(registerUserWithBusinesses(users[i*MAX_USERS_PER_API_REQUEST+j], businesses.slice(businessesRegistered, businessesRegistered + numBusinessesForUser), numBusinessesForUser))
        businessesRegistered += numBusinessesForUser;
      } else {
        promises.push(registerUser(users[i*MAX_USERS_PER_API_REQUEST+j]));
      }
    }
    try {
      await Promise.all(promises);
    } catch(e) {
      console.log(e);
      throw e;
    }
    console.log(`Registered ${(i + 1) * MAX_USERS_PER_API_REQUEST}`);
  }
}

/**
 *  Sets the businesses primary admin id to the user and the address of the business to the user's
 *  address that has just been registered and makes the post request to our backed to register the business
 */
async function registerBusiness(business, instance, userId, user) {

  business.primaryAdministratorId = userId
  business.address = user.homeAddress

    await instance
      .post(`http://localhost:9499/businesses`, business, {
        headers: {
          'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
        }
      })

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
