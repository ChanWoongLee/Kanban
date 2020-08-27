package com.intern.kanban.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveBoardMessage {
	private String actionName;

	private String socketIdentifier;

	private long boardId;

	private String status;

}
