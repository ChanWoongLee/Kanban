package com.intern.kanban.board.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intern.kanban.board.dao.BoardDAO;
import com.intern.kanban.board.model.Board;
import com.intern.kanban.board.model.dto.BoardPosition;
import com.intern.kanban.board.model.dto.BoardStatus;
import com.intern.kanban.board.model.dto.MoveCode;
import com.intern.kanban.history.model.ActionNameCase;
import com.intern.kanban.history.model.LoggedEvent;
import com.intern.kanban.history.service.HistoryService;

@Service
public class BoardService {

	private static final int BOARD_PAGING_LIMIT = 5;

	@Autowired
	BoardDAO boardDAO;

	@Autowired
	HistoryService historyService;

	public void createTable() {
		boardDAO.createTable();
	}

	@Transactional
	public Long saveBoard(BoardStatus boardStatus) {
		Board newBoard = new Board(boardStatus.getStatus(), MoveCode.POS_OF_TOP.getCode());
		Board topPosBoard = boardDAO.selectByPrevBoardId(MoveCode.POS_OF_TOP.getCode());
		long newBoardId = boardDAO.insert(newBoard);

		if (!isMainBoardEmpty(topPosBoard)) {
			updatePrevBoardId(topPosBoard.getBoardId(), newBoardId);
		}
		saveBoardEventAsync(
				ActionNameCase.ADD.getValue(),
				newBoardId,
				"boardId",
				boardStatus.getStatus(),
				null,
				String.valueOf(newBoardId));
		return newBoardId;
	}

	private boolean isMainBoardEmpty(Board board) {
		return board == null;
	}

	@Transactional
	public List<Board> findForPaging(int offset) {

		checkDataLoss(offset);

		Map<String, Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", BOARD_PAGING_LIMIT);
		return boardDAO.selectForPaging(param);
	}

	public Board findByBoardId(long boardId) {
		return boardDAO.selectById(boardId);
	}

	public int updateStatus(long boardId, BoardStatus boardStatus) {
		Map<String, Object> param = new HashMap<>();
		param.put("boardId", boardId);
		param.put("status", boardStatus.getStatus());

		String prevBoardStatus = boardDAO.selectById(boardId).getStatus();
		saveBoardEventAsync(
				ActionNameCase.UPDATE.getValue(),
				boardId,
				"status",
				boardStatus.getStatus(),
				prevBoardStatus,
				boardStatus.getStatus());
		return boardDAO.updateStatus(param);
	}

	private int updatePrevBoardId(long boardId, long prevBoardId) {
		Map<String, Object> param = new HashMap<>();
		param.put("boardId", boardId);
		param.put("prevBoardId", prevBoardId);
		return boardDAO.updatePrevBoardId(param);
	}

	public int updatePosition(BoardPosition boardPosition) {
		int numOfResults = 0;
		int code = boardPosition.getCode();
		saveMovedBoardEvent(boardPosition, code);
		if (code == MoveCode.MOVE_TO_NEXT.getCode()) {
			numOfResults += moveNext(boardPosition.getUpPosBoardId(), boardPosition.getDownPosBoardId());
		} else if (code == MoveCode.MOVE_TO_MIDDLE.getCode()) {
			numOfResults += moveMiddle(boardPosition.getOriginBoardId(), boardPosition.getDownPosBoardId());
		} else if (code == MoveCode.MOVE_TO_LAST.getCode()) {
			numOfResults += moveLast(boardPosition.getOriginBoardId(), boardPosition.getUpPosBoardId());
		}

		return numOfResults;
	}

	private void saveMovedBoardEvent(BoardPosition boardPosition, int code) {
		long movedOriginBoardId = boardPosition.getOriginBoardId();
		if (code == MoveCode.MOVE_TO_NEXT.getCode()) {
			movedOriginBoardId = checkMovedOriginBoardId(boardPosition);
		}
		saveBoardEventAsync(
				ActionNameCase.MOVE.getValue(),
				movedOriginBoardId,
				"index",
				boardDAO.selectById(movedOriginBoardId).getStatus(),
				String.valueOf(boardPosition.getOldIndex()),
				String.valueOf(boardPosition.getNewIndex()));
	}

	private long checkMovedOriginBoardId(BoardPosition boardPosition) {
		if (boardPosition.getNewIndex() > boardPosition.getOldIndex())
			return boardPosition.getDownPosBoardId();
		else
			return boardPosition.getUpPosBoardId();
	}

	@Transactional
	public int moveNext(long upPostBoardId, long downPosBoardId) {
		int numOfResults = 0;
		Board upPosBoard = boardDAO.selectById(upPostBoardId);
		Board downPosBoard = boardDAO.selectById(downPosBoardId);

		numOfResults += linkDownPosBoard(upPosBoard.getBoardId(), downPosBoard.getBoardId());

		numOfResults += updatePrevBoardId(upPosBoard.getBoardId(), downPosBoard.getPrevBoardId());

		numOfResults += updatePrevBoardId(downPosBoard.getBoardId(), upPosBoard.getBoardId());

		return numOfResults;
	}

