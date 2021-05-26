<table align="center"><tr><td align="center" width="999">


## Welcome to Wasteless! 
### To access the deployed development application, visit:  

### https://csse-s302g7.canterbury.ac.nz/test
### To access the deployed production application, visit: 
### https://csse-s302g7.canterbury.ac.nz/prod
### Create an account, log in, and use the navbar for easy navigation
### Or use the following routes to access all features below:
### `login`: `/` or `/login`
### `register`: `/register`
### `User Profile`: `/users/:id`
### `User Search`: `/users/search`
### `Home Page`: `/homePage`
### `Create Business`: `/businesses`
### `Business Profile`: `/businesses/:id`
### `Product Catalogue`: `/businesses/:id/products`
</td></tr></table>

## How to run

### Frontend / GUI

    $ cd frontend
    $ npm install
    $ npm run serve

Running on: http://localhost:9500/ by default

### Backend / server

    cd backend
    ./gradlew bootRun

Running on: http://localhost:9499/ by default

## Default Global Application Admin (DGAA)
A DGAA is just an admin account with the following property: the application must always have a DGAA when it is running.
This allows you to still get into the system if something goes wrong.  
Our system periodically checks whether a DGAA exists, and will create a default account if one does not.
You can tweak the length of that period through changing the `check-default-admin-period-ms` property of the config file: 
`backend/src/main/resources/global-admin.properties`.
The default period is 60000 milliseconds (1 minute). Note this will require a rebuild of the application.  
The username and password of the DGAA is taken at startup from the same properties file (properties `default-admin-username` and `default-admin-password`). NOTE: on the deployed environment,
the following gitlab CI variables are used instead:

    DGAA_EMAIL
    DGAA_PASSWORD

If you do not have these environment variables defined then the application will not start.

## Third Party Dependencies
### Frontend:
 - Vue-Router
 - Bootstrap Vue
### Backend:
*None*

## Contributors

- SENG302 teaching team
- Arish Myckel Abalos
- Marcus Brorens
- Oliver Cranshaw
- James Harris
- Eric Song
- Caleb Sim
- Gongzai Li
- Nitish Singh
- Phil Taylor