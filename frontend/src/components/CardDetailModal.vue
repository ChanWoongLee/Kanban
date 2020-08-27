<template>
  <transition name="modal">
    <div class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <div class="card-header">게 시 글</div>
            <div class="usingOther" v-if="usingOther">현재 {{ nowUpdatedClientNum }}명이 수정중입니다.</div>
          </div>
          <div class="modal-cardInfo">
            <div class="modal-cardInfo-header">
              <p class="titleViewer">제목 : {{ cardDetailInfo.title }}</p>
              <p class="versionNumber" v-if="recentVersionNum">버전 번호 :</p>
              <select
                class="versionList"
                v-if="recentVersionNum"
                :disabled="isShowEditor"
                v-model="selectedVersionNum"
                @change="fetchVersion"
              >
                <option
                  v-for="version in versionList"
                  :key="version.versionNum"
                  :versionList="versionList"
                >{{ version.versionNum }}</option>
              </select>
            </div>
            <div class="modal-cardInfo-parent">
              <div class="modal-cardInfo-main" v-if="isShowEditor">
                <editor
                  class="toastEditor"
                  ref="toastuiEditor"
                  height="100%"
                  :initialValue="cardDetailInfo.content"
                  :options="editorOptions"
                ></editor>
              </div>
              <div class="modal-cardInfo-main" v-else>
                <viewer
                  class="contentViewer"
                  v-if="cardDetailInfo.content != null"
                  :initialValue="cardDetailInfo.content"
                  :options="viewerOptions"
                  ref="toastuiViewer"
                />
              </div>
              <div class="modal-cardInfo-footer">
                <div class="modal-versionInfo">
                  <button
                    class="btn btn-primary btn-sm"
                    v-if="isShowEditor"
                    @click="saveCard"
                  >저 장 하 기</button>
                  <button
                    class="btn btn-danger btn-sm"
                    v-else
                    @click="editCard"
                    :disabled="recentVersionNum != selectedVersionNum"
                  >수 정 하 기</button>
                  <button
                    class="btn btn-success btn-sm"
                    v-if="!isShowEditor && recentVersionNum != selectedVersionNum"
                    @click="rollbackVersion"
                  >버 전 되 돌 리 기</button>
                </div>
                <div>
                  <p>
                    Author : {{ cardDetailInfo.author }}
                    <br />
                    Created Date :{{
                    $moment(cardDetailInfo.createdDate).format("YYYY-MM-DD HH:mm:ss")
                    }}
                  </p>
                </div>
                <div>
                  <p v-if="recentVersionNum">
                    Modifier : {{ curVersionInfo.modifier }}
                    <br />
                    Modified Date :{{
                    $moment(curVersionInfo.modifiedDate).format("YYYY-MM-DD HH:mm:ss")
                    }}
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-default-button" @click="$emit('close')">OK</button>
            </slot>
          </div>
        </div>
      </div>
      <ConflictModal
        v-if="showConflictModal"
        @close="close"
        @request="conflictRequest"
        :baseContent="this.baseContent"
        :conflictData="this.conflictData"
      ></ConflictModal>
    </div>
  </transition>
</template>

