package com.intern.kanban.card.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardPosition {

	private int code;

	private long originCardId;

	private long upPosCardId;

	private long downPosCardId;

	private long newBoardId;

	private long oldIndex;

	private long newIndex;

	private String socketIdentifier;
}
