<template>
  <div>
    <b-row class="mt-3">
      <b-col offset-lg="2"><h3 class="text-center">Durations between sale and closing dates</h3></b-col>
      <b-col lg="3">
          <b-row align-v="center" align-h="center" class="group-by-row ml-auto">
            Group size:
            <b-form-input number type="number" class="narrow-input ml-1 mr-1" min="1" max="100" v-model="granularity" @input="updateChart" :state="validGranularity"/>
            {{granularity === 1 ? 'Day ' : 'Days'}}
          </b-row>
        <b-form-invalid-feedback force-show v-if="validGranularity === false" class="text-center">
          {{invalidGranularityMessage}}
        </b-form-invalid-feedback>
      </b-col>
    </b-row>
    <canvas id="listings-durations-graph"></canvas>
  </div>
</template>

<style scoped>
.narrow-input {
  max-width: 5em;
}
.group-by-row {
  font-size: 1.2rem;
}
</style>

<script>
import Api from "../../Api";
import Chart from "chart.js/auto";

export default {
  name: "ListingsDurationsGraph",
  props: ['dateRange'],
  async mounted() {
    const cfg = {
      type: 'bar',
      data: {
        datasets: [{
          data: [],
          backgroundColor: ['#0f8d39'],
        }]
      },
      options: {
        scales: {
          xAxis: {
            type: 'linear',
            offset: false,
            grid: {
              offset: false
            },
            ticks: {
              stepSize: this.granularity,
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
                let daysValue = Math.floor(t[0].parsed.x); // The days is set to the middle of two granularity's
                daysValue = daysValue - daysValue % this.granularity;
                return ` Sold ${daysValue} - ${daysValue + this.granularity} days before closing`;
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
    await this.updateChart();
  },
  data() {
    return {
      chart: null,
      granularity: 1,
      validGranularity: null,
      invalidGranularityMessage: ""
    }
  },
  methods: {
    async updateChart() {
      if (this.granularity >= 100) {
        this.validGranularity = false;
        this.invalidGranularityMessage = "Value must be less than 100"
      } else if (this.granularity < 1) {
        this.validGranularity = false;
        this.invalidGranularityMessage = "Value must be more than 0"
      } else {
        this.validGranularity = null;
        const businessId = this.$route.params.id;
        const durationsData = (await Api.getListingDurations(businessId, this.granularity, ...this.dateRange)).data;

        let maxDuration = 0;
        let minDuration = 10000000;
        const offsetData = {}; // We offset all durations by 0.5, this is a hack to make the data line up on the histogram properly
        for (const duration in durationsData) {
          const durationInt = parseInt(duration);
          maxDuration = Math.max(maxDuration, durationInt);
          minDuration = Math.min(minDuration, durationInt);
          offsetData[durationInt + this.granularity / 2] = durationsData[duration];
        }

        this.chart.data.datasets[0].data = offsetData;

        this.chart.options.scales.xAxis.ticks.stepSize = this.granularity;
        this.chart.options.scales.xAxis.min = minDuration;
        this.chart.options.scales.xAxis.max = maxDuration + this.granularity;
        this.chart.update();
       }
    }
  }
}
</script>

<style scoped>

</style>