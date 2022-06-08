package com.startup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.startup.dao.FriendsDAO;
import com.startup.model.Friends;

/*
 * This class act as interface between friend controller class and friend dao class,
 * So that friend controller class cannot directly access to the friend database class.
 * */
@Service
public class FriendsService {

	@Autowired
	FriendsDAO friendsDao;

	public Friends addFriends(Friends friends) {
		return friendsDao.addFriends(friends);
	}

	public Friends getFriendsById(Long id) {
		return friendsDao.getFriendsById(id);
	}

	public Friends updateFriends(Friends friends) {
		return friendsDao.updateFriends(friends);
	}

	public List<Friends> getReceivedFriendsListBySendTo(Long senderTo) {

		return friendsDao.getReceivedFriendsListBySendTo(senderTo);
	}

	public List<Friends> getFriendsListByUser(Long userId) {
		return friendsDao.getFriendsListByUser(userId);
	}

	public Friends getFriendsByUser(Long sendBy, Long sendTo) {
		return friendsDao.getFriendsByUser(sendBy, sendTo);
	}

}
