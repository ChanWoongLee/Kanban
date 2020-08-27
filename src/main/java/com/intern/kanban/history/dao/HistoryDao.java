package com.intern.kanban.history.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.intern.kanban.history.model.LoggedEvent;
import com.intern.kanban.history.model.LoggedEventForAdmin;
import com.intern.kanban.history.model.LoggedEventForClient;

@Repository
public class HistoryDao {
	private static final String NAME_SPACE = "com.intern.kanban.history.dao.HistoryDao.";

	@Autowired
	SqlSessionTemplate template;

	public long insert(LoggedEvent loggedEvent) {
		template.insert(NAME_SPACE + "insert", loggedEvent);
		return loggedEvent.getEventId();
	}

	public List<LoggedEventForAdmin> selectForAdmin(Map<String, Object> param) {
		return template.selectList(NAME_SPACE + "selectForAdmin", param);
	}

	public List<LoggedEventForClient> selectAllForClient(Map<String, Object> param) {
		return template.selectList(NAME_SPACE + "selectAllForClient", param);
	}
}
