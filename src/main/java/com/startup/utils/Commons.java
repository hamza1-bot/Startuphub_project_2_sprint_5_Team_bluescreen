package com.startup.utils;


import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;






public class Commons {
	
	//Generate filename 
	public static String getFileName(){
			String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			int RANDOM_STRING_LENGTH = 10;
			StringBuffer randStr = new StringBuffer();
			for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
				int number;
				int randomInt = 0;
				Random randomGenerator = new Random();
				randomInt = randomGenerator.nextInt(52);
				if (randomInt - 1 == -1) {
					number = randomInt;
				} else {
					number = randomInt - 1;
				}
				char ch = CHAR_LIST.charAt(number);
				randStr.append(ch);
			}
			return randStr.toString();
	 }
	
	
	
	

	
	/*********** Password encryption decryption************/
	public static String PasswordEncryption(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		return hashedPassword;
	}
	
	public static boolean PasswordDecryption(String password,String encryptedpass) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		boolean matches = passwordEncoder.matches(password, encryptedpass);
		return matches;
	}

	
}
