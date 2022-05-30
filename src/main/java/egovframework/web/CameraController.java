package egovframework.web;

import java.io.IOException;

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
import egovframework.utils.SessionUtils;

@RestController
public class CameraController {

	/**
	 * 장비 목록 조회
	 * @param session
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/cams")
	public String getCameraList(HttpSession session,
			@RequestParam("f_device_name") String deviceName,
			@RequestParam("f_device_location") String deviceLocation) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		paramObject.put("f_device_name", deviceName);
		paramObject.put("f_device_location", deviceLocation);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("장비 목록 조회 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONArray camArray = new JSONArray();
		
		for (int i = 1; i <= 10; i++) {
			JSONObject camObject = new JSONObject();
			
			camObject.put("c_idx", i);
			camObject.put("c_name", "cam_" + i);
			camObject.put("c_id", "amdin");
			camObject.put("c_password", "cam1234");
			camObject.put("c_ip", "10.110.1.11");
			camObject.put("c_port", 5700);
			camObject.put("c_rtsp_url", "rtsp://...");
			camObject.put("c_width", 1920);
			camObject.put("c_height", 1080);
			camObject.put("c_location", "가나구 다라동 마바사 사거리");
			camObject.put("c_latitude", "37.487744");
			camObject.put("c_longitude", "127.031916");
			camObject.put("c_description", "가나구 다라동 마바사 사거리에 설치된 CCTV");
			
			camArray.put(camObject);
		}
		
		resObject.put("data", camArray);
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
	
	
	/**
	 * 장비 등록
	 * @param session
	 * @param params
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cams")
	public String setCamera(HttpSession session, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("장비 등록 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
	
	
	/**
	 * 장비 수정
	 * @param session
	 * @param params
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/cams")
	public String updateCamera(HttpSession session, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("장비 수정 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
	
	
	/**
	 * 장비 삭제
	 * 배열을 받기 위해서 method type DELETE 대신 POST 사용
	 * @param session
	 * @param params
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cams/delete")
	public String deleteCamera(HttpSession session, @RequestBody String params) {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("장비 삭제 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		
		resObject.put("data", new JSONObject());
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
	
	
	/**
	 * 장비 업로드 (.csv)
	 * @param session
	 * @param uploadFile
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/cams/file")
	public String setCameraList(HttpSession session, @RequestParam("file") MultipartFile uploadFile) throws IOException {
		// Request
		JSONObject reqObject = new JSONObject();
		JSONArray cameraArray = ExcelUtils.readCsvToJson(uploadFile.getInputStream());
		
		reqObject.put("data", cameraArray);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session));
		
		System.out.println("장비 업로드 : " + reqObject.toString());
		
		
		// Response
		JSONObject resObject = new JSONObject();
		JSONObject dataObject = new JSONObject();
		
		dataObject.put("result", "success");
		dataObject.put("complete_count", cameraArray.length());
		dataObject.put("failure_count", 0);
		dataObject.put("failure_info", new JSONArray());
		
		resObject.put("data", dataObject);
		resObject.put("res_info", UserController.getHttpResponse());
		
		return resObject.toString();
	}
	
}
