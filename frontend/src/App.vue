<template>
  <MainBoard></MainBoard>
</template>

<script>
import MainBoard from "./components/MainBoard.vue";
export default {
  name: "app",

  components: {
    MainBoard,
  },

  data() {
    return {};
  },

  methods: {},
};
</script>

<style>
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
