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

async function getUsers() {
  console.log('Getting random user info from Api...');
  const apiUsers = await getApiRandomUserInfo();
  console.log('Got all random users from Api.');

  const users = []

  for (let user of apiUsers) {
    users.push({
      firstName: user.name.first,
      lastName: user.name.last,
      nickName: (Math.random() < 0.1) ? user.login.username: undefined,
      bio: userBios[Math.floor(Math.random() * userBios.length)],
      email: user.email,
      dateOfBirth: user.dob.date,
      phoneNumber: user.phone,
      password: user.login.password,
      homeAddress: getAddressFromApiUser(user)
    })
  }

  return users;
}

async function main() {
  const users = await getUsers();

  fs.writeFile("users.json", JSON.stringify(users), () => {
    console.log('Finished writing users');
  });

  for (let i=0; i < NUM_USERS / MAX_USERS_PER_API_REQUEST; i++) {
    const promises = []
    for (let j=0; j < MAX_USERS_PER_API_REQUEST; j++) {
      promises.push(
        Axios
          .post(`http://localhost:9499/users`, users[i*MAX_USERS_PER_API_REQUEST+j], {
            headers: {
              'Content-Type': 'application/json',
            }
          })
      );
    }
    try {
      await Promise.all(promises);
    } catch(e) {
      console.log(e);
    }
    console.log(`Registered ${(i + 1) * MAX_USERS_PER_API_REQUEST}`);
  }
}

main().then(() => console.log('ALL DONE'));