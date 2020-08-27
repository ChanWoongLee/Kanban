<template>
  <div class="versionDiff">
    <div class="versionDiffHeader">{{ title }}</div>
    <div class="versionDiffBody">
      <VersionDiffLine
        v-for="diff in diffData"
        :key="diff.lineNum"
        :lineInfo="diff"
        @changeOperation="changeOperation"
      ></VersionDiffLine>
    </div>
  </div>
</template>
<script>
import VersionDiffLine from "./VersionDiffLine";
export default {
  props: {
    initialDiffData: {
      type: Array,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      diffData: this.initialDiffData,
    };
  },
  components: {
    VersionDiffLine,
  },
  created() {
    console.log(this.diffData);
  },
  methods: {
    changeOperation(lineNum) {
      let index = this.diffData.findIndex(function (item) {
        return item.lineNum === lineNum;
      });
      var originDiff = this.diffData[index];
      originDiff.operation = "EQUAL";
      this.diffData.splice(index, 1, originDiff);

      //emit insert cnt 감소
      this.$emit("minusInsertCnt");
    },
  },
};
</script>
<style>
.versionDiff {
  flex-basis: 25%;
  height: 100%;
  margin: 3px;
}
.versionDiffHeader {
  width: 100%;
  height: 5%;
  text-align: center;
  border: 1px solid black;
}
.versionDiffBody {
  width: 100%;
  height: 94%;
  border: 1px solid black;
  overflow: scroll;
}
</style>
