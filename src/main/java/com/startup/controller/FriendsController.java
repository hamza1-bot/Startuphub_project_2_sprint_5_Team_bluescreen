package com.startup.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.model.Friends;

import com.startup.model.User;
import com.startup.service.FriendsService;
import com.startup.service.UserService;

/*
 * This class is the controller class, it control all the request coming from the front end.
 * The requests related to friends apis are defined in this class.
 * */
@Controller
@RequestMapping("/api/")
public class FriendsController {
	@Autowired
	UserService userService;

	@Autowired
	FriendsService friendsService;

	// This method serve the request for send friend request to other user.
	@RequestMapping(value = "sendFriendRequest", method = RequestMethod.POST)
	public ResponseEntity<String> sendFriendRequest(@RequestBody String jsonStr, HttpServletRequest request)
	
	
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));

			if (user == null) {
				objMap.put("message", "Unauthorized");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
			}

			else {

				User otherUser = userService.findById(jsonObject.getLong("otherUserId"));
				Friends sendfriendRequest = new Friends();
				sendfriendRequest.setOnDate(new Date());
				sendfriendRequest.setSendBy(user);
				sendfriendRequest.setSendTo(otherUser);
				sendfriendRequest.setStatus(1); // 1 = sent

				friendsService.addFriends(sendfriendRequest);

				objMap.put("message", "Send successfully");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method return the friend requests received by user
	@RequestMapping(value = "receivedFriendRequest", method = RequestMethod.POST)
	public ResponseEntity<String> receivedFriendRequest(@RequestBody String jsonStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));

			if (user == null) {
				objMap.put("message", "Unauthorized");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
			}

			else {

				List<Friends> friends = friendsService.getReceivedFriendsListBySendTo(user.getId());
				objMap.put("friends", friends);
				objMap.put("message", "Successfully");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.OK);

			}

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method serve the request for accept or reject friend request
	@RequestMapping(value = "acceptOrRejectFriendRequest", method = RequestMethod.POST)
	public ResponseEntity<String> acceptOrRejectFriendRequest(@RequestBody String jsonStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));

			if (user == null) {
				objMap.put("message", "Unauthorized");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
			}

			else {

				Friends friend = friendsService.getFriendsById(jsonObject.getLong("friendId"));
				friend.setStatus(jsonObject.getInt("friendStatus")); // 2 - accepted, 3 - rejected
				friendsService.updateFriends(friend);

				if (jsonObject.getInt("friendStatus") == 2) {
					objMap.put("message", "Friend request accepted successfully");
				} else {
					objMap.put("message", "Friend request rejected successfully");

				}
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method serve the request for remove friend
	@RequestMapping(value = "removeFriend", method = RequestMethod.POST)
	public ResponseEntity<String> removeFriend(@RequestBody String jsonStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		
		
		
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));

			if (user == null) {
				objMap.put("message", "Unauthorized");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
			}

			else {

				Friends friend = friendsService.getFriendsById(jsonObject.getLong("friendId"));
				friend.setStatus(jsonObject.getInt("friendStatus")); // 4 removed
				friendsService.updateFriends(friend);

				objMap.put("message", "success");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method return the list of friends of user
	@RequestMapping(value = "myFriendsList", method = RequestMethod.POST)
	public ResponseEntity<String> myFriendsList(@RequestBody String jsonStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));

			if (user == null) {
				objMap.put("message", "Unauthorized");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
			}

			else {

				List<Friends> friends = friendsService.getFriendsListByUser(user.getId());
				objMap.put("friends", friends);
				objMap.put("message", "Successfully");
				objMap.put("status", 1);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.OK);

			}

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
