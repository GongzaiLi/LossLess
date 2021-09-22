<template>
  <div>
    <h3 class="mt-3 w-100 text-center">Durations between sale and closing dates</h3>
    <canvas id="listings-durations-graph"></canvas>
  </div>
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
          data: newData,
          backgroundColor: ['#0f8d39'],
        }]
      },
      options: {
        categoryPercentage: 1,
        scales: {
          xAxis: {
            type: 'linear',
            offset: false,
            grid: {
              offset: false
            },
            ticks: {
              stepSize: 1,
            },
            title: {
              display: true,
              text: "Number of days between sale date and closing date",
              font: {
                size: 16
              }
            }
          },
          yAxis: {
            title: {
              display: true,
              text: "Number of listings sold",
              font: {
                size: 16
              }
            }
          }
        },
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              title: (t) => {
                const daysValue = t[0].parsed.x;
                return ` Sold ${Math.floor(daysValue)} - ${Math.ceil(daysValue)} days before closing`;
              },
              label: (t) => {
                return ` ${t.parsed.y} listings`;
              }
            }
          },
        },
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