package com.intern.kanban.socket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.intern.kanban.socket.model.dto.SocketIdentifier;
import com.intern.kanban.socket.model.dto.UpdateState;
import com.intern.kanban.socket.model.dto.UpdatedClient;
import com.intern.kanban.socket.service.SocketService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SocketController {
	@Autowired
	SocketService socketService;

	@Autowired
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/identifier")
	public void SocketIdentifierHandler() {
		messagingTemplate.convertAndSend("/sub/identifier",
			new SocketIdentifier(socketService.getSocketIdentifier()));
	}

	@MessageMapping("/detail/card")
	public void SocketHandler(UpdatedClient updatedClient) {
		UpdateState updateState = new UpdateState(socketService.updatedClient(updatedClient));
		messagingTemplate.convertAndSend("/sub/detail/client/" + updatedClient.getCardId(), updateState);
	}
}
