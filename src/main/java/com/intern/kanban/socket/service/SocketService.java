package com.intern.kanban.socket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.intern.kanban.socket.model.dto.UpdatedClient;
import com.intern.kanban.socket.repo.EhCacheRepository;

@Service
public class SocketService {
	private static final String SOCKET_IDENTIFIER_KEY = "socketIdentifier";
	@Autowired
	private EhCacheRepository repository;

	public String getSocketIdentifier() {
		return repository.getSocketIdentifier(SOCKET_IDENTIFIER_KEY).toString();
	}

	public boolean checkSocketIdentifier(String nowSocketIdentifier) {
		if (StringUtils.pathEquals(nowSocketIdentifier,
			repository.getSocketIdentifier(SOCKET_IDENTIFIER_KEY).toString())) {
			repository.updatedSocketIdentifier(SOCKET_IDENTIFIER_KEY);
			return true;
		} else {
			return false; // 에러 처리
		}
	}

	public int updatedClient(UpdatedClient updatedClient) {
		int nowUpdatingClientNum = repository.getUpdatingCardClientNum(updatedClient.cardId);
		if (updatedClient.isUpdating) {
			repository.updateClientNum(updatedClient.cardId, nowUpdatingClientNum + 1);
		} else {
			repository.updateClientNum(updatedClient.cardId, nowUpdatingClientNum - 1);
		}
		return repository.getUpdatingCardClientNum(updatedClient.cardId);
	}
}
