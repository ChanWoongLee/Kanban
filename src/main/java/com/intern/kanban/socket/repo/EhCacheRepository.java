package com.intern.kanban.socket.repo;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class EhCacheRepository {

	@Cacheable(cacheNames = "updatingCardCache", key = "#cardId")
	public int getUpdatingCardClientNum(long cardId) {
		return 0;
	}

	@CacheEvict(cacheNames = "updatingCardCache", key = "#cardId")
	public void removeKey(long cardId) {
	}

	@CachePut(cacheNames = "updatingCardCache", key = "#cardId")
	public int updateClientNum(long cardId, int updateClientNum) {
		return updateClientNum;
	}

	@CachePut(cacheNames = "socketIdentifier", key = "#socketIdentifier")
	public UUID updatedSocketIdentifier(String socketIdentifier) {
		return UUID.randomUUID();
	}

	@Cacheable(cacheNames = "socketIdentifier", key = "#socketIdentifier")
	public UUID getSocketIdentifier(String socketIdentifier) {
		return UUID.randomUUID();
	}
}