<script>
import * as ApiUrl from "../utils/ApiUrl.js";
import "codemirror/lib/codemirror.css";
import "@toast-ui/editor/dist/toastui-editor.css";
import { Editor } from "@toast-ui/vue-editor";
import { Viewer } from "@toast-ui/vue-editor";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight";
import "highlight.js/styles/github.css";
import hljs from "highlight.js";
import ConflictModal from "./ConflictModal";
import { component } from "vuedraggable";
const RECENT_VERSION_INDEX = 0;
export default {
  props: {
    cardInfo: {
      type: Object,
      required: true,
    },
  },
  components: {
    editor: Editor,
    viewer: Viewer,
    ConflictModal,
  },
  data() {
    return {
      stompClient: Object,
      usingOther: false,
      nowUpdatedClientNum: 0,
      cardDetailInfo: {},
      editor: {},
      editorOptions: {
        hideModeSwitch: true,
        plugins: [[codeSyntaxHighlight, { hljs }]],
      },
      viewer: {},
      viewerOptions: {
        plugins: [[codeSyntaxHighlight, { hljs }]],
      },
      selectedVersionNum: 0,
      versionList: [],
      curVersionInfo: {},
      recentVersionNum: 0,
      isShowRollbackButton: false,
      showConflictModal: false,
      conflictData: {},
      curContent: "",
      requestContent: "",
      isShowEditor: false,
      baseContent: String,
      isExistConflictContent: false,
      subscriptionId: 0,
      requestContent: String,
      subscriptionId: 0,
      timerId: 0,
    };
  },
  created() {
    //  TO DO - axios 처리 필요
    this.initDetailInfo();
  },
  beforeDestroy() {
    this.removeUpdateState();
  },
  methods: {
    saveContentInLocalStorage() {
      localStorage.setItem(
        this.cardDetailInfo.cardId,
        this.$refs.toastuiEditor.invoke("getMarkdown")
      );
    },
    initDetailInfo() {
      this.$http
        .get(ApiUrl.CARD_API_BASE_URL + ApiUrl.DETAIL_URL, {
          params: {
            cardId: this.cardInfo.cardId,
          },
        })
        .then((result) => {
          this.cardDetailInfo = { ...this.cardInfo, ...result.data };
          this.curContent = this.cardDetailInfo.content;
          if (this.cardDetailInfo.versionNum > 0) {
            this.$http
              .get(ApiUrl.CARD_API_BASE_URL + ApiUrl.VERSION_URL, {
                params: {
                  cardId: this.cardInfo.cardId,
                  versionNum: this.cardDetailInfo.versionNum,
                },
              })
              .then((result) => {
                console.log(result.data);
                this.versionList = result.data;
                this.selectedVersionNum = result.data[RECENT_VERSION_INDEX].versionNum;
                this.recentVersionNum = result.data[RECENT_VERSION_INDEX].versionNum;
                this.curVersionInfo.modifier = result.data[RECENT_VERSION_INDEX].modifier;
                this.curVersionInfo.modifiedDate = result.data[RECENT_VERSION_INDEX].modifiedDate;
              });
          }
          this.checkCardContent();
        });
      window.addEventListener("beforeunload", this.exceptionUpdateState);
      this.addUpdateState();
    },
    editCard() {
      this.isShowEditor = true;
      let pastContent = localStorage.getItem(this.cardDetailInfo.cardId);
      if (pastContent !== null) {
        this.cardDetailInfo.content = pastContent;
        alert("작성중인 내용을 불러왔습니다.");
      }
      this.timerId = setInterval(() => {
        this.saveContentInLocalStorage();
      }, 3000);
    },
    checkCardContent() {
      if (!this.cardDetailInfo.content) {
        this.cardDetailInfo.content =
          "### 게시글 내용이 비어 있습니다. \n 수정하기를 눌러 게시글에 내용을 추가 해주세요.";
      }
    },
    // TO DO
    saveCard() {
      localStorage.removeItem(this.cardDetailInfo.cardId);
      clearTimeout(this.timerId);
      this.isShowEditor = false;
      if (
        this.curContent === this.$refs.toastuiEditor.invoke("getMarkdown") &&
        !this.isExistConflictContent
      ) {
        // do nothing
      } else {
        if (!this.isExistConflictContent) {
          this.requestContent = this.$refs.toastuiEditor.invoke("getMarkdown");
        } else {
          this.isExistConflictContent = false;
        }
        this.$http
          .patch(ApiUrl.CARD_API_BASE_URL, {
            content: this.requestContent,
            cardId: this.cardDetailInfo.cardId,
            versionNum: this.recentVersionNum + 1,
            modifier: this.cardDetailInfo.author,
            title: this.cardDetailInfo.title,
          })
          .then((result) => {
            console.log(result.data);
            this.curContent = result.data.content;
            this.cardDetailInfo.content = result.data.content;
            this.$refs.toastuiViewer.invoke("setMarkdown", result.data.content, true);
            this.selectedVersionNum =
              result.data.basicVersionInfos[RECENT_VERSION_INDEX].versionNum;
            this.recentVersionNum = result.data.basicVersionInfos[RECENT_VERSION_INDEX].versionNum;
            this.curVersionInfo.modifier =
              result.data.basicVersionInfos[RECENT_VERSION_INDEX].modifier;
            this.curVersionInfo.modifiedDate =
              result.data.basicVersionInfos[RECENT_VERSION_INDEX].modifiedDate;
            this.versionList = result.data.basicVersionInfos;
          })
          .catch((error) => {
            alert("Conflict!");
            console.log(error.response);
            this.baseContent = error.response.data.baseContent;
            this.conflictData.insertCnt = error.response.data.insertCnt;
            this.conflictData.recentContent = error.response.data.recentContent;
            this.conflictData.myContent = error.response.data.myContent;
            this.conflictData.recentVersionNum = error.response.data.recentVersionNum;
            this.showConflictModal = true;
            this.isShowEditor = true;
          });
      }
    },
    fetchVersion() {
      if (this.selectedVersionNum != this.recentVersionNum) {
          this.isShowRollbackButton = true;
        } else {
        this.isShowRollbackButton = false;
      }
        this.$http
          .get(ApiUrl.CARD_API_BASE_URL + ApiUrl.VERSION_FETCH_URL, {
            params: {
              cardId: this.cardDetailInfo.cardId,
              versionNum: this.selectedVersionNum,
            },
          })
          .then((result) => {
            this.curVersionInfo = result.data;
            this.$refs.toastuiViewer.invoke("setMarkdown", result.data.content, true);
          });
    },
    rollbackVersion() {
      console.log("Rollback!");
      this.$http
        .patch(ApiUrl.CARD_API_BASE_URL + ApiUrl.VERSION_ROLLBACK_URL, {
          cardId: this.cardDetailInfo.cardId,
          rollbackVersionNum: this.selectedVersionNum,
        })
        .then((result) => {
          if (this.isShowRollbackButton) this.isShowRollbackButton = false;
          this.selectedVersionNum = result.data[RECENT_VERSION_INDEX].versionNum;
          this.recentVersionNum = result.data[RECENT_VERSION_INDEX].versionNum;
          this.curVersionInfo.modifier = result.data[RECENT_VERSION_INDEX].modifier;
          this.curVersionInfo.modifiedDate = result.data[RECENT_VERSION_INDEX].modifiedDate;
          this.versionList = result.data;
        });
    },
    addUpdateState() {
      this.stompClient = this.$store.state.stompClient;
      this.stompClient.subscribe(
        ApiUrl.CARD_DETAIL_SUBSCRIPTION_BASE_URL + "/" + this.cardInfo.cardId,
        (res) => {
          this.subscriptionId = res.headers.subscription;
          this.nowUpdatedClientNum = JSON.parse(res.body).nowUpdatedClientNum;
          if (this.nowUpdatedClientNum > 1) {
            this.usingOther = true;
          } else {
            this.usingOther = false;
          }
        }
      );
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(
          ApiUrl.CARD_DETAIL_PUBLISH_BASE_URL,
          JSON.stringify({ cardId: this.cardInfo.cardId, isUpdating: true })
        );
      }
    },
    removeUpdateState() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(
          ApiUrl.CARD_DETAIL_PUBLISH_BASE_URL,
          JSON.stringify({ cardId: this.cardInfo.cardId, isUpdating: false })
        );
      }
      const subscriptions = this.stompClient.subscriptions;
      this.stompClient.unsubscribe(this.subscriptionId);
    },
    exceptionUpdateState() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(
          ApiUrl.CARD_DETAIL_PUBLISH_BASE_URL,
          JSON.stringify({ cardId: this.cardInfo.cardId, isUpdating: false })
        );
      }
      this.stompClient.disconnect();
    },
    close() {
      this.showConflictModal = false;
      this.cardDetailInfo = {};
      this.initDetailInfo();
      this.isShowEditor = false;
    },
    conflictRequest(conflictRequest) {
      this.showConflictModal = false;
      this.recentVersionNum = conflictRequest.recentVersionNum;
      this.requestContent = conflictRequest.content;
      this.isExistConflictContent = true;
      this.saveCard();
    },
  },
};
</script>
<style src="../assets/css/CardDetailModal.css"></style>
