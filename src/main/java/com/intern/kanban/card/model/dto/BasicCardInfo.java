package com.intern.kanban.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicCardInfo {

	private long cardId;

	private String author;

	private String title;
}