	@Transactional
	public int moveMiddle(long originBoardId, long downPosBoardId) {
		int numOfResults = 0;
		Board originBoard = boardDAO.selectById(originBoardId);
		Board downPosNewBoard = boardDAO.selectById(downPosBoardId);

		numOfResults += linkDownPosBoard(originBoard.getBoardId(), originBoard.getPrevBoardId());

		numOfResults += checkPosOfTop(originBoard.getBoardId(), downPosNewBoard.getPrevBoardId());

		numOfResults += updatePrevBoardId(downPosNewBoard.getBoardId(), originBoard.getBoardId());

		return numOfResults;
	}

	private int checkPosOfTop(long originBoardId, long downPosNewBoardPrevId) {
		int numOfResults = 0;
		Board upPosBoard = boardDAO.selectById(downPosNewBoardPrevId);

		if (upPosBoard != null) {
			numOfResults += updatePrevBoardId(originBoardId, downPosNewBoardPrevId);
		} else {
			numOfResults += updatePrevBoardId(originBoardId, MoveCode.POS_OF_TOP.getCode());
		}

		return numOfResults;
	}

	@Transactional
	public int moveLast(long originBoardId, long upPosBoardId) {
		int numOfResults = 0;
		Board originBoard = boardDAO.selectById(originBoardId);
		Board upPosBoard = boardDAO.selectById(upPosBoardId);

		numOfResults += linkDownPosBoard(originBoard.getBoardId(), originBoard.getPrevBoardId());

		numOfResults += updatePrevBoardId(originBoard.getBoardId(), upPosBoard.getBoardId());

		return numOfResults;
	}

	public void dropTable() {
		boardDAO.dropTable();
	}

	public void removeBoardRecord() {
		boardDAO.deleteBoardRecord();
	}

	@Transactional
	public int removeById(long boardId) {
		int numOfResults = 0;
		Board originBoard = boardDAO.selectById(boardId);
		String nowEntityTitle = originBoard.getStatus();
		numOfResults += linkDownPosBoard(originBoard.getBoardId(), originBoard.getPrevBoardId());
		numOfResults += boardDAO.deleteById(boardId);
		numOfResults += boardDAO.deleteCardByBoardId(boardId);

		saveBoardEventAsync(
				ActionNameCase.REMOVE.getValue(),
				boardId,
				"boardId",
				nowEntityTitle,
				String.valueOf(boardId),
				null);
		return numOfResults;
	}

	private int linkDownPosBoard(long boardId, long prevBoardId) {
		int numOfResults = 0;
		Board downPosBoard = boardDAO.selectByPrevBoardId(boardId);

		if (downPosBoard != null) {
			numOfResults += updatePrevBoardId(downPosBoard.getBoardId(), prevBoardId);
		}

		return numOfResults;
	}

	@Async("asyncThreadPool")
	private void saveBoardEventAsync(
			String actionName,
			long modifiedEntityId,
			String modifiedColumn,
			String nowEntityTitle,
			String beforeData,
			String afterData) {
		historyService.saveLoggedEvent(
				LoggedEvent.builder()
						.actionName(actionName)
						.modifiedEntity("board")
						.modifiedEntityId(modifiedEntityId)
						.modifiedColumn(modifiedColumn)
						.nowEntityTitle(nowEntityTitle)
						.beforeData(beforeData)
						.afterData(afterData)
						.modifiedTime(LocalDateTime.now())
						.build()
		);
	}

	private void checkDataLoss(long prevBoardId) {
		//check data loss
		Board nextBoard = boardDAO.selectByPrevBoardId(prevBoardId);
		if (nextBoard == null) {
			if (!checkCount()) {
				//loss start point
				if (prevBoardId == 0 && boardDAO.selectFirstBoard() == null) {
					settingStartPoint();
				}
				//check data loss point
				connectLossPoint();
			}
		}
	}

	private boolean checkCount() {
		return boardDAO.count() == boardDAO.countByRecursive();
	}

	private void connectLossPoint() {
		List<Board> startLossPoint = boardDAO.selectStartLossPoint();
		List<Board> endLossPoint = boardDAO.selectEndLossPoint();
		int endLossPointSize = endLossPoint.size();
		Map<String, Object> param = new HashMap<>();

		for (int i = 0; i < endLossPointSize; i++) {
			param.put("boardId", endLossPoint.get(i).getBoardId());
			param.put("prevBoardId", startLossPoint.get(i).getBoardId());
			boardDAO.updatePrevBoardId(param);
		}
	}

	private void settingStartPoint() {
		List<Board> endLossPoint = boardDAO.selectEndLossPoint();
		Map<String, Object> param = new HashMap<>();
		param.put("boardId", endLossPoint.get(0).getBoardId());
		param.put("prevBoardId", 0L);
		boardDAO.updatePrevBoardId(param);
	}

}
