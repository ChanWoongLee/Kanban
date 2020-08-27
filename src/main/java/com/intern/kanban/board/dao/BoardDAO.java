package com.intern.kanban.board.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.intern.kanban.board.model.Board;

@Repository
public class BoardDAO {

	private static final String NAME_SPACE = "com.intern.kanban.board.dao.BoardDAO.";

	@Autowired
	SqlSessionTemplate template;

	public void createTable() {
		template.update(NAME_SPACE + "createTable");
	}

	public long insert(Board board) {
		template.insert(NAME_SPACE + "insert", board);
		return board.getBoardId();
	}

	public Board selectById(long boardId) {
		return template.selectOne(NAME_SPACE + "selectById", boardId);
	}

	public Board selectFirstBoard() {
		return template.selectOne(NAME_SPACE + "selectFirstBoard");
	}

	public Board selectByPrevBoardId(long prevBoardId) {
		return template.selectOne(NAME_SPACE + "selectByPrevBoardId", prevBoardId);
	}

	public List<Board> selectForPaging(Map<String, Object> param) {
		return template.selectList(NAME_SPACE + "selectForPaging", param);
	}

	public List<Board> selectStartLossPoint() {
		return template.selectList(NAME_SPACE + "selectStartLossPoint");
	}

	public List<Board> selectEndLossPoint() {
		return template.selectList(NAME_SPACE + "selectEndLossPoint");
	}

	public int updateStatus(Map<String, Object> param) {
		return template.update(NAME_SPACE + "updateStatus", param);
	}

	public int updatePrevBoardId(Map<String, Object> param) {
		return template.update(NAME_SPACE + "updatePrevBoardId", param);
	}

	public void dropTable() {
		template.delete(NAME_SPACE + "dropTable");
	}

	public int deleteById(long boardId) {
		return template.delete(NAME_SPACE + "deleteById", boardId);
	}

	public int deleteCardByBoardId(long boardId) {
		return template.delete(NAME_SPACE + "deleteCardByBoardId", boardId);
	}

	public void deleteBoardRecord() {
		template.delete(NAME_SPACE + "deleteBoardRecord");
	}

	public long count() {
		return template.selectOne(NAME_SPACE + "count");
	}

	public long countByRecursive() {
		return template.selectOne(NAME_SPACE + "countByRecursive");
	}
}
