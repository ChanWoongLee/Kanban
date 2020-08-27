package com.intern.kanban.history.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.intern.kanban.history.model.LoggedEvent;
import com.intern.kanban.history.model.MovedInfo;

@Repository
public class MovedInfoDao {
	private static final String NAME_SPACE = "com.intern.kanban.history.dao.MovedInfoDao.";

	@Autowired
	SqlSessionTemplate template;

	public long insert(MovedInfo movedInfo) {
		template.insert(NAME_SPACE + "insert", movedInfo);
		return movedInfo.getMovedInfoId();
	}

	public List<LoggedEvent> selectAll() {
		return template.selectList(NAME_SPACE + "selectAll");
	}
}
