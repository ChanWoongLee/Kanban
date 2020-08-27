package com.intern.kanban.board.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardPosition {

	private int code;

	private long originBoardId;

	private long upPosBoardId;

	private long downPosBoardId;

	private long oldIndex;

	private long newIndex;

	private String socketIdentifier;
}
