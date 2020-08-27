<template>
  <div class="card">
    <p class="cardTitle">{{ card.title }}</p>
    <p class="cardAuthor">{{ card.author }}</p>
    <div>
      <button class="cardButton delete" @click="deleteCard()">delete</button>
      <button class="cardButton modify" @click="showModalTrigger()">modify</button>
    </div>
    <CardDetailModal v-if="showModal" @close="showModal = false" :cardInfo="this.card">
      <h3 slot="header">custom header</h3>
    </CardDetailModal>
  </div>
</template>

<script>
import CardDetailModal from "./CardDetailModal";
import * as ApiUrl from "../utils/ApiUrl.js";
import { Viewer } from "@toast-ui/vue-editor";

export default {
  props: {
    card: {
      type: Object,
      required: true,
    },
  },
  name: "card",
  components: {
    viewer: Viewer,
    CardDetailModal,
  },
  data() {
    return {
      showModal: false,
    };
  },
  created() {},
  methods: {
    showModalTrigger() {
      if (!this.showModal) {
        this.showModal = true;
      } else {
        this.showModal = false;
      }
    },
    deleteCard() {
      event.stopPropagation();
      this.$emit("deleteCard", this.card.cardId);
    },
  },
};
</script>

<style>
.cardButton {
  width: 20%;
  border-radius: 10px;
  margin: 0 10px 5px 0;
  background-color: rgb(226, 153, 169);
  border: 2px solid rgb(231, 164, 164);
  color: #fff;
  line-height: 30px;
  float: right;
}
.cardButton:hover {
  background-color: #fff;
  border-color: #59b1eb;
  color: #59b1eb;
}
.modify {
  background-color: rgb(111, 150, 233);
  border-color: #59b1eb;
}
.delete:hover {
  background-color: #fff;
  border-color: rgb(226, 153, 169);
  color: rgb(226, 153, 169);
}
.card {
  overflow: scroll;
  background-color: white;
  width: 95%;
  margin: 10px;
  height: 10%;
  cursor: pointer;
  border-radius: 15px;
  border-color: #8a9197;
  box-shadow: 2px 2px 5px #999;
}
.author p {
  font-size: x-small;
  color: #6c757d;
  text-align: right;
}
.cardTitle {
  margin-left: 15px;
  margin-top: 15px;
}
.cardAuthor {
  margin-right: 15px;
  text-align: right;
  color: darkgray;
}
</style>
