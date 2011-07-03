package com.aripio.wishlist;

/**
 * the basic class of user, contains user name and user password it could be
 * expanded to include more user info such as user contact etc.
 */

public class User {
	// --------------------- attributes ---------------------
	private String name;
	private String password;

	// --------------------- functions ----------------------
	// constructor 1
	public User() {
	}

	// constructor 2
	public User(String n, String p) {
		name = n;
		password = p;

	}

	public String getName() {
		return name;

	}

	public String getPassword() {
		return password;

	}

	public boolean setName(String n) {
		if (n == "") {
			return false;
		}

		name = n;
		return true;

	}

	public boolean setPassword(String p) {
		if (p == "") {
			return false;
		}

		password = p;
		return true;

	}

}
