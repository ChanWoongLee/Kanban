<template>
  <transition name="modal">
    <div class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <div name="header" style="font-size: 24px;">사용자 활 동 이 력</div>
          </div>
          <div class="wrapper-scroll-y custom-scrollbar">
            <clientHistory v-for="history in historyList" :key="history.eventId" :history="history"></clientHistory>
            <infinite-loading direction="bottom" @infinite="infiniteHandler">
              <span slot="no-more"></span>
              <span slot="no-results"></span>
            </infinite-loading>
          </div>
          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-default-button" @click="$emit('close')">OK</button>
            </slot>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>
<script>
import * as ApiUrl from "../utils/ApiUrl.js";
import clientHistory from "./ClientHistory.vue";
import infiniteLoading from "vue-infinite-loading";

export default {
  components: { clientHistory, infiniteLoading },
  data() {
    return {
      historyList: [],
      page: 0,
    };
  },
  methods: {
    infiniteHandler($state) {
      this.$http
        .get(ApiUrl.HISROTY_API_BASE_URL + "/client", {
          params: {
            offset: this.page,
          },
        })
        .then((result) => {
          if (result.data.length) {
            console.log("history paging loding");
            this.page += 1;
            this.historyList = this.historyList.concat(result.data);
            $state.loaded();
          } else {
            console.log("history paging done");
            $state.complete();
          }
        });
    },
  },
};
</script>
<style src="../assets/css/CardDetailModal.css"></style>
