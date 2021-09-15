<template>
  <div>
    <b-row no-gutters>
      <b-col cols="auto" class="mr-2">
        <span :class="{'disabled': showTotalValue}">Show total sales</span>
      </b-col>
      <b-col class="float-left">
        <b-form-checkbox v-model="showTotalValue" @input="refreshGraph" name="check-button" switch>
          <span :class="{'disabled': !showTotalValue}">Show total value</span>
        </b-form-checkbox>
      </b-col>
    </b-row>
    <canvas id="sales-report-graph"/>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import {getMonthName} from "../../util";

export default {
  name: "SalesReportGraph",
  props: ["reportData", "currency", "groupBy"],
  data: function () {
    return {
      chart: null,
      showTotalValue: false
    }
  },
  mounted() {
    const cfg = {
      type: 'bar',
      data: {
        datasets: [{
          label: 'Total Sales',
          data: [],
          backgroundColor: ['#005fc5'],
          parsing: {
            yAxisKey: 'totalPurchases',
            xAxisKey: 'label',
          }}]
      },
      options: {
        aspectRatio: 1.4,
        plugins: {
          tooltip: {
            callbacks: {
              label: (t) => {
                return this.showTotalValue ?
                    `Total Value: ${this.currency.symbol}${t.raw.totalValue}`:
                    `Total Sales: ${t.raw.totalPurchases}`;
              },
              afterLabel: (t) => {
                return this.showTotalValue ?
                    `Total Sales: ${t.raw.totalPurchases}` :
                    `Total Value: ${this.currency.symbol}${t.raw.totalValue}`;
              },
            }
          },
        },
        scales: {
          yAxis: {
            type: 'linear',
            ticks: {
              precision: 0
            }
          }
        }
      }
    };
    this.chart = new Chart(
        document.getElementById('sales-report-graph').getContext('2d'),
        cfg);
  },
  methods: {
    /**
     * Given a date string, returns the corresponding label to be displayed in the graph.
     */
    getLabelForValue(date) {
      switch (this.groupBy) {
        case "month" :
          return `${getMonthName(new Date(date).getMonth())} ${new Date(date).getFullYear()}`;
        case "year" :
          return (new Date(date).getFullYear()).toString();
        default:
          return date;
      }
    },
    /**
     * Makes the graph re-draw with updated data.
     */
    refreshGraph() {
      for (let dataPoint of this.reportData) {
        dataPoint.label = this.getLabelForValue(dataPoint.startDate);
      }

      this.chart.data.labels = [];
      this.chart.data.datasets.forEach((dataset) => {
        dataset.data = this.reportData;
      });
      this.chart.data.datasets.forEach((dataset) => {
        dataset.label = this.showTotalValue ? `Total Value (${this.currency.code})` : 'Total Sales';
        dataset.parsing.yAxisKey = this.showTotalValue ? 'totalValue' : 'totalPurchases';
      });
      this.chart.update();
    }
  },
  watch: {
    reportData: function() {
      this.refreshGraph();
    }
  }
}
</script>

<style scoped>
.disabled {
  opacity: 0.6;
}
</style>