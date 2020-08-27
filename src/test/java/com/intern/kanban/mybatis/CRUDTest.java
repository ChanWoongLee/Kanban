package com.intern.kanban.mybatis;
/*
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.intern.kanban.model.dto.Post;
import com.intern.kanban.service.PostService;

// Service Layer, Data Access Layer, DB에서의 CRUD 테스트.

@RunWith(SpringRunner.class)
@SpringBootTest
public class CRUDTest {

	@Autowired
	PostService postService;

	private Post post;
	private static final int ID = 1;
	private static final String AUTHOR = "first_author";
	private static final String CONTENT = "first_content";
	private static final int STATUS = 1;

	private static final int NEW_ID = 2;
	private static final String NEW_AUTHOR = "new_author";
	private static final String NEW_CONTENT = "new_content";
	private static final int NEW_STATUS = 2;

	private static final String CHANGED_AUTHOR = "changed_author";
	private static final String CHANGED_CONTENT = "changed_content";
	private static final int CHANGED_STATUS = 3;

	// 테스트 시 접근하는 레코드 수
	// 1개를 읽거나 추가하거나 삭제하는 경우만 있으므로 1이다.
	private static final int NUM_OF_ROWS = 1;

	// 테이블 생성 후 레코드 1개 추가
	@Before
	public void setUp() {
		postService.createTable();
		post = new Post(ID, AUTHOR, CONTENT, STATUS);
		postService.insert(post);
	}

	// 테이블 레코드 제거 및 테이블 제거
	@After
	public void testComplete() {
		postService.deletePostRecord();
		postService.dropTable();
	}

	// @Before에서 추가된 레코드 1개를 select 한다.
	@Test
	public void testSelectById() {
		// when
		Post post = postService.selectById(ID);

		// then
		assertEquals(ID, post.getId());
		assertEquals(AUTHOR, post.getAuthor());
		assertEquals(CONTENT, post.getContent());
		assertEquals(STATUS, post.getStatus());
	}

	@Test
	public void testInsert() {
		// given
		Post newPost = new Post(NEW_ID, NEW_AUTHOR, NEW_CONTENT, NEW_STATUS);

		// when
		int result = postService.insert(newPost); // result는 insert가 추가 시키는 레코드의 개수를 의미한다.
		Post insertedPost = postService.selectById(newPost.getId());

		// then
		assertEquals(NUM_OF_ROWS, result, 0);
		assertEquals(NEW_ID, insertedPost.getId(), 0);
		assertEquals(NEW_AUTHOR, insertedPost.getAuthor());
		assertEquals(NEW_CONTENT, insertedPost.getContent());
		assertEquals(NEW_STATUS, insertedPost.getStatus());
	}

	// 전체 조회 테스트
	@Test
	public void testSelect() {

		// when
		List<Post> posts = postService.select();

		// then
		for (Post post : posts) {
			Post compPost = postService.selectById(post.getId());
			assertEquals(post.getId(), compPost.getId());
			assertEquals(post.getAuthor(), compPost.getAuthor());
			assertEquals(post.getContent(), compPost.getContent());
			assertEquals(post.getStatus(), compPost.getStatus());
		}
	}

	@Test
	public void testUpdate() {
		// given
		Post toBeUpdatePost = new Post(ID, CHANGED_AUTHOR, CHANGED_CONTENT, CHANGED_STATUS);

		// when
		int result = postService.update(toBeUpdatePost); // result는 insert가 업데이트 시키는 레코드의 개수를 의미한다.
		Post updatedPost = postService.selectById(toBeUpdatePost.getId());

		// given
		assertEquals(NUM_OF_ROWS, result, 0);
		assertEquals(ID, updatedPost.getId(), 0);
		assertEquals(CHANGED_AUTHOR, updatedPost.getAuthor());
		assertEquals(CHANGED_CONTENT, updatedPost.getContent());
		assertEquals(CHANGED_STATUS, updatedPost.getStatus());
	}

	// 레코드 1개만 삭
	@Test
	public void testDelete() {

		// given
		Post toBeDeletePost = new Post(ID, AUTHOR, CONTENT, STATUS);

		// when
		int result = postService.deleteById(toBeDeletePost.getId()); // result는 insert가 업데이트 시키는 레코드의 개수를 의미한다.

		assertEquals(NUM_OF_ROWS, result, 0);

		// id로 select를 해서 조회 되지 않으면 null이다. null이면 해당 레코드가 삭제됨. 테스트 통과
		assertNull(postService.selectById(toBeDeletePost.getId()));
	}
}*/
