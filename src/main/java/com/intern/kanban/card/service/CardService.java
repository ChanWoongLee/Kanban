package com.intern.kanban.card.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intern.kanban.board.model.dto.MoveCode;
import com.intern.kanban.board.service.BoardService;
import com.intern.kanban.card.dao.CardDAO;
import com.intern.kanban.card.model.Card;
import com.intern.kanban.card.model.dto.AddCard;
import com.intern.kanban.card.model.dto.BasicCardInfo;
import com.intern.kanban.card.model.dto.CardPosition;
import com.intern.kanban.card.model.dto.DetailCardInfo;
import com.intern.kanban.history.model.ActionNameCase;
import com.intern.kanban.history.model.LoggedEvent;
import com.intern.kanban.history.model.MovedInfo;
import com.intern.kanban.history.service.HistoryService;
import com.intern.kanban.history.service.MoveInfoService;
import com.intern.kanban.version.dao.VersionDAO;

@Service
public class CardService {

	private static final int CARD_PAGING_LIMIT = 10;
	private static final long DEFAULT_VERSION = 0L;

	@Autowired
	CardDAO cardDAO;

	@Autowired
	VersionDAO versionDAO;

	@Autowired
	HistoryService historyService;

	@Autowired
	MoveInfoService moveInfoService;

	@Autowired
	BoardService boardService;

	public void createTable() {
		cardDAO.createTable();
	}

	@Transactional
	public Card saveCard(AddCard addCard) {
		Card newCard = new Card(addCard.getAuthor(), addCard.getTitle(), addCard.getBoardId(),
				MoveCode.POS_OF_TOP.getCode(),
				LocalDateTime.now(), DEFAULT_VERSION);
		Card topPosCard = cardDAO.selectFirstByBoardId(addCard.getBoardId());
		long newCardId = cardDAO.insert(newCard);

		if (!isBoardEmpty(topPosCard)) {
			updatePrevCardId(topPosCard.getCardId(), newCardId);
		}
		saveCardEvent(
				ActionNameCase.ADD.getValue(),
				newCardId,
				"cardId",
				addCard.getTitle(),
				null,
				String.valueOf(newCardId));

		return cardDAO.selectById(newCardId);
	}

	private boolean isBoardEmpty(Card card) {
		return card == null;
	}

	public Card findById(long cardId) {
		return cardDAO.selectById(cardId);
	}

	@Transactional
	public List<BasicCardInfo> findByBoardIdForPaging(long boardId, int offset) {

		checkDataLoss(boardId, offset);

		Map<String, Object> param = new HashMap<>();
		param.put("boardId", boardId);
		param.put("offset", offset);
		param.put("limit", CARD_PAGING_LIMIT);
		return cardDAO.selectByBoardIdForPaging(param);
	}

	@Transactional
	public DetailCardInfo findDetailInfo(long cardId) {
		Card card = cardDAO.selectById(cardId);
		return new DetailCardInfo(card.getContent(), card.getVersionNum(), card.getCreatedDate());
	}

	private int updatePrevCardId(long cardId, long prevCardId) {
		Map<String, Object> param = new HashMap<>();
		param.put("cardId", cardId);
		param.put("prevCardId", prevCardId);
		return cardDAO.updatePrevCardId(param);
	}

	private int updateBoardId(long cardId, long boardId) {
		Map<String, Object> param = new HashMap<>();
		param.put("cardId", cardId);
		param.put("boardId", boardId);
		return cardDAO.updateBoardId(param);
	}

	public int updatePosition(CardPosition cardPosition) {
		int numOfResults = 0;
		int code = cardPosition.getCode();
		long loggedEventId = saveMovedCardEvent(cardPosition);
		if (code == MoveCode.MOVE_TO_NEXT.getCode()) {
			numOfResults += moveNext(cardPosition);
		} else if (code == MoveCode.MOVE_TO_MIDDLE.getCode()) {
			numOfResults += moveMiddle(cardPosition, loggedEventId);
		} else if (code == MoveCode.MOVE_TO_LAST.getCode()) {
			numOfResults += moveLast(cardPosition, loggedEventId);
		} else if (code == MoveCode.MOVE_TO_EMPTY_BOARD.getCode()) {
			numOfResults += moveEmptyBoard(cardPosition, loggedEventId);
		}

		return numOfResults;
	}

