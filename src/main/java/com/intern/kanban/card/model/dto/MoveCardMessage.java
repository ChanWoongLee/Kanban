package com.intern.kanban.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoveCardMessage {
	private String actionName;

	private String socketIdentifier;

	private long originBoardId;

	private Long newBoardId;

	private long cardId;

	private long newIndex;

}
