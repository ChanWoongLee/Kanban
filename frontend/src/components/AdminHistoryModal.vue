<template>
  <transition name="modal">
    <div class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <div name="header" style="font-size: 24px;">관리자용 변경 이력</div>
            <select
              class="custom-select mr-sm-2"
              v-model="modifiedEntity"
              style="width:10%; float:right;"
            >
              <option selected value>전체</option>
              <option>board</option>
              <option>card</option>
            </select>
            <select
              class="custom-select mr-sm-2"
              v-model="actionName"
              style="float: right; width:10%; "
            >
              <option selected value>전체</option>
              <option>added</option>
              <option>removed</option>
              <option>updated</option>
              <option>moved</option>
            </select>
            <button
              type="button"
              class="btn btn-outline-dark"
              style=" float: right;"
              @click="changeHistoryFilter"
            >조회</button>
          </div>
          <div class="table-wrapper-scroll-y my-custom-scrollbar">
            <table class="table">
              <thead class="thead-light">
                <tr style=" font-size: 25px">
                  <th scope="col">No.</th>
                  <th scope="col">이력종류</th>
                  <th scope="col">엔티티</th>
                  <th scope="col">변경된 컬럼</th>
                  <th scope="col">이전 데이터</th>
                  <th scope="col">이후 데이터</th>
                  <th scope="col">수정된 날짜</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(history, index) in historyList" :key="history.eventId">
                  <td scope="row">{{ index + 1 }}</td>
                  <td scope="row">{{ history.actionName }}</td>
                  <td scope="row">{{ history.modifiedEntity }}</td>
                  <td scope="row">{{ history.modifiedColumn }}</td>
                  <td
                    scope="row"
                    v-if="history.beforeBoardTitle"
                  >{{ history.beforeData }}th in {{ history.beforeBoardTitle }}</td>
                  <td scope="row" v-else>{{ history.beforeData }}</td>
                  <td
                    scope="row"
                    v-if="history.afterBoardTitle"
                  >{{ history.afterData }}th in {{ history.afterBoardTitle }}</td>
                  <td scope="row" v-else>{{ history.afterData }}</td>
                  <td scope="row">{{ $moment(history.modifiedTime).format("YYYY-MM-DD HH:mm:ss") }}</td>
                </tr>
              </tbody>
            </table>
            <infinite-loading direction="bottom" @infinite="infiniteHandler" ref="infiniteLoading">
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
import infiniteLoading from "vue-infinite-loading";

export default {
  components: { infiniteLoading },
  data() {
    return {
      historyList: [],
      page: 0,
      actionName: "",
      modifiedEntity: "",
    };
  },
  methods: {
    changeHistoryFilter() {
      this.historyList = [];
      this.page = 0;
      this.$nextTick(() => {
        this.$refs.infiniteLoading.$emit("$InfiniteLoading:reset");
      });
    },
    infiniteHandler($state) {
      this.$http
        .get(ApiUrl.HISROTY_API_BASE_URL + "/admin", {
          params: {
            offset: this.page,
            actionName: this.actionName,
            modifiedEntity: this.modifiedEntity,
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
