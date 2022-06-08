package com.startup.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.model.Post;
import com.startup.model.User;
import com.startup.service.PostService;
import com.startup.service.RoleServices;
import com.startup.service.UserService;
import com.startup.utils.Commons;

/*
 * This class is the controller class, it control all the request coming from
 * the front end. The requests related to admin are defined in this class.
 */
@Controller
@RequestMapping("/api/")
public class AdminController {

	@Autowired
	UserService userService;

	@Autowired
	RoleServices roleService;

	@Autowired
	PostService postService;

	// This method serve the request for login the admin
	@RequestMapping(value = "adminLogin", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> login(@RequestBody String jsonStr) throws JSONException {
		
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject jsonObject = new JSONObject(jsonStr);
		User user = userService.findByEmail(jsonObject.getString("email"), 1);
		if (user != null) {

			boolean decrytedPassword = Commons.PasswordDecryption(jsonObject.getString("password"), user.getPassword());
			if (decrytedPassword) {

				Map<String, Object> data = new HashMap<String, Object>();

				SecureRandom random = new SecureRandom();
				user.setSecretId(new BigInteger(130, random).toString(32));

				userService.save(user);

				data.put("user", user);

				response.put("data", data);
				response.put("status", 1);
				response.put("code", "200");
				response.put("message", "Login successfully");
				return new ResponseEntity<>(response, HttpStatus.OK);

			} else {

				response.put("status", 0);
				response.put("message", "Email or password incorrect.");
				return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);

			}

		} else {

			response.put("status", 0);
			response.put("message", "Email or password incorrect.");
			return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);

		}

	}

	// This method return the list of all registered users
	@RequestMapping(value = "getUsers", method = RequestMethod.POST)
	public ResponseEntity<String> searchUsers() throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {

			String condition = "role = 2";

			List<User> users = userService.getUserList(condition);

			objMap.put("list", users);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
		} catch (Exception e) {
			objMap.put("message", e.getMessage());
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method serve the request for block the user from admin
	@RequestMapping(value = "blockUnblockUser", method = RequestMethod.POST)
	public ResponseEntity<String> blockUnblockUser(@RequestBody String jsonStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);

			User user = userService.findById(jsonObject.getLong("userId"));
			user.setStatus(jsonObject.getBoolean("status"));
			userService.save(user);

			if(jsonObject.getBoolean("status")) {
				objMap.put("message", "User unblocked successfully.");
			} else {
				objMap.put("message", "User blocked successfully.");
			}
			objMap.put("status", 1);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method return the list of all post posted by users
	@RequestMapping(value = "getPosts", method = RequestMethod.POST)
	public ResponseEntity<String> searchPosts() throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {

			String condition = " status = 1";

			List<Post> post = postService.getPostList(condition);

			objMap.put("list", post);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
		} catch (Exception e) {
			objMap.put("message", e.getMessage());
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method return the dashboard data
	@RequestMapping(value = "dashboardData", method = RequestMethod.POST)
	public ResponseEntity<String> dashboardData() throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {

			String condition = "role = 2 and status =1";
			List<User> users = userService.getUserList(condition);
			objMap.put("activeUserCount", users.size());

			condition = "role = 2 and status =0";
			users = userService.getUserList(condition);
			objMap.put("deactiveUserCount", users.size());

			condition = " status = 1";
			List<Post> post = postService.getPostList(condition);
			objMap.put("postCount", post.size());

			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
		} catch (Exception e) {
			objMap.put("message", e.getMessage());
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method serve the request for remove the post
	@RequestMapping(value = "deletePost", method = RequestMethod.POST)
	public ResponseEntity<String> deletePost(@RequestBody String jsonStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);

			Post post = postService.getPostById(jsonObject.getLong("postId"));
			post.setStatus(false);
			postService.save(post);

			objMap.put("message", "Post removed successfully.");
			objMap.put("status", 1);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			objMap.put("message", e.getMessage());
			objMap.put("status", 0);
			String jsonInString = mapper.writeValueAsString(objMap);
			return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// this method helps in edit the profile detail of user by admin.
		@RequestMapping(value = "editProfileByAdmin", method = RequestMethod.POST)
		public ResponseEntity<String> editProfileByAdmin(@RequestPart("data") String jsonStr,
				@RequestPart(value = "userImage", required = false) MultipartFile userImage, HttpServletRequest request)
				throws JsonProcessingException, ParseException {
			Map<String, Object> objMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				User user = userService.findById(jsonObject.getLong("userId"));
				System.out.println("jsonObject.getString(\"password\") = " + jsonObject.getString("password"));
				if(jsonObject.has("password") && !jsonObject.getString("password").isEmpty()) {
					String encryptedPassword = Commons.PasswordEncryption(jsonObject.getString("password"));
					user.setPassword(encryptedPassword);
				}
				user.setFirstName(jsonObject.getString("firstName"));
				user.setLastName(jsonObject.getString("lastName"));
				user.setMobile(jsonObject.getString("mobile"));
				user.setLocation(jsonObject.getString("location"));
				user.setCity(jsonObject.getString("city"));
				user.setState(jsonObject.getString("state"));
				user.setCountry(jsonObject.getString("country"));
				user.setBio(jsonObject.getString("bio"));

				if (userImage != null) {
					String dir = "StartupHub/resources/userImages/" + user.getId() + "/";

					if (!new File(dir).exists()) {
						new File(dir).mkdirs();
					}

					String fileName = userImage.getOriginalFilename();
					String ext = FilenameUtils.getExtension(fileName);
					fileName = Commons.getFileName() + "." + ext;
					InputStream fileContent = userImage.getInputStream();
					OutputStream outputStream = new FileOutputStream(new File(dir + "/" + fileName));
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = fileContent.read(bytes)) != -1) {
						outputStream.write(bytes, 0, read);
					}
					String n = "userImages/" + user.getId() + "/";
					user.setUserImage(n + fileName);
					outputStream.close();
					fileContent.close();
				}


				User user2 = userService.save(user);

				if (user2 != null) {
					objMap.put("user", user2);
					objMap.put("message", "Profile updated successfully");
					objMap.put("status", 1);
					String jsonInString = mapper.writeValueAsString(objMap);
					return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
				} else {
					objMap.put("message", "Something went wrong.");
					objMap.put("status", 0);
					String jsonInString = mapper.writeValueAsString(objMap);
					return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
				}

			} catch (Exception e) {
				e.printStackTrace();
				objMap.put("message", e.getMessage());
				objMap.put("status", 0);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// this method reset the password of the admin.
		@RequestMapping(value = "resetPassword", method = RequestMethod.PUT)
		public ResponseEntity<String> resetPassword(@RequestBody String objStr, HttpServletRequest request)
				throws JsonProcessingException, ParseException {
			Map<String, Object> objMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			try {
				JSONObject jsonObject = new JSONObject(objStr);
				User user = userService.findById(jsonObject.getLong("userId"));
				if (user != null) {
					if (jsonObject.getString("currentPassword").equals(user.getPassword())) {
						String encryptedPassword = Commons.PasswordEncryption(jsonObject.getString("password"));
						user.setPassword(encryptedPassword);
						userService.save(user);
						objMap.put("message", "Password changed successfully.");
						objMap.put("status", true);
						String jsonInString = mapper.writeValueAsString(objMap);
						return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
					} else {

						objMap.put("message", "Current password is incorrect");
						objMap.put("status", false);
						String jsonInString = mapper.writeValueAsString(objMap);
						return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
					}

				} else {
					objMap.put("message", "Unauthorized");
					objMap.put("status", "0");
					String jsonInString = mapper.writeValueAsString(objMap);
					return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
				}
			} catch (Exception e) {
				objMap.put("message", e.getMessage());
				objMap.put("status", false);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// this method will remove the user from the database
		@RequestMapping(value = "removeUser", method = RequestMethod.POST)
		public ResponseEntity<String> removeUser(@RequestBody String objStr, HttpServletRequest request)
				throws JsonProcessingException, ParseException {
			Map<String, Object> objMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			try {
				JSONObject jsonObject = new JSONObject(objStr);

				User user = userService.findById(jsonObject.getLong("id"));

				String condition = "";

				condition = "sendresume where send_to = " + user.getId() + " or send_by =" + user.getId();
				userService.deleteByCondition(condition);

				condition = "friends where send_to = " + user.getId() + " or send_by =" + user.getId();
				userService.deleteByCondition(condition);

				condition = "post where user =" + user.getId();
				userService.deleteByCondition(condition);

				condition = "user where id =" + user.getId();
				int i = userService.deleteByCondition(condition);

				if (i == 1) {

					objMap.put("message", "Users deleted successfully.");
					objMap.put("status", "1");
					String jsonInString = mapper.writeValueAsString(objMap);
					return new ResponseEntity<String>(jsonInString, HttpStatus.OK);

				} else {
					objMap.put("message", "Something went wrong.");
					objMap.put("status", "0");
					String jsonInString = mapper.writeValueAsString(objMap);
					return new ResponseEntity<String>(jsonInString, HttpStatus.OK);
				}

			} catch (Exception e) {
				objMap.put("message", e.getMessage());
				objMap.put("status", false);
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

}
