package com.intern.kanban.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Board {

	private long boardId;

	private final String status;

	private final long prevBoardId;
}
