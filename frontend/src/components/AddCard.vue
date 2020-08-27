<template>
  <div class="addCard" v-if="addCardTrigger" @click="addCard">
    <p>Add Card</p>
  </div>
  <div class="addCard" v-else>
    <div class="input-group">
      <input
        type="text"
        class="form-control"
        aria-label="author"
        placeholder="Enter a author for this card"
        aria-describedby="author"
        v-model="addCardInfo.author"
      />
    </div>
    <div class="input-group">
      <input
        type="text"
        class="form-control"
        aria-label="title"
        placeholder="Enter a title for this card"
        aria-describedby="title"
        v-model="addCardInfo.title"
      />
      <button class="btn btn-secondary" @click="addCard">Add</button>
    </div>
  </div>
</template>
<script>
import * as ApiUrl from "../utils/ApiUrl.js";
export default {
  props: {
    boardId: {
      type: Number,
      required: true,
    },
  },
  name: "addCard",
  components: {},
  data: function () {
    return {
      addCardTrigger: true,
      addCardInfo: {},
    };
  },

  methods: {
    addCard() {
      if (this.addCardTrigger) {
        this.addCardTrigger = false;
      } else {
        this.$emit("checkIsCardChange");
        this.addCardInfo.boardId = this.boardId;
        this.$http
          .post(ApiUrl.CARD_API_BASE_URL, {
            author: this.addCardInfo.author,
            title: this.addCardInfo.title,
            boardId: this.addCardInfo.boardId,
            socketIdentifier: localStorage.getItem("socketIdentifier"),
          })
          .then((result) => {
            console.log("card add success!");
            this.$emit("addNewCard", result.data);
          })
          .catch((error) => {
            alert("socket error!");
            console.log(error);
            location.reload(true);
          });
        this.addCardInfo = [];
        this.addCardTrigger = true;
      }
    },
  },
};
</script>
<style>
.addCard {
  background-color: transparent !important;
  width: 100%;
  margin: 0;
  padding: 0;
  font-size: larger;
  text-align: center;
}
</style>
