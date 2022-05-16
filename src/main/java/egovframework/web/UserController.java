package egovframework.web;

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
	public static JSONObject getHttpResponse() {
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", System.currentTimeMillis() / 1000);
		resObject.put("code", 200);
		resObject.put("result", "success");
		resObject.put("message", "");
		
		return resObject;
	}
	
	
	/**
	 * 사용자 등록
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
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse());
		
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
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse());
		
		// Response가 제대로 오면, 세션에 저장된 이름 변경
		String userName = paramObject.getString("u_name");
		SessionUtils.setUserName(session, userName);
		
		return resObject.toString();
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
		JSONObject resObject = new JSONObject();
		JSONObject userObject = new JSONObject();
		
		userObject.put("u_idx", 1);
		userObject.put("u_id", "user_1");
		userObject.put("u_password", "MGZkZWE0NTBjNmNi");
		userObject.put("u_name", "사용자01");
		userObject.put("p_code", 1);
		userObject.put("p_description", "General");
		
		resObject.put("data", userObject);
		resObject.put("res_info", getHttpResponse());
		
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
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", getHttpResponse());
		
		return resObject.toString();
	}
	
}
