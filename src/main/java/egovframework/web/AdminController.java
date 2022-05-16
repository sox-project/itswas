package egovframework.web;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import egovframework.utils.SessionUtils;

@RestController
public class AdminController {
	
	/**
	 * 사용자 목록 조회
	 * @param session
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/users")
	public String getUserList(HttpSession session) {
		// Request
		JSONObject reqObject = new JSONObject();
		
		reqObject.put("data", new JSONObject());
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("사용자 목록 조회 : " + reqObject.toString());
		
		System.out.println(SessionUtils.getUser(session, "p_code"));
		
		// fail test 
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		} else {
			if (!"1".equals(String.valueOf(SessionUtils.getUser(session, "p_code")))) {
				result = "failed";
				message = "Invalid user permission";
			}
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONArray userArray = new JSONArray();
		
		if ("success".equals(result)) {
			for (int i = 1; i <= 10; i++) {
				JSONObject userObject = new JSONObject();
				
				userObject.put("u_idx", i);
				userObject.put("u_id", "user_" + i);
				userObject.put("u_password", "MGZkZWE0NTBjNmNi");
				userObject.put("u_name", "사용자" + i);
				userObject.put("p_code", 1);
				userObject.put("p_description", "General");
				
				userArray.put(userObject);
			}
		}

		resObject.put("data", userArray);
		resObject.put("res_info", UserController.getHttpResponse(result, message));
		
		return resObject.toString();
	}	
	
	
	/**
	 * 사용자 삭제
	 * @param paramMap	JSON
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users/{u_id}/delete")
	public String deleteUser(HttpSession session, @PathVariable("u_id") String userId) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		paramObject.put("u_id", userId);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("사용자 삭제 : " + reqObject.toString());
		
		
		// fail test
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		} else {
			if (!"1".equals(String.valueOf(SessionUtils.getUser(session, "p_code")))) {
				result = "failed";
				message = "Invalid user permission";
			}
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", UserController.getHttpResponse(result, message));
		
		return resObject.toString();
	}
	
	
	/**
	 * 사용자 권한 변경
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/users/permission")
	public String updateUserPermission(HttpSession session, @RequestBody String params) {
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("사용자 권한 변경 : " + reqObject.toString());
		
		
		// fail test 
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		} else {
			if (!"1".equals(String.valueOf(SessionUtils.getUser(session, "p_code")))) {
				result = "failed";
				message = "Invalid user permission";
			}
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", UserController.getHttpResponse(result, message));
		
		return resObject.toString();
	}
	
	
	/**
	 * 비밀번호 변경 주기 관련 데이터 목록 조회
	 * @param session
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/users/password/cycle")
	public String getPasswordCycle(HttpSession session) {
		// Request
		JSONObject reqObject = new JSONObject();
		
		reqObject.put("data", new JSONObject());
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("비밀번호 변경 주기 관련 데이터 목록 조회 : " + reqObject.toString());
		
		
		// fail test 
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		} else {
			if (!"1".equals(String.valueOf(SessionUtils.getUser(session, "p_code")))) {
				result = "failed";
				message = "Invalid user permission";
			}
		}
			
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONObject dataObject = new JSONObject();
		
		if ("success".equals(result)) {
			dataObject.put("c_idx", 1);
			dataObject.put("c_pw_change_cycle", 90);
			dataObject.put("c_pw_change_alert", 10);
			dataObject.put("c_pw_change_use", true);
		}
		
		resObject.put("data", dataObject);
		resObject.put("res_info", UserController.getHttpResponse(result, message));
		
		return resObject.toString();
	}
	
	
	/**
	 * 비밀번호 변경 주기 변경
	 * @param session
	 * @param params
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/users/password/cycle")
	public String UpdatePasswordCycle(HttpSession session, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("비밀번호 주기 변경 : " + reqObject.toString());
		
		
		// fail test
		String result = "success";
		String message = "";
		
		if (SessionUtils.getUser(session).isEmpty()) {
			result = "failed";
			message = "Not exist session";
		} else {
			if (!"1".equals(SessionUtils.getUser(session, "p_code"))) {
				result = "failed";
				message = "Invalid user permission";
			}
		}
		
		
		// Response
		JSONObject resObject = new JSONObject();
		resObject.put("data", new JSONObject());
		resObject.put("res_info", UserController.getHttpResponse(result, message));
		
		return resObject.toString();
	}
	
}
