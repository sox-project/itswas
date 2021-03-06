package egovframework.web;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egovframework.utils.ExcelUtils;
import egovframework.utils.KafkaUtils;
import egovframework.utils.KeyUtils;
import egovframework.utils.PrintUtils;
import egovframework.utils.SessionUtils;

/**
 * 장비 관리
 * [BCITS-AIAS-IF-011] 장비 목록 조회	{@link #getCameraList(HttpSession, String, String)}
 * [BCITS-AIAS-IF-012] 장비 등록		{@link #setCamera(HttpSession, String)}
 * [BCITS-AIAS-IF-013] 장비 수정		{@link #updateCamera(HttpSession, String)}
 * [BCITS-AIAS-IF-014] 장비 삭제		{@link #deleteCamera(HttpSession, String)}
 * [BCITS-AIAS-IF-015] 장비 업로드		{@link #setCameraList(HttpSession, MultipartFile)}
 */
@RestController
public class CameraController {

	/**
	 * [BCITS-AIAS-IF-011] 장비 목록 조회
	 * @param session
	 * @param deviceName		사용자가 조회 필터를 통해 입력한 장비 이름 정보
	 * @param deviceLocation	사용자가 조회 필터를 통해 입력한 장비 설치 위치 정보
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/cams")
	public String getCameraList(HttpSession session,
			@RequestParam(name = "f_device_name", required = false) String deviceName,
			@RequestParam(name = "f_device_location", required = false) String deviceLocation) throws Exception {
		// Request
		String topic = "cam_list";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		paramObject.put("f_device_name", deviceName);
		paramObject.put("f_device_location", deviceLocation);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-011] 장비 목록 조회", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-012] 장비 등록
	 * @param session
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cams")
	public String setCamera(HttpSession session, @RequestBody String params) throws Exception {
		// Request
		String topic = "add_cam";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-012] 장비 등록", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-013] 장비 수정
	 * @param session
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/cams")
	public String updateCamera(HttpSession session, @RequestBody String params) throws Exception {
		// Request
		String topic = "mod_cam";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-013] 장비 수정", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-014] 장비 삭제
	 * 배열을 받기 위해서 method type DELETE 대신 POST 사용
	 * @param session
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cams/delete")
	public String deleteCamera(HttpSession session, @RequestBody String params) throws Exception {
		// Request
		String topic = "del_cam";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-014] 장비 삭제", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-015] 장비 업로드
	 * @param session
	 * @param uploadFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cams/file")
	public String setCameraList(HttpSession session, @RequestParam("file") MultipartFile uploadFile) throws Exception {
		// Request
		String topic = "upload_cam";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONArray cameraArray = ExcelUtils.readCsvToJson(uploadFile.getInputStream());
		
		reqObject.put("data", cameraArray);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-015] 장비 업로드", reqObject);

		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
}
