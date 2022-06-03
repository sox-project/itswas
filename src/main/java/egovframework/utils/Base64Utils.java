package egovframework.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class Base64Utils {
	
	public static String encode(byte[] strBytes) {
		return Base64.getEncoder().withoutPadding().encodeToString(strBytes);
	}
	
	public static String encode(String str) throws UnsupportedEncodingException {
		return encode(str.getBytes()); 
	}
	
	public static String decode(String str) throws UnsupportedEncodingException {
		return new String(Base64.getDecoder().decode(str));
	}
	
}
