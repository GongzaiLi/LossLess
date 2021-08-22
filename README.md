<table align="center"><tr><td align="center" width="999">

## Welcome to Wasteless! 
WASTELESS is a web app where stores can advertise any products that they are about to throw out at 
a reduced cost to the public. The primary purpose of this application is to reduce the amount of wastage.  
Users can sign up with an account and search for listings of food to like and purchase. Users can also search 
for businesses and view the food that the business is currently offering for sale.   
Users can also create a business account. A business account can 
create products and inventory to manage - Wasteless allows you to keep track of the quantites and expiry dates 
of your inventory. Additionally, businesses can publically list existing inventory for sale, where it can be purchased
by other users.  
WASTELESS also provides a community marketplace, where users can create cards publically advertising items they would 
like to buy, sell or exchange.

### To access the deployed production application, visit:
### https://csse-s302g7.canterbury.ac.nz/prod
### To access the deployed development application, visit:
### https://csse-s302g7.canterbury.ac.nz/test
</td></tr></table>

## How to run locally

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

## Data Generation script
Run the following commands:

    cd data-generation
    npm install
    npm run start

Ensure that the database is empty, otherwise email collisions may occur with existing users.

## User Guide
The user guide is in this repo's wiki, under the 'User Guide' page.

## Third Party Dependencies
### Frontend:
 - Vue-Router (3.5 or greater)
 - Bootstrap Vue (2.21 or greater)
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