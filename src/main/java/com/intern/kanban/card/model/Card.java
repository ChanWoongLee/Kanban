package com.intern.kanban.card.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Card {

	private long cardId;

	private final String author;

	private final String title;

	private String content;

	private final long boardId;

	private final long prevCardId;

	private final LocalDateTime createdDate;

	private final long versionNum;
}
