package it.lucavercelli.photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5util {

	/**
	 * Calculate MD5
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @see https://stackoverflow.com/questions/4883145
	 *      http://www.rgagnon.com/javadetails/java-0416.html
	 */
	public static byte[] createChecksum(File f) throws NoSuchAlgorithmException, IOException {

		InputStream fis = new FileInputStream(f);
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	/**
	 * Calculate MD5/HEX
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @see https://stackoverflow.com/questions/4883145
	 *      http://www.rgagnon.com/javadetails/java-0416.html
	 */
	public static String getMD5Checksum(File f) throws NoSuchAlgorithmException, IOException {
		byte[] b = createChecksum(f);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
