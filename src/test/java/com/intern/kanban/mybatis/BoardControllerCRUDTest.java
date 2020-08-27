package com.intern.kanban.mybatis;
/*
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.intern.kanban.dao.BoardDAO;
import com.intern.kanban.model.dto.Board;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardControllerCRUDTest {
	@Autowired
	private BoardDAO boardDAO;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final int ID = 1;
	private static final String STATUS = "1";
	private static final int DELETE_ID = 1;

	@Before
	public void CreateTable() {
		boardDAO.createTable();
	}

	@After
	public void DropTable() {
		boardDAO.dropTable();
	}

	@Test
	public void testPost() {
		Board board = new Board(ID, STATUS);
		String url = "http://localhost:" + port + "/board";
		ResponseEntity<Integer> responseEntity = restTemplate.postForEntity(url, board, Integer.class);

		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(responseEntity.getBody()).isGreaterThan(0);

		board = boardDAO.selectById(ID);
		Assertions.assertThat(board.getId()).isEqualTo(ID);
		Assertions.assertThat(board.getStatus()).isEqualTo(STATUS);
	}

	@Test
	public void testGet() {
		Board board = new Board(ID, STATUS);
		boardDAO.insert(board);

		String url = "http://localhost:" + port + "/board";
		ResponseEntity<Board[]> responseEntity = restTemplate.getForEntity(url, Board[].class);
		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		Board[] response = responseEntity.getBody();
		Assertions.assertThat(response[0].getId()).isEqualTo(ID);
		Assertions.assertThat(response[0].getStatus()).isEqualTo(STATUS);
	}

	@Test
	public void testDelete() {
		Board board = new Board(ID, STATUS);
		boardDAO.insert(board);

		String url = "http://localhost:" + port + "/board/" + DELETE_ID;
		restTemplate.delete(url);

		Assertions.assertThat(boardDAO.selectById(DELETE_ID)).isNull();
	}

	@Test
	public void testUpdate() {
		Board board = new Board(ID, STATUS);
		boardDAO.insert(board);

		String url = "http://localhost:" + port + "/board";
		Board changPost = new Board(ID, "STATUS_TEST");
		restTemplate.put(url, changPost);

		board = boardDAO.selectById(ID);

		Assertions.assertThat(board.getId()).isEqualTo(ID);
		Assertions.assertThat(board.getStatus()).isEqualTo("STATUS_TEST");
	}
}*/
