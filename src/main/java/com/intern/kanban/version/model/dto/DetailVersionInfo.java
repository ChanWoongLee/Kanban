package com.intern.kanban.version.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailVersionInfo {

	private String title;

	private String content;

	private String modifier;

	private LocalDateTime modifiedDate;
}
