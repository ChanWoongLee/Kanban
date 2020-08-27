package com.intern.kanban.card.error.exception;

import java.util.List;

import com.intern.kanban.card.model.CardOperation;
import com.intern.kanban.card.model.dto.ConflictInfo;
import com.intern.kanban.card.model.dto.LineInfo;

public class ConflictException extends Exception {
	private ConflictInfo error;

	public ConflictException(long recentVersionNum, String baseVersionContent, List<LineInfo> recentVersionContent, List<LineInfo> myContent) {
		int insertCnt = countInsert(recentVersionContent) + countInsert(myContent);
		error = new ConflictInfo(recentVersionNum, insertCnt, baseVersionContent, recentVersionContent, myContent);
	}

	public ConflictInfo getConflictData() {
		return error;
	}

	private int countInsert(List<LineInfo> LineInfos) {
		int insertCnt = 0;
		for (LineInfo lineInfo : LineInfos) {
			if (lineInfo.getOperation() == CardOperation.CHANGE) {
				insertCnt++;
			}
		}
		return insertCnt;
	}
}
