package com.startup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.startup.dao.PostDAO;
import com.startup.model.Post;

/*
 * This class act as interface between post controller class and post dao class,
 * So that post controller class cannot directly access to the post database class.
 * */
@Service
public class PostService {

	@Autowired
	PostDAO postDAO;

	public Post save(Post post) {
		return postDAO.save(post);
	}

	public Post getPostById(Long id) {
		return postDAO.getPostById(id);
	}

	public List<Post> getUserPostList(Long userId) {
		return postDAO.getUserPostList(userId);
	}
	
	public List<Post> getPostList(String condition) {
		return postDAO.getPostList(condition);
	}
}
