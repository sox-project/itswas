package egovframework.utils;

import java.util.UUID;

public class KeyUtils {
	
	/**
	 * UUID 발급
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
}
