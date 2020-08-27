<template>
  <div class="history">
    <div v-if="history.actionName == 'moved'">
      <div v-if="history.beforeBoardTitle">
        <span class="text-primary">{{ history.actionName }}</span>
        {{ history.modifiedEntity }}
        <span
          class="bodyText"
        >{{ Number(history.beforeData) + 1 }}</span> th in
        <span class="bodyText">{{ history.beforeBoardTitle }}</span> To
        <span class="bodyText">{{ Number(history.afterData) + 1 }}</span> th in
        <span class="bodyText">{{ history.afterBoardTitle }}</span>
        <span class="date">{{ $moment(history.modifiedTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
      </div>
      <div v-else>
        <span class="text-primary">{{ history.actionName }}</span>
        {{ history.modifiedEntity }}
        <span class="bodyText">{{ history.nowEntityTitle }}</span> From
        <span class="bodyText">{{ Number(history.beforeData) + 1 }}</span> th to
        <span class="bodyText">{{ Number(history.afterData) + 1 }}</span> th
        <span class="date">{{ $moment(history.modifiedTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
      </div>
    </div>
    <div v-else-if="history.actionName == 'updated'">
      <span class="text-info">{{ history.actionName }}</span>
      {{ history.modifiedEntity }} from
      <span class="bodyText">{{ history.beforeData }}</span> to
      <span class="bodyText">{{ history.afterData }}</span>
      <span class="date">{{ $moment(history.modifiedTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
    </div>
    <div v-else-if="history.actionName == 'added'">
      <span class="text-success">{{ history.actionName }}</span>
      {{ history.modifiedEntity }}
      <span class="bodyText">{{ history.nowEntityTitle }}</span>
      <span class="bodyText">{{ history.beforeData }}</span>
      <span class="date">{{ $moment(history.modifiedTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
    </div>
    <div v-else-if="history.actionName == 'removed'">
      <span class="text-danger">{{ history.actionName }}</span>
      {{ history.modifiedEntity }}
      <span class="bodyText">{{ history.nowEntityTitle }}</span>
      <span class="date">{{ $moment(history.modifiedTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    history: {
      type: Object,
      required: true,
    },
  },
  name: "history",
};
</script>

<style>
.history {
  margin: 10px;
  font-size: 25px;
}
.date {
  float: right;
}
.bodyText {
  font-size: 30px;
  font-weight: bold;
}
</style>
