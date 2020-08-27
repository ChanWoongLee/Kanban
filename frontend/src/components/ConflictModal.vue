<template>
  <transition name="modal">
    <div class="modal-mask">
      <div class="modal-wrapper">
        <div class="conflict-modal-container">
          <div class="modal-header">
            <div name="header" style="font-size: 24px;">Conflict Diff</div>
          </div>
          <div class="modal-info">
            <VersionDiff
              :initialDiffData="this.conflictData.myContent"
              title="MyContent"
              @minusInsertCnt="minusInsertCnt"
            ></VersionDiff>
            <div class="baseContent">
              <editor
                class="toastEditor"
                ref="toastuiEditor"
                :initialValue="this.baseContent"
                :options="editorOptions"
                height="100%"
              ></editor>
            </div>
            <VersionDiff
              :initialDiffData="this.conflictData.recentContent"
              title="RecentContent"
              @minusInsertCnt="minusInsertCnt"
            ></VersionDiff>
          </div>
          <div class="modal-footer">
            <button class="modal-default-button" @click="$emit('close')">Close</button>
            <button v-if="insertCnt != 0" class="modal-default-button" @click="request" disabled>
              Apply
            </button>
            <button v-else class="modal-default-button" @click="request">
              Apply
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
import * as ApiUrl from "../utils/ApiUrl.js";
import "codemirror/lib/codemirror.css";
import "@toast-ui/editor/dist/toastui-editor.css";
import { Viewer } from "@toast-ui/vue-editor";
import { Editor } from "@toast-ui/vue-editor";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight";
import "highlight.js/styles/github.css";
import hljs from "highlight.js";
import VersionDiff from "./VersionDiff";
export default {
  props: {
    baseContent: {
      type: String,
      required: true,
    },
    conflictData: {
      type: Object,
      required: true,
    },
  },
  components: {
    editor: Editor,
    viewer: Viewer,
    VersionDiff,
  },
  data() {
    return {
      editorOptions: {
        hideModeSwitch: true,
        plugins: [[codeSyntaxHighlight, { hljs }]],
      },
      insertCnt: this.conflictData.insertCnt,
      conflictRequest: {},
    };
  },
  created() {
    console.log(this.baseContent);
    console.log(this.conflictData);
  },
  mounted() {
    //동시 스크롤 구현
  },
  destroyed() {},
  methods: {
    minusInsertCnt() {
      this.insertCnt--;
    },
    request() {
      this.conflictRequest.content = this.$refs.toastuiEditor.invoke("getMarkdown");
      this.conflictRequest.recentVersionNum = this.conflictData.recentVersionNum;
      this.$emit("request", this.conflictRequest);
    },
  },
};
</script>
<style src="../assets/css/ConflictModal.css"></style>
