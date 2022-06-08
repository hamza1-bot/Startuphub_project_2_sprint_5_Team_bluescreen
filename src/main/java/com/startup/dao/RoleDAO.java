package com.startup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startup.model.Roles;

/* In this class, all queries related to roles table is defined.*/
@Repository
@Transactional
public class RoleDAO {

	@Autowired
	EntityManager entityManager;

	// Get role by id from role table
	public Roles getRoleById(int id) {
		return entityManager.find(Roles.class, id);
	}

	// save role in role table
	public Roles addRoles(Roles role) {
		return entityManager.merge(role);
	}

	// get role list from role table
	@SuppressWarnings("unchecked")
	public List<Roles> getRolesList() {
		return entityManager.createQuery("from Roles").getResultList();
	}

}
