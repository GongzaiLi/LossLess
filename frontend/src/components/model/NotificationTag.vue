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
  props: ['tagColor', "tagStyleProp"],
  data() {
    return {
      tagStyle: {
        background: "",
        height: "100%",
        width: "1.5rem"
      }
    }
  },

  methods: {
    /**
     * Determine the color of the tag based on the tag value
     */
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
          this.tagStyle.background = "linear-gradient(80deg, #ffa500 50%, #292929 50%)";
          break;
        }
      }
    }
  },

  mounted() {
    this.tagStyle = {
      ...this.tagStyle,
      ...this.tagStyleProp
    }
    this.determineTagCSSColor()
  }

}
</script>
