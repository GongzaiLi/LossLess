/**
 * Event-bus.js helps components communicate to each other
 * by letting one component emit and another listen
 * https://andrejsabrickis.medium.com/https-medium-com-andrejsabrickis-create-simple-eventbus-to-communicate-between-vue-js-components-cdc11cd59860
 */
import Vue from 'vue';
const EventBus = new Vue();
export default EventBus;