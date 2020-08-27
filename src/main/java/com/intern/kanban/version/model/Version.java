package com.intern.kanban.version.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Version {

	private final long cardId;

	private final long versionNum;

	private final String title;

	private final String content;

	private final String modifier;

	private final LocalDateTime modifiedDate;
}
