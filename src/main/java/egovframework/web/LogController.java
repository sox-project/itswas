package egovframework.web;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.utils.SessionUtils;

@RestController
public class LogController {

	/**
	 * 로그 필터 코드 값 조회
	 * @param session
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/logs/type")
	public String getLogType(HttpSession session) {
		// Request 
		JSONObject reqObject = new JSONObject();
		
		reqObject.put("data", new JSONObject());
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("로그 필터 코드 값 조회 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		
		String[] subTypes = {"user_management", "device_management"};
		String[] actions = {"add", "modify"};
		
		for (int i = 0; i < 2; i++) {
			JSONObject typeObject = new JSONObject();
			
			typeObject.put("l_type", "operate");
			typeObject.put("l_sub_type", subTypes[i]);
			typeObject.put("l_action", actions[i]);
			
			typeArray.put(typeObject);
		}
		
		resObject.put("data", typeArray);
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
	
	
	/**
	 * 운영 로그 이력 조회
	 * @param userId		해당 동작을 실행한 사용자 아이디
	 * @param subType		운영 로그 종류
	 * @param action		운영 로그 종류에 대한 동작
	 * @param startDate		로그 검색을 위해 조회 필터에서 설정한 로그 검색 시작 일시
	 * @param endDate		로그 검색을 위해 조회 필터에서 설정한 로그 검색 종료 일시
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"/logs/operation", "/logs/operation/{user_id}"})
	public String getOperationLog(HttpSession session,
			@PathVariable(name = "user_id", required = false) String userId, 
			@RequestParam(name = "sub_type", required = false, defaultValue = "") String subType,
			@RequestParam(name = "action", required = false, defaultValue = "") String action,
			@RequestParam(name = "start_date", required = false) String startDate,
			@RequestParam(name = "end_date", required = false) String endDate) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		paramObject.put("user_id", userId != null? userId : "");
		paramObject.put("sub_type", subType);
		paramObject.put("action", action);
		paramObject.put("start_date", startDate);
		paramObject.put("end_date", endDate);

		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("운영 로그 이력 조회 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONArray logArray = new JSONArray();
		
		String[] message = {
				"password cycle: 10 -> 90 alert cycle: 10 -> 7",
				"password cycle: 90 -> 45",
				"password cycle: 45 -> 30 alert cycle: 7 -> 5 operates: True -> False",
				"password cycle: 30 -> 90 alert cycle: 5 -> 10 operates: False -> True"
		};
		
		for (int i = 0; i < 4; i++) {
			JSONObject logObject = new JSONObject();
			JSONObject infoObject = new JSONObject();
			
			infoObject.put("user_id", userId);
			infoObject.put("user_name", "관리자01");
			infoObject.put("source_ip", "10.0.5.33");
			infoObject.put("action", action);
			infoObject.put("message", message[i]);

			logObject.put("infomation", infoObject);
			logObject.put("sub_type", subType);
			logObject.put("date", String.valueOf(System.currentTimeMillis() / 1000));
			
			logArray.put(logObject);
		}
		
		resObject.put("data", logArray);
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
}
