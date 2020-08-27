//vue infinite loading 메소드 재정의
import InfiniteLoading from "vue-infinite-loading";

export default {
  name: "infinite-loading-extra",
  extends: InfiniteLoading,
  methods: {
    getCurrentDistance() {
      let distance = 0;
      if (this.direction === "right") {
        const infiniteElmOffsetLeftFromRight = this.$el.getBoundingClientRect().left;
        const scrollElmOffsetLeftFromRight =
          this.scrollParent === window
            ? window.innerWidth
            : this.scrollParent.getBoundingClientRect().right;
        distance = infiniteElmOffsetLeftFromRight - scrollElmOffsetLeftFromRight;
      }
      return distance;
    },

    getScrollParent(elm = this.$el) {
      let result;
      if (elm.tagName === "BODY") {
        result = window;
      } else if (
        !this.forceUseInfiniteWrapper &&
        this.direction === "right" &&
        ["scroll", "auto"].indexOf(getComputedStyle(elm).overflowX) > -1
      ) {
        result = elm;
      } else if (
        elm.hasAttribute("infinite-wrapper") ||
        elm.hasAttribute("data-infinite-wrapper")
      ) {
        result = elm;
      }
      return result || this.getScrollParent(elm.parentNode);
    },
  },
};
