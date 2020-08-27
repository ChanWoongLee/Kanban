<template>
  <div class="mainboard">
    <div class="mainboardheader">
      <h3 class="font-weight-light text-black">Kanban Board</h3>
      <button class="btn btn-success btn-sm" @click="addBoard">Board Add</button>
      <button class="btn btn-info" @click="showAdminHistoryModalTrigger()">
        admin이력 확인하기
      </button>
      <adminHistoryModal
        v-if="showAdminHistoryModal"
        @close="showAdminHistoryModal = false"
      ></adminHistoryModal>
      <button class="btn btn-info" @click="showClientHistoryModalTrigger()">
        client이력 확인하기
      </button>
      <clientHistoryModal
        v-if="showClientHistoryModal"
        @close="showClientHistoryModal = false"
      ></clientHistoryModal>
    </div>
    <div class="mainboardbody">
      <draggable
        ghost-class="ghost"
        :list="boardList"
        @change="changeOrder"
        v-bind="dragOptions"
        @start="drag = true"
        @end="drag = false"
      >
        <transition-group type="transition" :name="!drag ? 'flip-list' : null">
          <board
            v-for="boardList in boardList"
            :key="boardList.boardId"
            :boardList="boardList"
            @modifyBoard="modifyBoard"
            @deleteBoard="deleteBoard"
            @checkIsCardChange="checkIsCardChange"
            ref="boardInfo"
          ></board>
        </transition-group>
        <!-- board body 부분 -->
      </draggable>
      <infinite-loading-extra direction="right" @infinite="infiniteHandler">
        <span slot="no-more"></span>
        <span slot="no-results"></span>
      </infinite-loading-extra>
    </div>
  </div>
</template>

<script>
import * as ApiUrl from "../utils/ApiUrl.js";
import board from "./Board.vue";
import adminHistoryModal from "./AdminHistoryModal";
import clientHistoryModal from "./ClientHistoryModal";
import draggable from "vuedraggable";
import infiniteLoadingExtra from "../plugins/infiniteloadingextra.js";
import axios from "axios";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";