	private Long saveMovedCardEvent(CardPosition cardPosition) {
		long movedOriginCardId = cardPosition.getOriginCardId();
		long loggedEventId = 0L;
		try {
			loggedEventId = saveCardEvent(ActionNameCase.MOVE.getValue(), movedOriginCardId, "index",
					cardDAO.selectById(movedOriginCardId).getTitle(),
					String.valueOf(cardPosition.getOldIndex()),
					String.valueOf(cardPosition.getNewIndex())).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace(); // 추후에 에러 핸들링 필요
		}
		return loggedEventId;
	}

	private long checkMovedOriginCardId(CardPosition cardPosition) {
		if (cardPosition.getNewIndex() > cardPosition.getOldIndex())
			return cardPosition.getDownPosCardId();
		else
			return cardPosition.getUpPosCardId();
	}

	@Transactional
	public int moveNext(CardPosition cardPosition) {
		int numOfResults = 0;

		Card upPosCard = cardDAO.selectById(cardPosition.getUpPosCardId());
		Card downPosCard = cardDAO.selectById(cardPosition.getDownPosCardId());

		numOfResults += linkDownPosCard(upPosCard.getCardId(), downPosCard.getCardId(), upPosCard.getBoardId());

		numOfResults += updatePrevCardId(upPosCard.getCardId(), downPosCard.getPrevCardId());

		numOfResults += updatePrevCardId(downPosCard.getCardId(), upPosCard.getCardId());

		return numOfResults;
	}

	@Transactional
	public int moveMiddle(CardPosition cardPosition, long loggedEventId) {
		int numOfResults = 0;
		Card originCard = cardDAO.selectById(cardPosition.getOriginCardId());
		Card downPosNewCard = cardDAO.selectById(cardPosition.getDownPosCardId());

		numOfResults += linkDownPosCard(originCard.getCardId(), originCard.getPrevCardId(), originCard.getBoardId());

		numOfResults += checkBoardID(originCard.getCardId(), originCard.getBoardId(), downPosNewCard.getBoardId(),
				loggedEventId);

		numOfResults += checkPosOfTop(originCard.getCardId(), downPosNewCard.getPrevCardId());

		numOfResults += updatePrevCardId(downPosNewCard.getCardId(), originCard.getCardId());

		return numOfResults;
	}

	@Transactional
	public int moveLast(CardPosition cardPosition, long loggedEventId) {
		int numOfResults = 0;
		Card originCard = cardDAO.selectById(cardPosition.getOriginCardId());
		Card upPosCard = cardDAO.selectById(cardPosition.getUpPosCardId());

		numOfResults += linkDownPosCard(originCard.getCardId(), originCard.getPrevCardId(), originCard.getBoardId());

		numOfResults += checkBoardID(originCard.getCardId(), originCard.getBoardId(), upPosCard.getBoardId(),
				loggedEventId);

		numOfResults += updatePrevCardId(originCard.getCardId(), upPosCard.getCardId());

		return numOfResults;
	}

	@Transactional
	public int moveEmptyBoard(CardPosition cardPosition, long loggedEventId) {
		int numOfResults = 0;
		Card originCard = cardDAO.selectById(cardPosition.getOriginCardId());
		long newBoardId = cardPosition.getNewBoardId();

		saveMoveBoardToBoardEvent(originCard.getBoardId(), newBoardId, loggedEventId);
		numOfResults += linkDownPosCard(originCard.getCardId(), originCard.getPrevCardId(), originCard.getBoardId());

		numOfResults += updateBoardId(originCard.getCardId(), newBoardId);

		numOfResults += updatePrevCardId(originCard.getCardId(), MoveCode.POS_OF_TOP.getCode());

		return numOfResults;
	}

	private int checkPosOfTop(long originCardId, long downPosNewCardPrevId) {
		int numOfResults = 0;
		Card upPosCard = cardDAO.selectById(downPosNewCardPrevId);

		if (upPosCard != null) {
			numOfResults += updatePrevCardId(originCardId, downPosNewCardPrevId);
		} else {
			numOfResults += updatePrevCardId(originCardId, MoveCode.POS_OF_TOP.getCode());
		}

		return numOfResults;
	}

