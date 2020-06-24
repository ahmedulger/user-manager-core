package com.ulger.sk.usermanager.api.user.manager;

/**
 * Main object of data source. This class should used data source operations like save or read.
 */
public interface UserEntity {

	/**
	 * Unique numeric identifier of entity
	 * @return id
	 */
	Object getId();

	/**
	 * Unique email of entity
	 * @return email
	 */
	String getEmail();

	/**
	 * @return firstName
	 */
	String getFirstName();

	/**
	 * @return lastName
	 */
	String getLastName();

	/**
	 * This field stores password of entity
	 * @return credential
	 */
	String getCredential();
}