package com.intern.kanban.card.model.dto;

import com.intern.kanban.card.model.CardOperation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineInfo {
	private int lineNum;

	private CardOperation operation;

	private String lineContent;
}
