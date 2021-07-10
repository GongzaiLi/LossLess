const fs = require('fs')
const Axios = require('axios');

const NUM_USERS = 100;
const MAX_PER_REQUEST = 50;

async function getApiRandomUserInfo() {
  let users = [];
  for (let i = 0; i < NUM_USERS / MAX_PER_REQUEST; i++) {
    let usersBatch = (await Axios
      .get(`https://randomuser.me//api/?results=${MAX_PER_REQUEST}`))
      .data.results;
    console.log(`Got ${(i + 1) * MAX_PER_REQUEST} / ${NUM_USERS} users from API`)
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

async function main() {
  console.log('Getting random user info from Api...');
  const apiUsers = await getApiRandomUserInfo();
  console.log('Got all random users from Api.');

  const users = []

  for (let user of apiUsers) {
    users.push({
      address: getAddressFromApiUser(user)
    })
  }

  fs.writeFile("users.json", JSON.stringify(users), () => {
    console.log('Finished writing users');
  });
}

main().then(() => console.log('ALL DONE'));