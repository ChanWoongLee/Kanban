package com.intern.kanban.card.model.dto;

import com.intern.kanban.card.model.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddCardMessage {
	private String actionName;

	private String socketIdentifier;

	private Card newCard;
}
