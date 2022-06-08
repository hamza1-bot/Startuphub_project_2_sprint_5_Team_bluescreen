package com.startup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startup.model.Post;

/* In this class, all queries related to post table is defined.*/
@Transactional
@Repository
public class PostDAO {

	@Autowired
	EntityManager entityManager;

	// This method save the post in post table
	public Post save(Post post) {
		return entityManager.merge(post);
	}

	// This method get post by id from post table.
	public Post getPostById(Long id) {
		return entityManager.find(Post.class, id);
	}

	// This method get post of users from post table.
	@SuppressWarnings("unchecked")
	public List<Post> getUserPostList(Long userId) {
		return entityManager.createQuery("from Post where status = 1 and user=" + userId).getResultList();
	}

	// Get user list from user table.
	@SuppressWarnings("unchecked")
	public List<Post> getPostList(String condition) {
		return entityManager.createQuery("from Post where " + condition).getResultList();
	}

}
