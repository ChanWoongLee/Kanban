package com.intern.kanban.version.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasicVersionInfo {

	private long versionNum;

	private String modifier;

	private LocalDateTime modifiedDate;
}
