<template>
  <div v-if="lineInfo.operation === 'CHANGE'" class="versionDiffLineInsert">
    <viewer class="changeLine" :initialValue="lineInfo.lineContent"> </viewer>
    <button
      type="button"
      class="btn btn-primary btn-xs"
      v-clipboard:copy="lineInfo.lineContent"
      v-clipboard:success="onCopy"
    >
      Copy
    </button>
    <button type="button" class="btn btn-secondary btn-xs" @click="changeOperation">x</button>
  </div>
  <div v-else class="versionDiffLine">
    <viewer :initialValue="lineInfo.lineContent"></viewer>
  </div>
</template>
<script>
import { Viewer } from "@toast-ui/vue-editor";

export default {
  props: {
    lineInfo: {
      type: Object,
      required: true,
    },
  },
  components: {
    viewer: Viewer,
  },
  created() {
    console.log(this.lineInfo);
  },
  methods: {
    onCopy(e) {
      alert("복사되었습니다.");
      this.changeOperation();
    },
    changeOperation() {
      this.$emit("changeOperation", this.lineInfo.lineNum);
    },
  },
};
</script>
<style>
.versionDiffLine {
  width: 100%;
  min-height: 3%;
}
.versionDiffLineInsert {
  width: 100%;
  min-height: 3%;
  background-color: #c7f3c7;
}
.changeLine {
  min-height: 2vh;
  background-color: #c7f3c7;
}
.tui-editor-contents {
  overflow: inherit;
}
/* .tui-editor-contents p {
  margin: 0;
} */
.btn-group-xs > .btn,
.btn-xs {
  padding: 0.25rem 0.4rem;
  font-size: 0.875rem;
  line-height: 0.5;
  border-radius: 0.2rem;
}
</style>
