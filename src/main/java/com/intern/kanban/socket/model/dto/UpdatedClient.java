package com.intern.kanban.socket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedClient {

	public Long cardId;

	public Boolean isUpdating;
}
