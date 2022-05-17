package egovframework.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import egovframework.utils.KafkaUtils;
import egovframework.utils.SessionUtils;

@RestController
public class UserController {
	
	/**
	 * 임시 : HTTP Handler (HTTP 200 반환)
	 * @return
	 */
	public static JSONObject getHttpResponse() {
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", System.currentTimeMillis() / 1000);
		resObject.put("code", 200);
		resObject.put("result", "success");
		resObject.put("message", "");
		
		return resObject;
	}
	
	
	/**
	 * 임시 : 사용자 인증, 로그인 시 입력받은 데이터를 세션에 넣음
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users/auth")
	public String login(HttpSession session, HttpServletRequest request, @RequestBody String params) {
		JSONObject paramObject = new JSONObject(params.toString());
		
		SessionUtils.setUser(session, request, paramObject);
		
		return SessionUtils.getUser(session).toString();
	}
	
	
	/**
	 * 사용자 등록
	 * @param params	JSON
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users")
	public String setUser(HttpServletRequest request, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params.toString());
		JSONObject infoObject = new JSONObject();
		
		// 입력받은 데이터를 Request 정보로 보냄
		infoObject.put("req_id", paramObject.has("u_id")? paramObject.get("u_id") : "");
		infoObject.put("req_name", paramObject.has("u_name")? paramObject.get("u_name") : "");
		infoObject.put("req_ip", request.getRemoteAddr());
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", infoObject);
		
		System.out.println("사용자 등록 : " + reqObject.toString());
		
		// Response
		String topicName = "add_user";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * 사용자 정보 수정
	 * @param paramMap	JSON
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/users")
	public String updateUser(HttpSession session, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		paramObject.put("u_id", SessionUtils.getUserId(session));
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("사용자 정보 수정 : " + reqObject.toString());
		
		
		// Response
		String topicName = "mod_user";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		JSONObject resObject = new JSONObject(receiveMsg);
		if ("success".equals(resObject.getString("result"))) {
			// Response가 제대로 오면, 세션에 저장된 이름 변경
			String userName = paramObject.getString("u_name");
			SessionUtils.setUserName(session, userName);	
		}
		
		return receiveMsg;
	}
	
	
	/**
	 * 특정 사용자 프로필 조회
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/user/me")
	public String getUserInfo(HttpSession session) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		paramObject.put("u_id", SessionUtils.getUserId(session));
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("특정 사용자 프로필 조회 : " + reqObject.toString());
		
		
		// Response
		String topicName = "user_info";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * 사용자 비밀번호 변경
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/users/password")
	public String updateUserPassword(HttpSession session, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		paramObject.put("u_id", SessionUtils.getUserId(session));
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("사용자 비밀번호 변경 : " + reqObject.toString());
		
		
		// Response
		String topicName = "mod_pw";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
	}
	
}
