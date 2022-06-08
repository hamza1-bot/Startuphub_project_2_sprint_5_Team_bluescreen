package com.startup.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

/* This class save as user table in database.
 * All user information will be saved in this table.
 */
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String mobile;
	private String location;

	private String city;
	private String state;
	private String country;
	@Type(type = "text")
	private String bio;

	private String cv;

	private Boolean status; // user status(if active then true else false)
	private Date createdOn;

	private String secretId;

	private String userImage;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name = "role")
	private Roles role;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private Set<Post> postList = new HashSet<Post>();

	@JsonIgnore
	@OneToMany(mappedBy = "sendTo", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private Set<SendResume> sendToList = new HashSet<SendResume>();

	@JsonIgnore
	@OneToMany(mappedBy = "sendBy", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private Set<SendResume> sendByList = new HashSet<SendResume>();

	@JsonIgnore
	@OneToMany(mappedBy = "sendBy", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private Set<Friends> sendByConnectionsList = new HashSet<Friends>();

	@JsonIgnore
	@OneToMany(mappedBy = "sendTo", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private Set<Friends> sendToConnectionsList = new HashSet<Friends>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getCv() {
		return cv;
	}

	public void setCv(String cv) {
		this.cv = cv;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public Set<Post> getPostList() {
		return postList;
	}

	public void setPostList(Set<Post> postList) {
		this.postList = postList;
	}

	public Set<SendResume> getSendToList() {
		return sendToList;
	}

	public void setSendToList(Set<SendResume> sendToList) {
		this.sendToList = sendToList;
	}

	public Set<SendResume> getSendByList() {
		return sendByList;
	}

	public void setSendByList(Set<SendResume> sendByList) {
		this.sendByList = sendByList;
	}

}
