<template>
  <div class="board">
    <div class="topboard">
      <h6 class="status">{{ boardList.status }}</h6>
      <button class="btn btn-danger btn-sm" @click="deleteBoard">Board Delete</button>
      <button class="btn btn-primary btn-sm" @click="updateBoard">Board Update</button>
      <div class="input-group">
        <input
          type="text"
          v-if="!updateTrigger"
          class="form-control"
          aria-label="status"
          placeholder="Enter a modified board status"
          aria-describedby="status"
          v-model="modifiedBoard.status"
        />
      </div>
      <!-- <input type="text" v-if="!updateTrigger" v-model="modifiedBoard.status" /> -->
    </div>
    <div class="middleboard">
      <draggable
        ghost-class="ghost"
        :list="cardList"
        group="card"
        @change="changeOrder"
        v-bind="dragOptions"
        @start="drag = true"
        @end="drag = false"
        :emptyInsertThreshold="100"
      >
        <transition-group type="transition" :name="!drag ? 'flip-list' : null">
          <card v-for="card in cardList" :key="card.cardId" :card="card" @deleteCard="deleteCard"></card>
        </transition-group>
      </draggable>
      <infinite-loading direction="bottom" @infinite="infiniteHandler">
        <span slot="no-more"></span>
        <span slot="no-results"></span>
      </infinite-loading>
    </div>
    <div class="bottomboard">
      <add-card
        :boardId="boardList.boardId"
        @addNewCard="addNewCard"
        @checkIsCardChange="checkIsCardChange"
      ></add-card>
    </div>
  </div>
</template>

<script>
import * as ApiUrl from "../utils/ApiUrl.js";
import card from "./Card.vue";
import addCard from "./AddCard.vue";
import draggable from "vuedraggable";
import axios from "axios";
import infiniteLoading from "vue-infinite-loading";

