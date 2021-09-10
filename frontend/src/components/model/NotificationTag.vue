<template>
  <div>
    <b-badge :style=tagStyle>
       {{""}}
    </b-badge>
  </div>
</template>

<script>
export default {
  name: "NotificationTag",
  props: ['tagColor'],
  data() {
    return {
      tagStyle: {
        background: "",
        width: "100%",
        height: "1.5rem"
      }
    }
  },

  methods: {
    determineTagCSSColor () {
      switch (this.tagColor) {
        case "RED": {
          this.tagStyle.background = "hsla(360, 100%, 53%, 1)";
          break;
        }
        case "ORANGE": {
          this.tagStyle.background = "hsla(39, 100%, 50%, 1)";
          break;
        }
        case "YELLOW": {
          this.tagStyle.background = "hsla(51, 100%, 50%, 1)";
          break;
        }
        case "GREEN": {
          this.tagStyle.background = "hsla(101, 100%, 50%, 1)";
          break;
        }
        case "BLUE": {
          this.tagStyle.background = "hsla(191, 100%, 50%, 1)";
          break;
        }
        case "PURPLE": {
          this.tagStyle.background = "hsla(290, 100%, 50%, 1)";
          break;
        }
        case "BLACK": {
          this.tagStyle.background = "hsla(1, 100%, 0%, 1)";
          break;
        }
        default: {
          this.tagStyle.background = "linear-gradient(to left, #ffa500 50%, #292929 50%)";
          break;
        }
      }
    }
  },

  mounted() {
    this.determineTagCSSColor()
  }

}
</script>

<style scoped>

</style>
