<template>
  <div>
    <h1 strong align="left"> {{fullCard.title}} </h1>
    <b-container>
        <h6 align="left"> Listed On {{fullCard.created}}</h6>
    </b-container>
    <br>

    <b-card no-body>
      <template #header>
        <label> {{fullCard.description}}</label>
      </template>
    </b-card>
    <br>

    <b-card no-body>
      <template #header>
        Tags:
        <label v-for="tag in fullCard.keywords" v-bind:key="tag.id">
          {{tag + ", "}}
        </label>
      </template>
    </b-card>
    <br>


    <b-input-group-text>
      <b-container>
        <h6 align="center"> <strong> Seller Info: </strong></h6>
        <label> Seller Name: {{fullCard.creator.firstName }} {{ fullCard.creator.lastName }}   </label>
        <br>
        <label> Seller Location: {{fullCard.creator.homeAddress.suburb ? fullCard.creator.homeAddress.suburb + "," : ""}} {{fullCard.creator.homeAddress.city}}</label>
      </b-container>
    </b-input-group-text>
    <br>


      <div>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="closeFullViewCardModal"> Close </b-button>
      </div>

  </div>

</template>

<script>
import api from "../../Api";
export default {
  name: "full-card",
  props: ["cardId", 'closeFullViewCardModal'],
  data() {
    return {
      fullCard: {
      "id": 500,
        "creator": {
          "id": 100,
          "firstName": "John",
          "lastName": "Smith",
          "middleName": "Hector",
          "nickname": "Jonny",
          "bio": "Likes long walks on the beach",
          "email": "johnsmith99@gmail.com",
          "dateOfBirth": "1999-04-27",
          "phoneNumber": "+64 3 555 0129",
          "homeAddress": {
            "streetNumber": "3/24",
            "streetName": "Ilam Road",
            "suburb": "Upper Riccarton",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand",
            "postcode": "90210"
          },
          "created": "2020-07-14T14:32:00Z",
          "role": "user",
          "businessesAdministered": [
              {
                "id": 100,
                "administrators": [
                    "string"
                ],
                "primaryAdministratorId": 20,
                "name": "Lumbridge General Store",
                "description": "A one-stop shop for all your adventuring needs",
                "address": {
                  "streetNumber": "3/24",
                  "streetName": "Ilam Road",
                  "suburb": "Upper Riccarton",
                  "city": "Christchurch",
                  "region": "Canterbury",
                  "country": "New Zealand",
                  "postcode": "90210"
                },
                "businessType": "Accommodation and Food Services",
                "created": "2020-07-14T14:52:00Z"
              }
              ]
        },
        "section": "ForSale",
        "created": "2021-07-15T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "1982 Lada Samara",
        "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
        "keywords": [ "Vehicle", "Sale",]
      }
    }
  },
  mounted() {
    this.getCard()
  },
  methods: {

    getCard() {
      api.getFullCard(this.cardId)
        .then((resp) => {
          this.$log.debug("Data loaded: ", resp.data);
          this.fullCard = resp.data;
      }).catch((error) => {
          this.$log.debug(error);
      })
    }

  }
}
</script>

<style scoped>

</style>
