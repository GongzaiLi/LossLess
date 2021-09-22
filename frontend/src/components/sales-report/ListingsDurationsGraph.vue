<template>
  <canvas id="listings-durations-graph"></canvas>
</template>

<script>
import Api from "@/Api";
import Chart from "chart.js/auto";

export default {
  name: "ListingsDurationsGraph",
  props: ['dateRange'],
  async mounted() {
    const businessId = this.$route.params.id;
    const durationsData = (await Api.getListingDurations(businessId, ...this.dateRange)).data;
    const cfg = {
      type: 'bar',
      data: {
        datasets: [{
          label: 'Durations',
          data: durationsData,
          backgroundColor: ['#005fc5']
        }]
      },
      options: {
        barPercentage: 1.25,
        scales: {
          xAxes: [{
            display: true,
            ticks: {
              max: 3,
            }
          }],
          yAxes: [{
            ticks: {
              beginAtZero:true
            }
          }]
        }
      }
    };
    this.chart = new Chart(
        document.getElementById('listings-durations-graph').getContext('2d'),
        cfg);
  }
}
</script>

<style scoped>

</style>