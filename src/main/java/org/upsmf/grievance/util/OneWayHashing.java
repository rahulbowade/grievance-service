package org.upsmf.grievance.util;

import java.security.MessageDigest;

import lombok.NoArgsConstructor;

/**
 * @author Juhi Agarwal This class will hash data only one way. once it
 *         encrypted you can't decrypt it.
 */
@NoArgsConstructor
public class OneWayHashing {

	/**
	 * This method will encrypt value using SHA-256 . it is one way encryption.
	 *
	 * @param val
	 *            String
	 * @return String encrypted value or empty in case of exception
	 */

	public static String encryptVal(String val) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(val.getBytes());
			byte[] byteData = md.digest();
			
			StringBuilder sb = new StringBuilder();
			for (byte element : byteData) {
				sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
			}
			
			
			return sb.toString();
		} catch (Exception e) {
			
		}
		return "";
	}
}
