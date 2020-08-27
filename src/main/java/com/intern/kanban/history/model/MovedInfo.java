package com.intern.kanban.history.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class MovedInfo {

	private long movedInfoId;

	private final long loggedEventId;

	private final String beforeBoardTitle;

	private final String afterBoardTitle;

}
