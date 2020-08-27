package com.intern.kanban.version.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RollbackVersion {

	private long cardId;

	private long rollbackVersionNum;
}
