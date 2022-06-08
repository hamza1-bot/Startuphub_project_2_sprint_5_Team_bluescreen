package com.startup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.startup.dao.UserDAO;
import com.startup.model.SendResume;
import com.startup.model.User;

/*
 * This class act as interface between user controller class and user dao class,
 * So that user controller class cannot directly access to the database class.
 * */
@Service
public class UserService {

	@Autowired
	UserDAO userDao;

	public User save(User user) {
		return userDao.save(user);
	}

	public User findByEmail(String email, int role) {
		return userDao.findByEmail(email, role);
	}

	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	public User findBySecretId(String secretId) {
		return userDao.findBySecretId(secretId);
	}

	public User findById(Long id) {
		return userDao.findById(id);
	}

	public User checkAuthentication(String email, String password) {
		return userDao.checkAuthentication(email, password);
	}

	public List<User> getUserList(String condition) {
		return userDao.getUserList(condition);
	}

	public List<User> searchUser(String text, Long userId) {
		return userDao.searchUser(text, userId);
	}

	public SendResume save(SendResume sendResume) {
		return userDao.saveResume(sendResume);
	}

	public List<SendResume> getResumeReceivedByUser(Long userId) {
		return userDao.getResumeReceivedByUser(userId);
	}
	
	public int deleteByCondition(String condition) {
		return userDao.deleteByCondition(condition);
	}
}
