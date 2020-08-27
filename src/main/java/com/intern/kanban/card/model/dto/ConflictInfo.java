package com.intern.kanban.card.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConflictInfo {
	private long recentVersionNum;

	private int insertCnt;

	private String baseContent;

	private List<LineInfo> recentContent;

	private List<LineInfo> myContent;
}
