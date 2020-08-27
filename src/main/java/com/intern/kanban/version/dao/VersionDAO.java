package com.intern.kanban.version.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.intern.kanban.version.model.Version;
import com.intern.kanban.version.model.dto.BasicVersionInfo;
import com.intern.kanban.version.model.dto.DetailVersionInfo;

@Repository
public class VersionDAO {
	private static final String NAME_SPACE = "com.intern.kanban.version.dao.VersionDAO.";

	@Autowired
	SqlSessionTemplate template;

	public void insert(Version version) {
		template.insert(NAME_SPACE + "insert", version);
	}

	public List<BasicVersionInfo> selectVersionInfos(long cardId) {
		return template.selectList(NAME_SPACE + "selectByCardId", cardId);
	}

	public String selectContent(Map<String, Object> param) {
		return template.selectOne(NAME_SPACE + "selectContent", param);
	}

	public DetailVersionInfo selectDetailVersionInfo(Map<String, Object> param) {
		return template.selectOne(NAME_SPACE + "selectDetailVersionInfo", param);
	}

	public int deleteByCardId(long cardId) {
		return template.delete(NAME_SPACE + "deleteByCardId", cardId);
	}
}
