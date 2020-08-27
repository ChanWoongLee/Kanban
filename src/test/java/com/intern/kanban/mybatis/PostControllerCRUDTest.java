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
import com.intern.kanban.dao.CardDAO;
import com.intern.kanban.model.board.Board;
import com.intern.kanban.model.card.Card;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerCRUDTest {
	@Autowired
	private CardDAO cardDAO;

	@Autowired
	private BoardDAO boardDAO;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String TABLE_NAME = "post";
	private static final int ID = 1;
	private static final int DELETE_ID = 1;
	private static final String AUTHOR = "작성자1";
	private static final String CONTENT = "내용1";
	private static final int STATUS = 1;

	@Before
	public void CreateTable() {
		boardDAO.createTable();
		boardDAO.insert(new Board(1, "1"));
		cardDAO.createTable();

	}

	@After
	public void DropTable() {
		cardDAO.dropTable();
		boardDAO.dropTable();
	}

	@Test
	public void testPost() {
		Card post = new Card(ID, AUTHOR, CONTENT, STATUS);
		String url = "http://localhost:" + port + "/post";
		ResponseEntity<Integer> responseEntity = restTemplate.postForEntity(url, post, Integer.class);

		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(responseEntity.getBody()).isGreaterThan(0);

		post = cardDAO.selectById(ID);
		Assertions.assertThat(post.getId()).isEqualTo(ID);
		Assertions.assertThat(post.getAuthor()).isEqualTo(AUTHOR);
		Assertions.assertThat(post.getContent()).isEqualTo(CONTENT);
		Assertions.assertThat(post.getStatus()).isEqualTo(STATUS);
	}

	@Test
	public void testGet() {
		Card post = new Card(ID, AUTHOR, CONTENT, STATUS);
		cardDAO.insert(post);

		String url = "http://localhost:" + port + "/post";
		ResponseEntity<Card[]> responseEntity = restTemplate.getForEntity(url, Card[].class);
		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		Card[] response = responseEntity.getBody();
		Assertions.assertThat(response[0].getId()).isEqualTo(ID);
		Assertions.assertThat(response[0].getAuthor()).isEqualTo(AUTHOR);
		Assertions.assertThat(response[0].getContent()).isEqualTo(CONTENT);
		Assertions.assertThat(response[0].getStatus()).isEqualTo(STATUS);
	}

	@Test
	public void testGetByStatus() {
		Card post = new Card(ID, AUTHOR, CONTENT, STATUS);
		cardDAO.insert(post);
		boardDAO.insert(new Board(2, "2"));
		cardDAO.insert(new Card(2, "2", "2", 2));
		cardDAO.insert(new Card(3, "2", "2", 3));

		String url = "http://localhost:" + port + "/post";
		ResponseEntity<Card[]> responseEntity = restTemplate.getForEntity(url, Card[].class);
		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		Card[] response = responseEntity.getBody();
		Assertions.assertThat(response[0].getId()).isEqualTo(ID);
		Assertions.assertThat(response[0].getAuthor()).isEqualTo(AUTHOR);
		Assertions.assertThat(response[0].getContent()).isEqualTo(CONTENT);
		Assertions.assertThat(response[0].getStatus()).isEqualTo(STATUS);
	}

	@Test
	public void testDelete() {
		Card post = new Card(ID, AUTHOR, CONTENT, STATUS);
		cardDAO.insert(post);

		String url = "http://localhost:" + port + "/post/" + DELETE_ID;
		restTemplate.delete(url);

		Assertions.assertThat(cardDAO.selectById(DELETE_ID)).isNull();
	}

	@Test
	public void testUpdate() {
		Card post = new Card(ID, AUTHOR, CONTENT, STATUS);
		cardDAO.insert(post);

		String url = "http://localhost:" + port + "/post";
		Card changPost = new Card(ID, "AUTHOR_TEST", "CONTENT_TEST", 1);
		restTemplate.put(url, changPost);

		post = cardDAO.selectById(ID);

		Assertions.assertThat(post.getId()).isEqualTo(ID);
		Assertions.assertThat(post.getAuthor()).isEqualTo("AUTHOR_TEST");
		Assertions.assertThat(post.getContent()).isEqualTo("CONTENT_TEST");
		Assertions.assertThat(post.getStatus()).isEqualTo(1);
	}
}
*/