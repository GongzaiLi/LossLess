const fs = require('fs')
const Axios = require('axios');

const NUM_USERS = 10000;
const MAX_GENERATED_USERS_PER_REQUEST = 5000;
const MAX_USERS_PER_API_REQUEST = 100;

const userBios = require('./bios.json')

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
      console.log(user.email);
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

  const users = []

  for (let user of apiUsers) {
    users.push({
      firstName: user.name.first,
      lastName: user.name.last,
      nickName: (Math.random() < 0.1) ? user.login.username: undefined,
      bio: userBios[Math.floor(Math.random() * userBios.length)],
      email: user.email.replace('\'', '').replace('..', '.'),  // some data comes back malformed with ' (single quote) characters or .. (double dots), so we remove them here
      dateOfBirth: user.dob.date,
      phoneNumber: user.phone,
      password: user.login.password,
      homeAddress: getAddressFromApiUser(user)
    })
  }

  makeEmailsUnique(users);

  fs.writeFile("users.json", JSON.stringify(users), () => {
    console.log('Finished writing users');
  });

  return users;
}

/**
 * Given a list of users in SENG302 API format, registers all of them to the backend.
 * This is done in batches of MAX_USERS_PER_API_REQUEST to maximise efficiency
 */
async function registerUsers(users) {
  for (let i=0; i < users.length / MAX_USERS_PER_API_REQUEST; i++) {
    const promises = []
    for (let j=0; j < MAX_USERS_PER_API_REQUEST; j++) {
      promises.push(
        Axios
          .post(`http://localhost:9499/users`, users[i*MAX_USERS_PER_API_REQUEST+j], {
            headers: {
              'Content-Type': 'application/json', // For some reason Axios will make the content type something else by default
            }
          })
      );
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

async function main() {
  let users;
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

  await registerUsers(users);
}

main().then(() => console.log('ALL DONE'));