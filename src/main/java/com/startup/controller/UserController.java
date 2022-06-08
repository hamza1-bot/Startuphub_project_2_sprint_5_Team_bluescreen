package com.startup.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.model.Friends;
import com.startup.model.Mail;
import com.startup.model.SendResume;
import com.startup.model.User;
import com.startup.service.EmailService;
import com.startup.service.FriendsService;
import com.startup.service.RoleServices;
import com.startup.service.UserService;
import com.startup.utils.Commons;

/*
 * This class is the controller class, it control all the request coming from the front end.
 * The requests related to user apis are defined in this class.
 * */
@Controller
@RequestMapping("/api/")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	EmailService emailService;

	@Autowired
	RoleServices roleService;

	@Autowired
	FriendsService friendsService;

	
	// this method is serve the request for signup.
	@RequestMapping(value = "signUp", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> signUp(@RequestBody String jsonStr) throws JSONException {
		Map<String, Object> response = new HashMap<String, Object>();
		try {

			JSONObject jsonObject = new JSONObject(jsonStr);

			// check in db if user exist
			User user = userService.findByEmail(jsonObject.getString("email"), 2); // 2 is the role of the user

			// if user is not find then create new user profile
			if (user == null) {
				user = new User();
				Map<String, Object> data = new HashMap<String, Object>();

				user.setEmail(jsonObject.getString("email"));
				user.setCreatedOn(new Date());
				user.setFirstName(jsonObject.getString("firstName"));
				user.setLastName(jsonObject.getString("lastName"));
				
				String encryptedPassword = Commons.PasswordEncryption(jsonObject.getString("password"));
				user.setPassword(encryptedPassword);
				user.setRole(roleService.getRoleById(2));
				user.setStatus(true);
				SecureRandom random = new SecureRandom();
				user.setSecretId(new BigInteger(130, random).toString(32));

				User user2 = userService.save(user);

				data.put("user", user2);
				response.put("data", data);
				response.put("status", 1);
				response.put("message", "Your account has been created successfully.");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else {
				response.put("status", 0);
				response.put("message", "Email, already registered with us.");
				return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			response.put("data", "");
			response.put("status", 0);
			response.put("message", "Error while creating an account");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// this method is serve the login request.
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> login(@RequestBody String jsonStr) throws JSONException {
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject jsonObject = new JSONObject(jsonStr);
		User user = userService.findByEmail(jsonObject.getString("email"), 2);
		// check in db if user exist with email address
		if (user != null) {
			boolean decrytedPassword = Commons.PasswordDecryption(jsonObject.getString("password"), user.getPassword());


			if (decrytedPassword) {
				
				if(!user.getStatus()) {
					response.put("status", 1);
					response.put("message", "You have been blocked by Admin.");
					return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
				}

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
			response.put("message", "User doesn't exist or removed from the admin.");
			return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	// this method is serve the request of view profile of user.
	@RequestMapping(value = "viewProfile", method = RequestMethod.POST)
	public ResponseEntity<String> viewProfile(@RequestBody String jsonStr, HttpServletRequest request)
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
			} else {
				objMap.put("user", user);
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

	
	// this method helps in edit the profile detail of user.
	@RequestMapping(value = "editProfile", method = RequestMethod.POST)
	public ResponseEntity<String> editProfile(@RequestPart("data") String jsonStr,
			@RequestPart(value = "cv", required = false) MultipartFile cv,
			@RequestPart(value = "userImage", required = false) MultipartFile userImage, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));

			if (user == null) {
				objMap.put("message", "Unauthorized");
				objMap.put("status", "0");
				String jsonInString = mapper.writeValueAsString(objMap);
				return new ResponseEntity<String>(jsonInString, HttpStatus.UNAUTHORIZED);
			}

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

			if (cv != null) {
				String dir = "StartupHub/resources/cv/" + user.getId() + "/";

				if (!new File(dir).exists()) {
					new File(dir).mkdirs();
				}

				String fileName = cv.getOriginalFilename();
				String ext = FilenameUtils.getExtension(fileName);
				fileName = Commons.getFileName() + "." + ext;
				InputStream fileContent = cv.getInputStream();
				OutputStream outputStream = new FileOutputStream(new File(dir + "/" + fileName));
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = fileContent.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				String n = "cv/" + user.getId() + "/";
				user.setCv(n + fileName);
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

	// this method change the password of the user.
	@RequestMapping(value = "changePassword", method = RequestMethod.PUT)
	public ResponseEntity<String> changePassword(@RequestBody String objStr, HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("change password");
			JSONObject jsonObject = new JSONObject(objStr);
			User user = userService.findBySecretId(jsonObject.getString("secretId"));
			if (user != null) {
				
				boolean decrytedPassword = Commons.PasswordDecryption(jsonObject.getString("currentPassword"), user.getPassword());
				
				if (decrytedPassword) {
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

	// this method is serve the request of forgot password.
	@RequestMapping(value = "forgotPassword", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody String jsonStr) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {

			JSONObject jsonObject = new JSONObject(jsonStr);
			Integer role = 2;
			if(jsonObject.has("role")) {
				role = 1;
			}
			User user = userService.findByEmail(jsonObject.getString("email"), role);
			System.out.println("use ===================================== " + user);
			if (user != null) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("userId", user.getId());
				data.put("email", user.getEmail());
				Map<String, Object> mailData = new HashMap<String, Object>();
				mailData.put("urlLink", "http://localhost:8010/StartupHub/api/" + user.getId() + "/resetPassword?key="
						+ user.getSecretId());

				Mail mail = new Mail();
				mail.setTo(user.getEmail());
				mail.setModel(mailData);
				mail.setSubject("Startup Hub Reset Password");

				emailService.sendResetPasswordMail(mail);

				response.put("data", data);
				response.put("status", 1);
				response.put("code", "200");
				response.put("message",
						"An email has been sent to the address provided. Follow the instructions in the email to reset your password");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.put("status", 0);
				response.put("code", "400");
				response.put("message", "Email is not registered with us.");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {

			System.out.println(e.getMessage());

			response.put("data", "");
			response.put("status", 0);
			response.put("code", "500");
			response.put("message", "Something went wrong while sending email.");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This method reset the user password
	@RequestMapping(value = "{id}/resetPassword", method = RequestMethod.GET)
	public String resetPassword(@PathVariable("id") Long id, @RequestParam("key") String key, Model model) {
		User user = userService.findById(id);
		System.out.println("Key is >>> " + key);

		model.addAttribute("email", user.getEmail());
		model.addAttribute("id", user.getId());
		return "/reset_password/ResetPassword";
	}

	// this method update the user password from forget password email
	@RequestMapping(value = "{id}/updatePassword", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable("id") Long id, @RequestBody String jsonStr)
			throws JSONException {
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject jsonObject = new JSONObject(jsonStr);
		User user = userService.findById(id);

		if (user.getEmail().equals(jsonObject.getString("key"))) {
			
			String encryptedPassword = Commons.PasswordEncryption(jsonObject.getString("password"));

			user.setPassword(encryptedPassword);
			userService.save(user);
			response.put("data", "");
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Password updated successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("data", "");
			response.put("status", "Failed");
			response.put("code", "400");
			response.put("message", "Bad Credentials");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// This method serve the request for view other user profile
	@RequestMapping(value = "otherUserProfile", method = RequestMethod.POST)
	public ResponseEntity<String> otherUserProfile(@RequestBody String jsonStr, HttpServletRequest request)
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
				objMap.put("otherUser", otherUser);
				Friends friends = friendsService.getFriendsByUser(user.getId(), jsonObject.getLong("otherUserId"));
				if (friends != null) {
					objMap.put("isFriend", true);
					objMap.put("friendObj", friends);
				} else {
					objMap.put("isFriend", false);
				}

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

	
	// This method search the user from find friend screen
	@RequestMapping(value = "searchUser", method = RequestMethod.POST)
	public ResponseEntity<String> searchUser(@RequestBody String jsonStr, HttpServletRequest request)
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
				String text = jsonObject.getString("text");
				List<User> usersList = userService.searchUser(text.toUpperCase(), user.getId());

				objMap.put("usersList", usersList);
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

	
	// This method serve the request for send resume
	@RequestMapping(value = "sendResume", method = RequestMethod.POST)
	public ResponseEntity<String> sendResume(@RequestBody String jsonStr, HttpServletRequest request)
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
				SendResume sendResume = new SendResume();
				sendResume.setOnDate(new Date());
				sendResume.setSendBy(user);
				sendResume.setSendTo(otherUser);
				userService.save(sendResume);
				objMap.put("message", "Sent successfully");
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
	
	// This method return the resume received by user 
	@RequestMapping(value = "receivedResume", method = RequestMethod.POST)
	public ResponseEntity<String> receivedResume(@RequestBody String jsonStr, HttpServletRequest request)
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

				List<SendResume> resumes = userService.getResumeReceivedByUser(user.getId());
				objMap.put("resumes", resumes);
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
