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
    const newData = {};
    for (const val of Object.keys(durationsData)) {
      newData[parseInt(val) + 0.5] = durationsData[val];
    }
    const cfg = {
      type: 'bar',
      data: {
        datasets: [{
          label: 'Durations',
          data: newData,
          backgroundColor: ['#0f8d39'],
        }]
      },
      options: {
        categoryPercentage: 1,
        scales: {
          xAxis: {
            type: 'linear',
            display: true,
            offset: false,
            grid: {
              offset: false
            },
            ticks: {
              stepSize: 1,
            },
          },
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