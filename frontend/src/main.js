// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import App from "./App";
import Vuex from "vuex";
import BootstrapVue from "bootstrap-vue";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import axios from "axios";
import VueMoment from "vue-moment";
import VueClipboard from "vue-clipboard2";

Vue.prototype.$http = axios;

// vuex 사용처리
Vue.use(Vuex);
Vue.use(VueMoment);
Vue.use(VueClipboard);

const store = new Vuex.Store({
  state: {
    stompClient: 0,
  },
  mutations: {
    connect(state, data) {
      state.stompClient = data;
    },
  },
  actions: {},
});

Vue.use(BootstrapVue);

Vue.config.productionTip = false;

/* eslint-disable no-new */
new Vue({
  store,
  render: (h) => h(App),
}).$mount("#app");
