package com.intern.kanban.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveCardMessage {
	private String actionName;

	private String socketIdentifier;

	private long cardId;

	private long nowBoardId;
}
