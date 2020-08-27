package com.intern.kanban.card.model.dto;

import java.util.List;

import com.intern.kanban.version.model.dto.BasicVersionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatedCardInfo {
	private String title;

	private String content;

	private List<BasicVersionInfo> basicVersionInfos;
}
