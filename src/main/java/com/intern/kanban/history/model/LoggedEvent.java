package com.intern.kanban.history.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoggedEvent {

	private long eventId;

	private final String actionName;

	private final String modifiedEntity;

	private final long modifiedEntityId;

	private final String nowEntityTitle;

	private final String modifiedColumn;

	private String beforeData;

	private String afterData;

	private final LocalDateTime modifiedTime;

}
