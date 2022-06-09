package egovframework.web;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.utils.KafkaUtils;
import egovframework.utils.KeyUtils;
import egovframework.utils.PrintUtils;
import egovframework.utils.SessionUtils;

/**
 * 로그 이력 관리
 * [BCITS-AIAS-IF-016] 로그 필터 코드 값 조회
 */
@RestController
public class LogController {

	/**
	 * [BCITS-AIAS-IF-016] 로그 필터 코드 값 조회
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/logs/type")
	public String getLogType(HttpSession session) throws Exception {
		// Request 
		String topic = "logtype_list";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		reqObject.put("data", new JSONObject());
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-016] 로그 필터 코드 값 조회", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
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
	
	
	/**
	 * 사용자 접속 통계 조회
	 * @param session
	 * @param userId		검색할 사용자
	 * @param startDate		로그 검색을 위해 조회 필터에서 설정한 로그 검색 시작 일시
	 * @param endDate		로그 검색을 위해 조회 필터에서 설정한 로그 검색 종료 일시
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"/logs/login/statistics", "/logs/login/statistics/{user_id}"})
	public String getLoginLog(HttpSession session,
			@PathVariable(name = "user_id", required = false) String userId,
			@RequestParam(name = "start_date", required = false) String startDate,
			@RequestParam(name = "end_date", required = false) String endDate) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		paramObject.put("user_id", userId != null? userId : "");
		paramObject.put("start_date", startDate);
		paramObject.put("end_date", endDate);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("사용자 접속 통계 조회 : " + reqObject.toString());
		
		
		// Response 
		JSONObject resObject = new JSONObject();
		JSONObject dataObject = new JSONObject();
		
		dataObject.put("20220101", "0");
		dataObject.put("20220102", "2");
		dataObject.put("20220103", "5");
		dataObject.put("20220104", "3");
		dataObject.put("20220105", "6");
		
		resObject.put("data", dataObject);
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
}
