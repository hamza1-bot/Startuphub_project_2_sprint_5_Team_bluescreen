package com.startup.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
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
import com.startup.service.UserService;
import com.startup.utils.Commons;

/*
 * This class is the controller class, it control all the request coming from the front end.
 * The requests related to post apis are defined in this class.
 * */
@Controller
@RequestMapping("/api/")
public class PostController {

	@Autowired
	UserService userService;

	@Autowired
	PostService postService;

	// This method serve the request for add post by user
	@RequestMapping(value = "addPost", method = RequestMethod.POST)
	public ResponseEntity<String> addPost(@RequestPart("data") String jsonStr,
			@RequestPart(value = "image", required = false) MultipartFile image, HttpServletRequest request)
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

			Post post = new Post();
			post.setDescription(jsonObject.getString("description"));
			post.setCreatedOn(new Date());
			post.setStatus(true);
			post.setUser(user);

			post = postService.save(post);

			if (image != null) {
				String dir = "StartupHub/resources/postImages/" + post.getId() + "/";

				if (!new File(dir).exists()) {
					new File(dir).mkdirs();
				}

				String fileName = image.getOriginalFilename();
				String ext = FilenameUtils.getExtension(fileName);
				fileName = Commons.getFileName() + "." + ext;
				InputStream fileContent = image.getInputStream();
				OutputStream outputStream = new FileOutputStream(new File(dir + "/" + fileName));
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = fileContent.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				String n = "postImages/" + post.getId() + "/";
				post.setImage(n + fileName);
				outputStream.close();
				fileContent.close();

				post = postService.save(post);
			}

			if (post != null) {
				objMap.put("post", post);
				objMap.put("message", "Post added successfully");
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

	// This method send the list of user post added by user.
	@RequestMapping(value = "getUserPost", method = RequestMethod.POST)
	public ResponseEntity<String> getUserPost(@RequestBody String jsonStr, HttpServletRequest request)
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

				List<Post> posts = postService.getUserPostList(user.getId());
				objMap.put("posts", posts);
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

	// This method serve the request for remove the post
	@RequestMapping(value = "removePost", method = RequestMethod.POST)
	public ResponseEntity<String> removePost(@RequestBody String jsonStr, HttpServletRequest request)
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

				Post post = postService.getPostById(jsonObject.getLong("postId"));
				post.setStatus(false);
				postService.save(post);

				objMap.put("message", "Post removed successfully.");
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
