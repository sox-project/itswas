package egovframework.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Utils {
	
	public static String encode(byte[] strBytes) {
		return Base64.getEncoder().withoutPadding().encodeToString(strBytes);
	}
	
	public static String encode(String str) throws UnsupportedEncodingException {
		return encode(str.getBytes()); 
	}
	
	public static byte[] decode(String str) {
		return Base64.getDecoder().decode(str);
	}
	
	public static String decodeToString(String str) throws UnsupportedEncodingException {
		return new String(decode(str));
	}
}
