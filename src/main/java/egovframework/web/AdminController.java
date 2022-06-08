package egovframework.web;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import egovframework.utils.KafkaUtils;
import egovframework.utils.KeyUtils;
import egovframework.utils.PrintUtils;
import egovframework.utils.SessionUtils;

/**
 * 사용자 관리
 * [BCITS-AIAS-IF-001] 사용자 조회 				{@link #getUserList(HttpSession)}
 */
@RestController
public class AdminController {
	
	/**
	 * [BCITS-AIAS-IF-001] 사용자 조회
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/users")
	public String getUserList(HttpSession session) throws Exception {
		// Request
		String topic = "user_alist";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		reqObject.put("data", new JSONObject());
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-001] 사용자 조회", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
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
		
		
		// Response
		String topicName = "del_user"; 
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
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
		
		
		// Response
		String topicName = "chan_permission";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
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
		
		
		// Response
		String topicName = "password_cycle_list";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
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
		
		
		// Response
		String topicName = "chan_pw_cycle";
		String receiveMsg = KafkaUtils.sendAndReceive(topicName, reqObject.toString());
		
		return receiveMsg;
	}
	
}
