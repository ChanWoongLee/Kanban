package com.intern.kanban.card.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailCardInfo {

	private String content;

	private long versionNum;

	private LocalDateTime createdDate;
}
