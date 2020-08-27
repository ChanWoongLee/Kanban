package com.intern.kanban.history.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intern.kanban.history.dao.HistoryDao;
import com.intern.kanban.history.model.LoggedEvent;
import com.intern.kanban.history.model.LoggedEventForAdmin;
import com.intern.kanban.history.model.LoggedEventForClient;

@Service
public class HistoryService {
	private static final int HISTORY_PAGING_LIMIT = 10;

	@Autowired
	HistoryDao historyDao;

	public long saveLoggedEvent(LoggedEvent loggedEvent) {
		return historyDao.insert(loggedEvent);
	}

	public List<LoggedEventForAdmin> findForAdmin(int offset, String modifiedEntity, String actionName) {
		Map<String, Object> param = new HashMap<>();
		param.put("offset", offset * HISTORY_PAGING_LIMIT);
		param.put("limit", HISTORY_PAGING_LIMIT);
		param.put("modifiedEntity", modifiedEntity.equals("") ? "%" : modifiedEntity);
		param.put("actionName", actionName.equals("") ? "%" : actionName);

		return historyDao.selectForAdmin(param);
	}

	public List<LoggedEventForClient> findAllForClient(int offset) {
		Map<String, Object> param = new HashMap<>();
		param.put("offset", offset * HISTORY_PAGING_LIMIT);
		param.put("limit", HISTORY_PAGING_LIMIT);
		return historyDao.selectAllForClient(param);

	}
}
