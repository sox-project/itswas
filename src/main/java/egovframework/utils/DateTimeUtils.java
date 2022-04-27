package egovframework.utils;

import java.text.SimpleDateFormat;

public class DateTimeUtils {
	public static final String DATETIME_DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_FILE_PATTERN = "yyyyMMdd_HHmmss";
	
	public static String getDateTimeNow(String pattern) {
		if ("".equals(pattern)) {
			pattern = DATETIME_DEFAULT_PATTERN;
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		return simpleDateFormat.format(System.currentTimeMillis());
	}
}
