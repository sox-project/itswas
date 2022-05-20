package egovframework.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import egovframework.utils.SessionUtils;

@RestController
public class UserController {
	
	/**
	 * 임시 : HTTP Handler (HTTP 200 반환)
	 * @return
	 */
	public static JSONObject getHttpResponse(String result, String message) {
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", System.currentTimeMillis() / 1000);
		resObject.put("code", 200);
		resObject.put("result", result);
		resObject.put("message", message);
		
		return resObject;
	}
	
	
	/**
	 *	임시 : 사용자 인증 (입력받은 아이디와 비밀번호를 세션에 넣음)
	 *	
	 *	# failed
	 *	- 사용자 이름은 홍길동, 권한은 관리자 모드를 기본으로 함
	 *	- apple이라는 아이디가 입력 시, 아이디 미존재 메시지
	 *	- banana/banana 로 로그인 시 아이디/비밀번호 불일치 메세지
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users/auth")
	public String login(HttpSession session, HttpServletRequest request, @RequestBody String params) {
		
		JSONObject paramObject = new JSONObject(params);
		
		// fail test
		String userId = paramObject.getString("u_id");
		String userPswd = paramObject.getString("u_password");
		
		String result = "success";
		String message = "";
		
		if ("apple".equals(userId)) {
			result = "failed";
			message = "Not exist user id";
		} else if ("banana".equals(userId) && "banana".equals(userPswd)) {
			result = "failed";
			message = "Invalid user id or password";
		} else {
			// 이름 입력 여부 확인
			if (!paramObject.has("u_name")) {
				paramObject.put("u_name", "홍길동");
			} 
			
			// 권한 입력 여부 확인
			if (!paramObject.has("p_code")) {
				paramObject.put("p_code", "1");
			}
			
			SessionUtils.setUser(session, request, paramObject);
		}
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse(result, message));
		
		return resObject.toString();
	}
	
	
	/**
	 * 사용자 등록
	 * 
	 * # failed
	 * - "banana"로 등록 시 중복 아이디 메세지
	 * @param params	JSON
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users")
	public String setUser(@RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params.toString());
		
		reqObject.put("data", paramObject);
		
		System.out.println("사용자 등록 : " + reqObject.toString());
		
		
		// failed test
		String result = "success";
		String message = "";
		
		String userId = paramObject.getString("u_id");
		if ("banana".equals(userId)) {
			result = "failed";
			message = "Exist user id";
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse(result, message));
		
		return resObject.toString();
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
		
		
		// fail test
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse(result, message));
		
		// Response가 제대로 오면, 세션에 저장된 이름 변경
		if ("success".equals(result)) {
			String userName = paramObject.getString("u_name");
			SessionUtils.setUserName(session, userName);
		}
		
		return resObject.toString();
	}
	
	
	/**
	 * 특정 사용자 프로필 조회 : 본인 프로필을 보는 기능
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"/users/", "/users/{u_id}"})
	public String getUserInfo(HttpSession session, @PathVariable(value = "u_id", required = false) String userId) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		// 본인 프로필 조회
		if (userId == null) {
			userId = SessionUtils.getUserId(session);
		}

		paramObject.put("u_id", userId);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("특정 사용자 프로필 조회 : " + reqObject.toString());
		
		
		// fail test
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONObject userObject = new JSONObject();
		
		if ("success".equals(result)) {
			userObject = SessionUtils.getUser(session);
			
			userObject.put("u_idx", 1);
			
			if ("1".equals(String.valueOf(SessionUtils.getUser(session, "p_code")))) {
				userObject.put("p_description", "Admin");
			} else {
				userObject.put("p_description", "General");
			}
		}
		
		resObject.put("data", userObject);
		resObject.put("res_info", getHttpResponse(result, message));
		
		return resObject.toString();
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
		
		
		// fail test 
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		}
		
		String curPswd = String.valueOf(SessionUtils.getUser(session, "u_password"));
		String inputPswd = paramObject.getString("current_password");
	
		if (!curPswd.equals(inputPswd)) {
			result = "failed";
			message = "invalid user current password";
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse(result, message));
		
		if ("success".equals(result)) {
			JSONObject sessionObject = SessionUtils.getUser(session);
			sessionObject.put("u_password", inputPswd);
		}
		
		return resObject.toString();
	}
	
}
