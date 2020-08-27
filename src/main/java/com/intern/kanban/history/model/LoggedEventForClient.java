package com.intern.kanban.history.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class LoggedEventForClient {
	private final long eventId;

	private final String actionName;

	private final String modifiedEntity;

	private final String nowEntityTitle;

	private String beforeData;

	private String afterData;

	private final LocalDateTime modifiedTime;

	private String beforeBoardTitle;

	private String afterBoardTitle;
}
