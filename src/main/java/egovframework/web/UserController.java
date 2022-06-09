package egovframework.web;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import egovframework.utils.Base64Utils;
import egovframework.utils.CipherUtils;
import egovframework.utils.KafkaUtils;
import egovframework.utils.KeyUtils;
import egovframework.utils.PrintUtils;
import egovframework.utils.SessionUtils;

/**
 * 사용자 관리
 * [BCITS-AIAS-IF-002] 사용자 등록 				{@link #setUser(HttpServletRequest, String)}
 * [BCITS-AIAS-IF-003] 사용자 수정 				{@link #updateUser(HttpSession, String)}
 * [BCITS-AIAS-IF-005] 특정 사용자 프로필 조회 		{@link #getUserInfo(HttpSession, String)}
 * [BCITS-AIAS-IF-006] 사용자 비밀번호 변경 		{@link #updateUserPassword(HttpSession, String)}
 * 
 * 사용자 인증
 * [BCITS-AIAS-IF-020] 로그인 					{@link #login(HttpSession, HttpServletRequest, String)}
 */
@RestController
public class UserController {
	
	/**
	 * [BCITS-AIAS-IF-002] 사용자 등록
	 * @param params	JSON
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * @throws InterruptedException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users")
	public String setUser(HttpServletRequest request, @RequestBody String params) throws JSONException, UnsupportedEncodingException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, JsonMappingException, JsonProcessingException {
		// Request
		String topic = "add_user";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		JSONObject infoObject = new JSONObject();
		
		infoObject.put("req_ip", request.getRemoteAddr());
		infoObject.put("req_key", Base64Utils.encode(uuid));
		
		JSONObject pubObject = new JSONObject();
		pubObject.put("u_id", paramObject.getString("u_id"));
		
		reqObject.put("data", pubObject);
		reqObject.put("req_info", infoObject);
		
		// 사용자 관리용 암호화 Public Key 요청
		String receiveMsg = KafkaUtils.getPublicKey(uuid, reqObject);
		if (!"".equals(receiveMsg)) {
			JSONObject resObject = new JSONObject(receiveMsg);
			String result = resObject.getJSONObject("res_info").getString("result");
			if ("success".equals(result)) {
				String password = paramObject.getString("u_password");
				String publicKey = resObject.getJSONObject("data").getString("public_key");
				
				// 비밀번호 RSA 암호화
				String encryptedPswd = CipherUtils.encryptRSA(password, publicKey);
				
				paramObject.put("u_password", encryptedPswd);
				
				infoObject.put("req_id", paramObject.getString("u_id"));
				infoObject.put("req_name", paramObject.getString("u_name"));
				
				reqObject.put("data", paramObject);
				reqObject.put("req_info", infoObject);
				
				PrintUtils.printRequest("[BCITS-AIAS-IF-002] 사용자 등록", reqObject);
				
				// Response
				receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
			}
		}

		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-003] 사용자 정보 수정
	 * @param param
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws InterruptedException 
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/users")
	public String updateUser(HttpSession session, @RequestBody String params) throws JSONException, UnsupportedEncodingException, JsonMappingException, JsonProcessingException, InterruptedException {
		// Request
		String topic = "mod_user";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		
		paramObject.put("u_id", SessionUtils.getUserId(session));
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-003] 사용자 정보 수정", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		if (!"".equals(receiveMsg)) {
			JSONObject resObject = new JSONObject(receiveMsg);
			String result = resObject.getJSONObject("res_info").getString("result");
			if ("success".equals(result)) {
				// 세션에 저장된 이름 변경
				String userName = paramObject.getString("u_name");
				
				SessionUtils.setUserName(session, userName);
			}
		}
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-005] 특정 사용자 프로필 조회
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws InterruptedException 
	 */
	@RequestMapping(method = RequestMethod.GET, value = {"/users/", "/users/{u_id}"})
	public String getUserInfo(HttpSession session, @PathVariable(value = "u_id", required = false) String userId) throws JSONException, UnsupportedEncodingException, JsonMappingException, JsonProcessingException, InterruptedException {
		// Request
		String topic = "user_info";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject();
		
		// 본인 프로필 조회
		if (userId == null) {
			userId = SessionUtils.getUserId(session);
		}
		
		paramObject.put("u_id", userId);
		
		reqObject.put("data", paramObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-005] 특정 사용자 프로필 조회", reqObject);
		
		// Response
		String receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-006] 사용자 비밀번호 변경
	 * @param session
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/users/password")
	public String updateUserPassword(HttpSession session, @RequestBody String params) throws Exception {
		// Request
		String topic = "mod_pw";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject pubObject = new JSONObject();
		
		String userId = SessionUtils.getUserId(session);
		pubObject.put("u_id", userId);
		
		reqObject.put("data", pubObject);
		reqObject.put("req_info", SessionUtils.getRequestInfo(session, uuid));
		
		// 사용자 관리용 암호화 Public Key 요청
		String receiveMsg = KafkaUtils.getPublicKey(uuid, reqObject);
		if (!"".equals(receiveMsg)) {
			JSONObject paramObject = new JSONObject(params);
			JSONObject resObject = new JSONObject(receiveMsg);
			String result = resObject.getJSONObject("res_info").getString("result");
			if ("success".equals(result)) {
				String curPswd = paramObject.getString("current_password");
				String chaPswd = paramObject.getString("change_password");
				String publicKey = resObject.getJSONObject("data").getString("public_key");
				
				paramObject.put("u_id", userId);
				paramObject.put("current_password", CipherUtils.encryptRSA(curPswd, publicKey));
				paramObject.put("change_password", CipherUtils.encryptRSA(chaPswd, publicKey));
				
				reqObject.put("data", paramObject);
				
				PrintUtils.printRequest("[BCITS-AIAS-IF-006] 사용자 비밀번호 변경", reqObject);
				
				// Response
				receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
			}	
		}
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IT-020] 로그인
	 * @param session
	 * @param request
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users/auth")
	public String login(HttpSession session, HttpServletRequest request, @RequestBody String params) throws Exception {
		// Request
		String topic = "auth_user";
		String uuid = KeyUtils.getUUID();
		
		JSONObject reqObject = new JSONObject();
		JSONObject paramObject = new JSONObject(params);
		JSONObject infoObject = new JSONObject();
		infoObject.put("req_id", "");
		infoObject.put("req_name", "");
		infoObject.put("req_ip", request.getRemoteAddr());
		infoObject.put("req_key", Base64Utils.encode(uuid));
		
		JSONObject pubObject = new JSONObject();
		pubObject.put("u_id", paramObject.getString("u_id"));
		
		reqObject.put("data", pubObject);
		reqObject.put("req_info", infoObject);
		
		// Public Key 요청
		String receiveMsg = KafkaUtils.getAuthPublicKey(uuid, reqObject);
		if (!"".equals(receiveMsg)) {
			JSONObject resObject = new JSONObject(receiveMsg);
			String result = resObject.getJSONObject("res_info").getString("result");
			if ("success".equals(result)) {
				String password = paramObject.getString("u_password");
				String publicKey = resObject.getJSONObject("data").getString("public_key");
				
				String encryptedPswd = CipherUtils.encryptRSA(password, publicKey);
				paramObject.put("u_password", encryptedPswd);
				
				reqObject.put("data", paramObject);
				
				PrintUtils.printRequest("[BCITS-AIAS-IF-020] 로그인", reqObject);
				
				// Response
				receiveMsg = KafkaUtils.sendAndReceive(uuid, topic, reqObject.toString());
			} else {
				return receiveMsg;
			}		
		} else {
			return receiveMsg;
		}
		
		// 로그인 확인
		if (!"".equals(receiveMsg)) {
			JSONObject resObject = new JSONObject(receiveMsg);
			String result = resObject.getJSONObject("res_info").getString("result");
			if ("success".equals(result)) {
				JSONObject userObject = resObject.getJSONObject("data");
				
				// 세션에 유저 정보 입력
				SessionUtils.setUser(session, request, userObject);
			}
		}
		
		return receiveMsg;
	}
}
