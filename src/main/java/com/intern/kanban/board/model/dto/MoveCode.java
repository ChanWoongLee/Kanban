package com.intern.kanban.board.model.dto;

public enum MoveCode {
	POS_OF_TOP(0),
	MOVE_TO_NEXT(1),
	MOVE_TO_MIDDLE(2),
	MOVE_TO_LAST(3),
	MOVE_TO_EMPTY_BOARD(4);

	private final int code;

	MoveCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
