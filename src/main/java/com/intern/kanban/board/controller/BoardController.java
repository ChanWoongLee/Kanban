package com.intern.kanban.board.controller;

import java.net.SocketException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intern.kanban.board.model.Board;
import com.intern.kanban.board.model.dto.BoardPosition;
import com.intern.kanban.board.model.dto.BoardStatus;
import com.intern.kanban.board.model.dto.MoveBoardMessage;
import com.intern.kanban.board.model.dto.RemoveBoardMessage;
import com.intern.kanban.board.model.dto.SaveBoardMessage;
import com.intern.kanban.board.service.BoardService;
import com.intern.kanban.history.model.ActionNameCase;
import com.intern.kanban.socket.service.SocketService;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private BoardService boardService;

	@Autowired
	private SocketService socketService;

	@PostMapping
	public long saveBoard(@RequestBody BoardStatus boardStatus) throws Exception {
		if (!socketService.checkSocketIdentifier(boardStatus.getSocketIdentifier())) {
			throw new SocketException();
		}
		long boardId = boardService.saveBoard(boardStatus);
		messagingTemplate.convertAndSend("/sub/board",
			new SaveBoardMessage(ActionNameCase.ADD.getValue(), socketService.getSocketIdentifier(), boardId,
				boardStatus.getStatus()));
		return boardId;
	}

	@GetMapping
	public List<Board> findForPaging(@RequestParam("offset") int offset) {
		return boardService.findForPaging(offset);
	}

	@PatchMapping("/{boardId}")
	public int updateStatus(@PathVariable long boardId, @RequestBody BoardStatus boardStatus) {
		return boardService.updateStatus(boardId, boardStatus);
	}

	@PatchMapping("/order")
	public int updatePosition(@RequestBody BoardPosition boardPosition) throws Exception {
		if (!socketService.checkSocketIdentifier(boardPosition.getSocketIdentifier())) {
			throw new SocketException();
		}
		messagingTemplate.convertAndSend("/sub/board",
			new MoveBoardMessage(ActionNameCase.MOVE.getValue(), socketService.getSocketIdentifier(),
				boardPosition.getOldIndex(),
				boardPosition.getNewIndex()));
		return boardService.updatePosition(boardPosition);
	}

	@DeleteMapping
	public int removeById(@RequestParam long boardId, @RequestParam String socketIdentifier) throws Exception {
		if (!socketService.checkSocketIdentifier(socketIdentifier)) {
			throw new SocketException();
		}
		messagingTemplate.convertAndSend("/sub/board",
			new RemoveBoardMessage(ActionNameCase.REMOVE.getValue(), socketService.getSocketIdentifier(), boardId));
		return boardService.removeById(boardId);
	}
}