	private int checkBoardID(long originCardId, long oldBoardID, long newBoardID, long loggedEventId) {
		int numOfResults = 0;
		if (oldBoardID != newBoardID) {
			numOfResults += updateBoardId(originCardId, newBoardID);
			saveMoveBoardToBoardEvent(oldBoardID, newBoardID, loggedEventId);
		}
		return numOfResults;
	}

	private void saveMoveBoardToBoardEvent(long oldBoardID, long newBoardID, long loggedEventId) {
		moveInfoService.saveMoveInfo(
				new MovedInfo(loggedEventId, boardService.findByBoardId(oldBoardID).getStatus(),
						boardService.findByBoardId(newBoardID).getStatus()));
	}

	public void dropTable() {
		cardDAO.dropTable();
	}

	public void removePostRecord() {
		cardDAO.deleteCardRecord();
	}

	public long removeById(long cardId) {
		Card originCard = cardDAO.selectById(cardId);
		long nowBoardID = originCard.getBoardId();
		String nowEntityTitle = originCard.getTitle();
		linkDownPosCard(originCard.getCardId(), originCard.getPrevCardId(), originCard.getBoardId());
		cardDAO.deleteById(cardId);
		versionDAO.deleteByCardId(cardId);
		saveCardEvent(ActionNameCase.REMOVE.getValue(), cardId, "cardId", nowEntityTitle, String.valueOf(cardId), null);
		return nowBoardID;
	}

	private int linkDownPosCard(long cardId, long prevCardId, long boardId) {
		int numOfResults = 0;
		Card downPosCard = selectByPrevCardId(cardId, boardId);

		if (downPosCard != null) {
			numOfResults += updatePrevCardId(downPosCard.getCardId(), prevCardId);
		}

		return numOfResults;
	}

	private Card selectByPrevCardId(long prevCardId, long boardId) {
		Map<String, Object> param = new HashMap<>();
		param.put("prevCardId", prevCardId);
		param.put("boardId", boardId);
		return cardDAO.selectByPrevCardId(param);
	}

	@Async("asyncThreadPool")
	private Future<Long> saveCardEvent(
			String actionName,
			long modifiedEntityId,
			String modifiedColumn,
			String nowEntityTitle,
			String beforeData,
			String afterData) {
		return new AsyncResult<>(historyService.saveLoggedEvent(
				LoggedEvent.builder()
						.actionName(actionName)
						.modifiedEntity("card")
						.modifiedEntityId(modifiedEntityId)
						.modifiedColumn(modifiedColumn)
						.nowEntityTitle(nowEntityTitle)
						.beforeData(beforeData)
						.afterData(afterData)
						.modifiedTime(LocalDateTime.now())
						.build())
		);
	}

	private void checkDataLoss(long boardId, long prevCardId) {
		Map<String, Object> param = new HashMap<>();
		param.put("boardId", boardId);
		param.put("prevCardId", prevCardId);

		//check data loss
		Card nextCard = cardDAO.selectByPrevCardId(param);
		if (nextCard == null) {
			if (!checkCount(boardId)) {
				//loss start point
				if (prevCardId == 0 && cardDAO.selectFirstByBoardId(boardId) == null) {
					settingStartPoint(boardId);
				}

				//check data loss point
				connectLossPoint(boardId);
			}
		}
	}

	private boolean checkCount(long boardId) {
		return cardDAO.countByBoardId(boardId) == cardDAO.countByRecursive(boardId);
	}

	private void connectLossPoint(long boardId) {
		List<Card> startLossPoint = cardDAO.selectStartLossPoint(boardId);
		List<Card> endLossPoint = cardDAO.selectEndLossPoint(boardId);
		int endLossPointSize = endLossPoint.size();
		Map<String, Object> param = new HashMap<>();

		for (int i = 0; i < endLossPointSize; i++) {
			System.out.println("중간 데이터 복구" + boardId);

			param.put("cardId", endLossPoint.get(i).getCardId());
			param.put("prevCardId", startLossPoint.get(i).getCardId());
			cardDAO.updatePrevCardId(param);
		}
	}

	private void settingStartPoint(long boardId) {
		List<Card> endLossPoint = cardDAO.selectEndLossPoint(boardId);
		Map<String, Object> param = new HashMap<>();
		param.put("cardId", endLossPoint.get(0).getCardId());
		param.put("prevCardId", 0L);
		cardDAO.updatePrevCardId(param);
	}
}
