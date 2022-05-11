package egovframework.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

public class SessionUtils {
	private static final String SESSION_KEY_USER = "user";
	private static final String SESSION_KEY_USER_ID = "u_id";
	private static final String SESSION_KEY_USER_NAME = "u_name";
	private static final String SESSION_KEY_USER_IP = "u_ip";

	private static final String REQUEST_KEY_USER_ID = "req_id";
	private static final String REQUEST_KEY_USER_NAME = "req_name";
	private static final String REQUEST_KEY_USER_IP = "req_ip";
	
	
	/**
	 * 세션에 유저 정보 저장
	 * @param session
	 * @param userObject
	 */
	public static void setUser(HttpSession session, HttpServletRequest request, JSONObject userObject) {
		if (session != null) {
			// IP 저장
			userObject.put(SESSION_KEY_USER_IP, request.getRemoteAddr());
			
			session.setAttribute(SESSION_KEY_USER, userObject);
		}
	}

	
	/**
	 * 세션에 저장된 유저 이름 수정
	 * @param session
	 * @param userName
	 */
	public static void setUserName(HttpSession session, String userName) {
		JSONObject userObject = getUser(session);
		
		if (!userObject.isEmpty()) {
			userObject.put(SESSION_KEY_USER_NAME, userName);
		}
	}
	
	
	/**
	 * 세션에서 유저 정보 가져오기
	 * @param session
	 * @return
	 */
	public static JSONObject getUser(HttpSession session) {
		Object object = session.getAttribute(SESSION_KEY_USER);
		
		if (object != null) {
			return (JSONObject) object;
		}
		
		return new JSONObject();
	}
	
	
	/**
	 * 세션에 있는 유저 정보에서 Key로 가져오기
	 * @param session
	 * @param key
	 * @return
	 */
	public static Object getUser(HttpSession session, String key) {
		JSONObject object = getUser(session);
		
		if (object.has(key)) {
			return object.get(key);
		}
		
		return null;
	}
	
	
	/**
	 * 세션에서 유저 아이디 가져오기
	 * @param session
	 * @return
	 */
	public static String getUserId(HttpSession session) {
		Object userId = getUser(session, SESSION_KEY_USER_ID);
		
		if (userId != null) {
			return String.valueOf(userId);
		}
		
		return new String();
	}
	
	
	/**
	 * 세션에서 유저 이름 가져오기
	 * @param session
	 * @return
	 */
	public static String getUserName(HttpSession session) {
		Object userName = getUser(session, SESSION_KEY_USER_NAME);
		
		if (userName != null) {
			return String.valueOf(userName);
		}
		
		return new String();
	}
	
	
	/**
	 * 세션에서 유저 아이피 가져오기
	 * @param session
	 * @return
	 */
	public static String getUserIp(HttpSession session) {
		Object userIp = getUser(session, SESSION_KEY_USER_IP);
		
		if (userIp != null) {
			return String.valueOf(userIp);
		}
		
		return new String();
	}
	
	
	/**
	 * 세션에서 Request로 보낼 데이터 가져오기
	 * @param session
	 * @return
	 */
	public static JSONObject getRequestInfo(HttpSession session) {
		JSONObject reqObject = new JSONObject();

		reqObject.put(REQUEST_KEY_USER_ID, getUserId(session));
		reqObject.put(REQUEST_KEY_USER_NAME, getUserName(session));
		reqObject.put(REQUEST_KEY_USER_IP, getUserIp(session));
	
		return reqObject;
	}
	
}