export default {
  props: {
    boardList: {
      type: Object,
      required: true,
    },
  },
  components: {
    card,
    addCard,
    draggable,
    infiniteLoading,
  },
  data() {
    return {
      cardList: [],
      changedStatus: "",
      updateTrigger: true,
      moveToBoard: false,
      modifiedBoard: [],
      addCardInfo: {},
      page: 0,
      oldIndex: 0,
      newIndex: 0,
      newBoardId: 0,
      drag: false,
    };
  },
  computed: {
    dragOptions() {
      return {
        animation: 200,
      };
    },
  },
  methods: {
    updateBoard() {
      if (this.updateTrigger) this.updateTrigger = false;
      else {
        this.updateTrigger = true;
        if (this.modifiedBoard.status !== undefined) {
          this.modifiedBoard.boardId = this.boardList.boardId;
          this.$emit("modifyBoard", this.modifiedBoard);
          this.modifiedBoard = [];
        }
      }
    },
    deleteBoard() {
      this.$emit("deleteBoard", this.boardList.boardId);
    },

    deleteCard(deleteId) {
      this.$emit("checkIsCardChange");
      this.$http
        .delete(ApiUrl.CARD_API_BASE_URL, {
          params: {
            cardId: deleteId,
            socketIdentifier: localStorage.getItem("socketIdentifier"),
          },
        })
        .then((result) => {
          let index = this.cardList.findIndex(function (item) {
            return item.cardId === deleteId;
          });

          this.cardList.splice(index, 1);
        })
        .catch((error) => {
          alert("socket error!");
          console.log(error);
          location.reload(true);
        });
    },
    changeOrder(e) {
      let cardPosition = {};
      let apiCode;
      if (e.moved) {
        this.oldIndex = e.moved.oldIndex;
        this.newIndex = e.moved.newIndex;
        if (Math.abs(this.newIndex - this.oldIndex) === 1) {
          if (this.newIndex > this.oldIndex) {
            cardPosition.upPosCardId = this.cardList[this.oldIndex].cardId;
            cardPosition.downPosCardId = this.cardList[this.newIndex].cardId;
          } // 위에서 아래로 옮겼을 때
          else {
            cardPosition.upPosCardId = this.cardList[this.newIndex].cardId;
            cardPosition.downPosCardId = this.cardList[this.oldIndex].cardId;
          }
          cardPosition.originCardId = this.cardList[this.newIndex].cardId;
          cardPosition.code = ApiUrl.MOVE_TO_NEXT;
        }
      } else {
        if (e.added) {
          this.newIndex = e.added.newIndex;
          this.newBoardId = this.boardList.boardId;
          if (this.cardList.length == 1) {
            cardPosition.originCardId = this.cardList[this.newIndex].cardId;
            cardPosition.newBoardId = this.boardList.boardId;
            cardPosition.code = ApiUrl.MOVE_EMPTY_BOARD;
          }
        } else {
          return;
        }
      }

      if (Object.keys(cardPosition).length === 0) {
        if (this.newIndex == this.cardList.length - 1) {
          cardPosition.originCardId = this.cardList[this.newIndex].cardId;
          cardPosition.upPosCardId = this.cardList[this.newIndex - 1].cardId;
          cardPosition.code = ApiUrl.MOVE_TO_LAST;
        } else {
          cardPosition.originCardId = this.cardList[this.newIndex].cardId;
          cardPosition.downPosCardId = this.cardList[this.newIndex + 1].cardId;
          cardPosition.code = ApiUrl.MOVE_TO_MIDDLE;
        }
      }
      cardPosition.oldIndex = this.oldIndex;
      cardPosition.newIndex = this.newIndex;
      cardPosition.newBoardId = this.newBoardId;
      cardPosition.socketIdentifier = localStorage.getItem("socketIdentifier");
      this.$emit("checkIsCardChange");
      this.$http
        .patch(ApiUrl.CARD_API_BASE_URL + ApiUrl.ORDER_URL, cardPosition)
        .then((result) => {
          console.log("수정된 card 개수" + result.data);
        })
        .catch((error) => {
          alert("socket error!");
          console.log(error);
          location.reload(true);
        });
    },
    infiniteHandler($state) {
      this.$http
        .get(ApiUrl.CARD_API_BASE_URL, {
          params: {
            boardId: this.boardList.boardId,
            offset: this.page,
          },
        })
        .then((result) => {
          if (result.data.length) {
            console.log("card paging loding");
            this.page = result.data[result.data.length - 1].cardId;
            this.cardList = this.cardList.concat(result.data);
            $state.loaded();
          } else {
            console.log("card paging done");
            $state.complete();
          }
        });
    },
    addNewCard(newCard) {
      this.cardList.unshift(newCard);
    },
    checkIsCardChange() {
      this.$emit("checkIsCardChange");
    },
  },
};
</script>
<style>
.status {
  font-weight: bold;
  font-size: 20px;

  text-transform: uppercase;
  background-color: transparent !important;
}
.board {
  /*뷰의 전체 픽셀의 20%*/
  width: 20vw;
  height: 100%;
  background-color: #d8d8d8;
  border: 1px solid #c4bdbd;
  margin: 20px;
  box-sizing: border-box;
  cursor: pointer;
  display: inline-grid;
  border-radius: 15px;
  grid-template-rows: 10% 80% 10%;
  box-shadow: 2px 2px 5px #999;
}
.topboard {
  background-color: transparent !important;
  border-radius: 50px;
  width: 100%;
  margin: 0;
  padding: 10px;
  height: 15%;
}
.middleboard {
  background-color: #d8d8d8;
  width: 100%;
  margin: 0;
  padding: 0;
  /* height: 65%; */
  overflow-y: scroll;
}
.bottomboard {
  background-color: #9c9ba0;
  width: 100%;
  margin: 0;
  padding: 0;
  /*font-size: larger;
  text-align: center; */
  /* height: 20%; */
}
.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}
</style>