export default {
  name: "boardmain",
  components: {
    board,
    draggable,
    infiniteLoadingExtra,
    adminHistoryModal,
    clientHistoryModal,
  },
  data() {
    return {
      showDiffModal: false,
      showAdminHistoryModal: false,
      showClientHistoryModal: false,
      boardList: [],
      page: 0,
      isBoardChange: false,
      isCardChange: false,
      drag: false,
    };
  },
  created() {
    this.connect();
  },
  computed: {
    dragOptions() {
      return {
        animation: 200,
      };
    },
  },
  methods: {
    checkIsCardChange() {
      this.isCardChange = !this.isCardChange;
    },
    showAdminHistoryModalTrigger() {
      this.showAdminHistoryModal = !this.showAdminHistoryModal;
    },
    showClientHistoryModalTrigger() {
      this.showClientHistoryModal = !this.showClientHistoryModal;
    },
    connect() {
      let socket = new SockJS(ApiUrl.SERVER_URL);
      this.stompClient = Stomp.over(socket);
      console.log("소켓 연결을 시도합니다. 서버 주소: ${ApiUrl.SERVER_URL}");
      this.stompClient.connect(
        {},
        (frame) => {
          this.connected = true;
          console.log("소켓 연결 성공", frame);
          this.$store.commit("connect", this.stompClient);
          this.stompClient.subscribe("/sub/identifier", (res) => {
            localStorage.setItem("socketIdentifier", JSON.parse(res.body).socketIdentifier);
          });
          this.stompClient.send("/pub/identifier");
          this.stompClient.subscribe(ApiUrl.BOARD_SUBSCRIPTION_BASE_URL, (res) => {
            localStorage.setItem("socketIdentifier", JSON.parse(res.body).socketIdentifier);
            if (this.isBoardChange) {
              this.isBoardChange = false;
              return;
            }
            switch (JSON.parse(res.body).actionName) {
              case "added":
                this.boardList.unshift(JSON.parse(res.body));
                break;
              case "removed":
                let removedBoardId = JSON.parse(res.body).boardId;
                var index = this.boardList.findIndex((item) => item.boardId === removedBoardId);
                if (index != -1) {
                  this.boardList.splice(index, 1);
                }
                break;
              case "moved":
                let newIndex = JSON.parse(res.body).newIndex;
                let oldIndex = JSON.parse(res.body).oldIndex;
                let tempObj = this.boardList.splice(oldIndex, 1);
                this.boardList.splice(newIndex, 0, tempObj[0]);
                break;
            }
          });

          this.stompClient.subscribe(ApiUrl.CARD_SUBSCRIPTION_BASE_URL, (res) => {
            localStorage.setItem("socketIdentifier", JSON.parse(res.body).socketIdentifier);
            if (this.isCardChange) {
              this.isCardChange = false;
              return;
            }
            let indexOfNowBoard;
            switch (JSON.parse(res.body).actionName) {
              case "added":
                let addedCard = JSON.parse(res.body).newCard;
                indexOfNowBoard = this.$refs.boardInfo.findIndex(
                  (item) => item.boardList.boardId === addedCard.boardId
                );
                this.$refs.boardInfo[indexOfNowBoard].cardList.unshift(addedCard);
                break;
              case "removed":
                let removeCardMessage = JSON.parse(res.body);
                indexOfNowBoard = this.$refs.boardInfo.findIndex(
                  (item) => item.boardList.boardId === removeCardMessage.nowBoardId
                );
                let indexOfremovedCard = this.$refs.boardInfo[indexOfNowBoard].cardList.findIndex(
                  (item) => item.cardId === removeCardMessage.cardId
                );
                this.$refs.boardInfo[indexOfNowBoard].cardList.splice(indexOfremovedCard, 1);
                break;
              case "moved":
                let movedCardMessage = JSON.parse(res.body);

                indexOfNowBoard = this.$refs.boardInfo.findIndex(
                  (item) => item.boardList.boardId === movedCardMessage.originBoardId
                );
                let indexOfCard = this.$refs.boardInfo[indexOfNowBoard].cardList.findIndex(
                  (item) => item.cardId === movedCardMessage.cardId
                );
                let tempObj = this.$refs.boardInfo[indexOfNowBoard].cardList.splice(indexOfCard, 1);
                if (movedCardMessage.newBoardId != 0) {
                  indexOfNowBoard = this.$refs.boardInfo.findIndex(
                    (item) => item.boardList.boardId === movedCardMessage.newBoardId
                  );
                }
                this.$refs.boardInfo[indexOfNowBoard].cardList.splice(
                  movedCardMessage.newIndex,
                  0,
                  tempObj[0]
                );
            }
          });
        },
        (error) => {
          console.log("소켓 연결 실패", error);
          this.connected = false;
        }
      );
    },
    addBoard() {
      let newBoard = {};
      newBoard.status = "new board";
      this.$http
        .post(ApiUrl.BOARD_API_BASE_URL, {
          status: newBoard.status,
          socketIdentifier: localStorage.getItem("socketIdentifier"),
        })
        .then((result) => {
          console.log(result.data);
          newBoard.boardId = result.data;
          this.boardList.unshift(newBoard);
        })
        .catch((error) => {
          alert("socket error!");
          console.log(error);
          location.reload(true);
        });
      this.isBoardChange = true;
    },
    modifyBoard(modifiedBoard) {
      this.$http
        .patch(ApiUrl.BOARD_API_BASE_URL + "/" + modifiedBoard.boardId, {
          status: modifiedBoard.status,
        })
        .then((result) => {
          var index = this.boardList.findIndex(function (item) {
            return item.boardId === modifiedBoard.boardId;
          });
          this.boardList[index].status = modifiedBoard.status;
        });
    },
    deleteBoard(deleteId) {
      this.$http
        .delete(ApiUrl.BOARD_API_BASE_URL, {
          params: {
            boardId: deleteId,
            socketIdentifier: localStorage.getItem("socketIdentifier"),
          },
        })
        .then((result) => {
          var index = this.boardList.findIndex(function (item) {
            return item.boardId === deleteId;
          });
          this.boardList.splice(index, 1);
        })
        .catch((error) => {
          alert("socket error!");
          console.log(error);
          location.reload(true);
        });
      this.isBoardChange = true;
    },
    changeOrder(e) {
      let boardPosition = {};
      let newIndex;
      let apiCode;
      let oldIndex;
      if (e.moved) {
        oldIndex = e.moved.oldIndex;
        newIndex = e.moved.newIndex;
        if (Math.abs(newIndex - oldIndex) == 1) {
          if (newIndex > oldIndex) {
            boardPosition.upPosBoardId = this.boardList[oldIndex].boardId;
            boardPosition.downPosBoardId = this.boardList[newIndex].boardId;
          } else {
            boardPosition.upPosBoardId = this.boardList[newIndex].boardId;
            boardPosition.downPosBoardId = this.boardList[oldIndex].boardId;
          }
          boardPosition.code = ApiUrl.MOVE_TO_NEXT;
        }
      }
      if (Object.keys(boardPosition).length == 0) {
        if (newIndex == this.boardList.length - 1) {
          boardPosition.originBoardId = this.boardList[newIndex].boardId;
          boardPosition.upPosBoardId = this.boardList[newIndex - 1].boardId;
          boardPosition.code = ApiUrl.MOVE_TO_LAST;
        } else {
          boardPosition.originBoardId = this.boardList[newIndex].boardId;
          boardPosition.downPosBoardId = this.boardList[newIndex + 1].boardId;
          boardPosition.code = ApiUrl.MOVE_TO_MIDDLE;
        }
      }
      boardPosition.oldIndex = oldIndex;
      boardPosition.newIndex = newIndex;
      boardPosition.socketIdentifier = localStorage.getItem("socketIdentifier");
      this.$http
        .patch(ApiUrl.BOARD_API_BASE_URL + ApiUrl.ORDER_URL, boardPosition)
        .then((result) => {
          console.log("수정된 board 개수: " + result.data);
        })
        .catch((error) => {
          alert("socket error!");
          console.log(error);
          location.reload(true);
        });
      this.isBoardChange = true;
    },
    infiniteHandler($state) {
      this.$http
        .get(ApiUrl.BOARD_API_BASE_URL, {
          params: {
            offset: this.page,
          },
        })
        .then((result) => {
          if (result.data.length) {
            console.log("board paging loding");
            this.page = result.data[result.data.length - 1].boardId;
            this.boardList = this.boardList.concat(result.data);
            $state.loaded();
          } else {
            console.log("board paging done");
            $state.complete();
          }
        });
    },
  },
};
</script>

<style>
.mainboard {
  width: 100%;
  height: 100%;
}
.mainboardheader {
  width: 100%;
  height: 15%;
  background-color: white;
  margin: 0;
  padding: 10px;
}
.mainboardbody {
  width: 100;
  height: 80%;
  background-color: white;
  margin: 0;
  padding: 10px;
  overflow-y: hidden;
  overflow-x: auto;
  white-space: nowrap;
  display: flex;
}
.mainboardbody > div {
  height: 100%;
}
.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}
</style>
