package com.startup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startup.model.SendResume;
import com.startup.model.User;

/* In this class, all queries related to users table is defined.*/
@Transactional
@Repository
public class UserDAO {

	@Autowired
	EntityManager entityManager;

	// Save user in user table.
	public User save(User user) {
		return entityManager.merge(user);
	}

	// This method find user by email and role in the user table.
	public User findByEmail(String email, int role) {
		try {
			User user = null;

			user = entityManager.createQuery("from User where email = '" + email + "' and role=" + role, User.class)
					.getSingleResult();

			return user;

		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}

	// This method find user by secretId in the user table.
	public User findBySecretId(String secretId) {
		try {

			return entityManager.createQuery("from User where secretId = '" + secretId + "'", User.class)
					.getSingleResult();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// This method find user by email in the user table.
	public User findByEmail(String email) {
		try {

			return entityManager.createQuery("from User where email = '" + email + "'", User.class).getSingleResult();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// This method find user in the user table by email id.
	public User findById(Long id) {
		return entityManager.find(User.class, id);
	}

	// This method find user by email and password in the user table.
	public User checkAuthentication(String email, String password) {
		try {
			String hql = "FROM User where email = '" + email + "' AND password= '" + password + "'";
			return (User) entityManager.createQuery(hql).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Get user list from user table.
	@SuppressWarnings("unchecked")
	public List<User> getUserList(String condition) {
		return entityManager.createQuery("from User where " + condition).getResultList();
	}

	// Search user from user table whose role is 2 (user role only).
	public List<User> searchUser(String text, Long userId) {
		try {
			Query query = entityManager.createQuery("FROM User where role=2  and id != " + userId
					+ " and upper(concat(firstName, ' ', lastName)) like '%" + text + "%'");
			// query.setFirstResult(0);
			// query.setMaxResults(10);
			@SuppressWarnings("unchecked")
			List<User> User = query.getResultList();
			return User;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Save sent resume in send resume table.
	public SendResume saveResume(SendResume sendResume) {
		return entityManager.merge(sendResume);
	}
	
	// This method get the resume received by users from send resume table. 
	@SuppressWarnings("unchecked")
	public List<SendResume> getResumeReceivedByUser(Long userId) {
		Query query = entityManager.createQuery(
				"FROM SendResume where sendTo =" + userId );
		List<SendResume> list = query.getResultList();
		return list;
	}
	
	public int deleteByCondition(String condition) {
		try{
			
			int i = entityManager.createNativeQuery("delete from "+condition).executeUpdate();
			return i;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}

}
