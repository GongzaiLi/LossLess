# Seng302 Example Project

Basic project template using `gradle`, `npm`, `Spring Boot`, `Vue.js` and `Gitlab CI`.

## Basic Project Structure

A frontend sub-project (web GUI):

- `frontend/src` Frontend source code (Vue.js)
- `frontend/public` publicly accessible web assets (e.g., icons, images, style sheets)
- `frontend/dist` Frontend production build

A backend sub-project (business logic and persistence server):

- `backend/src` Backend source code (Java - Spring)
- `backend/out` Backend production build

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

## Todo (S1)

- Add team name into `build.gradle` and `package.json`
- Update this README title
- Update this README contributors

## Todo (S2)

- Update team name into `build.gradle` and `package.json`
- Update this README title
- Update this README contributors
- Set up Gitlab CI server (refer to the student guide on learn)
- Decide on a LICENSE

## Contributors

- SENG302 teaching team

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue docs](https://vuejs.org/v2/guide/)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=10577&section=11)
