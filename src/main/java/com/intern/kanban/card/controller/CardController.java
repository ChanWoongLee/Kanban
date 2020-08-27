package com.intern.kanban.card.controller;

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

import com.intern.kanban.card.model.Card;
import com.intern.kanban.card.model.dto.AddCard;
import com.intern.kanban.card.model.dto.AddCardMessage;
import com.intern.kanban.card.model.dto.BasicCardInfo;
import com.intern.kanban.card.model.dto.CardPosition;
import com.intern.kanban.card.model.dto.DetailCardInfo;
import com.intern.kanban.card.model.dto.EditCard;
import com.intern.kanban.card.model.dto.MoveCardMessage;
import com.intern.kanban.card.model.dto.RemoveCardMessage;
import com.intern.kanban.card.model.dto.UpdatedCardInfo;
import com.intern.kanban.card.service.CardMergeService;
import com.intern.kanban.card.model.dto.UpdatedCardInfo;
import com.intern.kanban.card.service.CardService;
import com.intern.kanban.history.model.ActionNameCase;
import com.intern.kanban.socket.service.SocketService;

@RestController
@RequestMapping("/api/card")
public class CardController {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private CardService cardService;

	@Autowired
	private CardMergeService cardMergeService;

	@Autowired
	private SocketService socketService;

	@PostMapping
	public Card saveCard(@RequestBody AddCard addCard) throws SocketException {
		if (!socketService.checkSocketIdentifier(addCard.getSocketIdentifier())) {
			throw new SocketException();
		}
		Card newCard = cardService.saveCard(addCard);
		messagingTemplate.convertAndSend("/sub/card",
				new AddCardMessage(ActionNameCase.ADD.getValue(), socketService.getSocketIdentifier(), newCard));
		return newCard;
	}

	@GetMapping("/{cardId}")
	public Card findById(@PathVariable long cardId) {
		return cardService.findById(cardId);
	}

	@GetMapping
	public List<BasicCardInfo> findByBoardIdForPaging(@RequestParam long boardId, @RequestParam int offset) {
		return cardService.findByBoardIdForPaging(boardId, offset);
	}

	@GetMapping("/detail")
	public DetailCardInfo findDetailInfo(@RequestParam long cardId) {
		return cardService.findDetailInfo(cardId);
	}

	@PatchMapping
	public UpdatedCardInfo updateCardInfo(@RequestBody EditCard editCard) throws Exception {
		return cardMergeService.updateCardInfo(editCard);
	}

	@PatchMapping("/order")
	public int updatePosition(@RequestBody CardPosition cardPosition) throws SocketException {
		if (!socketService.checkSocketIdentifier(cardPosition.getSocketIdentifier())) {
			throw new SocketException();
		}
		messagingTemplate.convertAndSend("/sub/card",
				new MoveCardMessage(ActionNameCase.MOVE.getValue(),
						socketService.getSocketIdentifier(),
						cardService.findById(cardPosition.getOriginCardId()).getBoardId(),
						cardPosition.getNewBoardId(),
						cardPosition.getOriginCardId(),
						cardPosition.getNewIndex()));
		return cardService.updatePosition(cardPosition);
	}

	@DeleteMapping
	public long removeById(@RequestParam long cardId, @RequestParam String socketIdentifier) throws SocketException {
		if (!socketService.checkSocketIdentifier(socketIdentifier)) {
			throw new SocketException();
		}
		long nowBoardId = cardService.removeById(cardId);
		messagingTemplate.convertAndSend("/sub/card",
				new RemoveCardMessage(ActionNameCase.REMOVE.getValue(),
						socketService.getSocketIdentifier(),
						cardId,
						nowBoardId));
		return cardId;
	}
}
