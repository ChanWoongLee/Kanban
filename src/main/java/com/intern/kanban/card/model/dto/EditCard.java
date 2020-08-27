package com.intern.kanban.card.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EditCard {

	private long cardId;

	private long versionNum;

	private String title;

	private String content;

	private String modifier;
}
