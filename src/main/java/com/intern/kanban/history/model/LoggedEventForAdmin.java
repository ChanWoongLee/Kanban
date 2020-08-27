package com.intern.kanban.history.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoggedEventForAdmin {
	private final long eventId;

	private final String actionName;

	private final String modifiedEntity;

	private final long modifiedEntityId;

	private final String nowEntityTitle;

	private final String modifiedColumn;

	private String beforeData;

	private String afterData;

	private final LocalDateTime modifiedTime;

	private Long movedInfoId;

	private Long loggedEventId;

	private String beforeBoardTitle;

	private String afterBoardTitle;

}
