package com.startup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startup.model.Friends;

/* In this class, all queries related to friends table is defined.*/
@Transactional // select insert all are transactional quries

@Repository
public class FriendsDAO {

	@Autowired
	EntityManager entityManager;

	// This method save friend request send to other user
	public Friends addFriends(Friends friends) {
		return entityManager.merge(friends);
	}

	// This method get the friend by id from friend table
	public Friends getFriendsById(Long id) {
		return entityManager.find(Friends.class, id);
	}

	// This method update the status like 1 - send, 2 - accepted, 3 - rejected 4. removed in friend table
	public Friends updateFriends(Friends friends) {
		return entityManager.merge(friends);
	}

	// This method get the friends request received by users
	@SuppressWarnings("unchecked")
	public List<Friends> getReceivedFriendsListBySendTo(Long senderTo) {
		Query query = entityManager
				.createQuery("FROM Friends where sendTo ='" + senderTo + "' and status = 1 order by onDate desc");
		List<Friends> list = query.getResultList();
		return list;
	}

	// This method get the friends of users from friends table. 
	@SuppressWarnings("unchecked")
	public List<Friends> getFriendsListByUser(Long userId) {
		Query query = entityManager.createQuery(
				"FROM Friends where ( sendTo ='" + userId + "' OR sendBy ='" + userId + "') and status = 2");
		List<Friends> list = query.getResultList();
		return list;
	}

	// This method get check if the user is already friend with other user.
	public Friends getFriendsByUser(Long sendBy, Long sendTo) {
		try {
			String hql = "FROM Friends where status not in (3, 4) and ((sendBy = " + sendBy + " AND sendTo= " + sendTo
					+ ") OR (sendTo = " + sendBy + " AND sendBy=" + sendTo + "))";
			System.out.println(hql);
			return (Friends) entityManager.createQuery(hql).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
