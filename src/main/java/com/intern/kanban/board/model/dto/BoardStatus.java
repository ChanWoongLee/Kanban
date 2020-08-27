package com.intern.kanban.board.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardStatus {

	private String status;

	private String socketIdentifier;
}
