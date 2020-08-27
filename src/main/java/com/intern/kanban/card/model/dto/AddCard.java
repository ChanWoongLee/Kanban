package com.intern.kanban.card.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddCard {

	public String author;

	public String title;

	public long boardId;

	private String socketIdentifier;
}
