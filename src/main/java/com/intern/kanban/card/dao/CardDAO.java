package com.intern.kanban.card.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.intern.kanban.card.model.Card;
import com.intern.kanban.card.model.dto.BasicCardInfo;

@Repository
public class CardDAO {

	private static final String NAME_SPACE = "com.intern.kanban.card.dao.CardDAO.";

	@Autowired
	SqlSessionTemplate template;

	public void createTable() {
		template.update(NAME_SPACE + "createTable");
	}

	public long insert(Card card) {
		template.insert(NAME_SPACE + "insert", card);
		return card.getCardId();
	}

	public Card selectById(long cardId) {
		return template.selectOne(NAME_SPACE + "selectById", cardId);
	}

	public Card selectFirstByBoardId(long boardId) {
		return template.selectOne(NAME_SPACE + "selectFirstByBoardId", boardId);
	}

	public Card selectByPrevCardId(Map<String, Object> param) {
		return template.selectOne(NAME_SPACE + "selectByPrevCardId", param);
	}

	public List<BasicCardInfo> selectByBoardIdForPaging(Map<String, Object> param) {
		return template.selectList(NAME_SPACE + "selectByBoardIdForPaging", param);
	}

	public List<Card> selectStartLossPoint(long boardId) {
		return template.selectList(NAME_SPACE + "selectStartLossPoint", boardId);
	}

	public List<Card> selectEndLossPoint(long boardId) {
		return template.selectList(NAME_SPACE + "selectEndLossPoint", boardId);
	}

	public int updateBoardId(Map<String, Object> param) {
		return template.update(NAME_SPACE + "updateBoardId", param);
	}

	public int updatePrevCardId(Map<String, Object> param) {
		return template.update(NAME_SPACE + "updatePrevCardId", param);
	}

	public int updateCardInfo(Map<String, Object> param) {
		return template.update(NAME_SPACE + "updateCardInfo", param);
	}

	public int updateVersion(Map<String, Object> param) {
		return template.update(NAME_SPACE + "updateVersion", param);
	}

	public void dropTable() {
		template.delete(NAME_SPACE + "dropTable");
	}

	public int deleteById(long cardId) {
		return template.delete(NAME_SPACE + "deleteById", cardId);
	}

	public void deleteCardRecord() {
		template.delete(NAME_SPACE + "deletePostRecord");
	}

	public long countByBoardId(long boardId) {
		return template.selectOne(NAME_SPACE + "countByBoardId", boardId);
	}

	public long countByRecursive(long boardId) {
		return template.selectOne(NAME_SPACE + "countByRecursive", boardId);
	}

}
