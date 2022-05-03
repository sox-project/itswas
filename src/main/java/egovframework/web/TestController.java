package egovframework.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.service.TestService;
import egovframework.utils.DateTimeUtils;
import egovframework.utils.ExcelUtils;
import egovframework.utils.FileUtils;
import egovframework.utils.KafkaUtils;
import egovframework.utils.WebSocket;

@EnableKafka
@RestController
public class TestController {
	
	@Resource(name = "TestService")
	private TestService userService;
	
	@Resource(name = "fileProperties")
	private EgovPropertyService fileProperties;
	
	/**
	 * 로그인
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/test/login")
	public Map<String, Object> login(@RequestBody Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, Object> userMap = userService.selectUser(paramMap);
		
		if (userMap != null) {
			resultMap.put("result", "OK");
			resultMap.put("msg", "로그인 성공");
		} else {
			resultMap.put("result", "ERROR");
			resultMap.put("msg", "다시 시도해주세요.");
		}
		
		return resultMap;
	}
	
	
	/**
	 * 파일 업로드
	 * @param uploadFile
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/test/file")
	public Map<String, Object> upload(@RequestParam("file") MultipartFile uploadFile) throws IOException {
		Map<String, Object> resultMap = new HashMap<>();
		
		String filePath = fileProperties.getString("filePath");
		String fileName = uploadFile.getOriginalFilename();
		String extension = fileName.substring(fileName.lastIndexOf("."));
		
		// 업로드 파일 이름은 fileName_yyyyMMdd_HHmmss.extension로 저장
		fileName = fileName + "_" + DateTimeUtils.getDateTimeNow(DateTimeUtils.DATETIME_FILE_PATTERN) + extension;
		
		File directory = new File(filePath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		File file = new File(filePath + fileName);
		uploadFile.transferTo(file);
		
		if (file.exists()) {
			resultMap.put("result", "OK");
			resultMap.put("msg", "파일이 업로드 되었습니다.");
		} else {
			resultMap.put("result", "ERROR");
			resultMap.put("msg", "다시 시도해주세요.");
		}
		
		return resultMap;
	}
	
	
	/**
	 * .zip 파일 압축 해제
	 * @param uploadFile
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/test/file/unzip")
	public Map<String, Object> unzip(@RequestParam("file") MultipartFile uploadFile) throws IOException {
		Map<String, Object> resultMap = new HashMap<>();
		
		String fileName = uploadFile.getOriginalFilename();
		
		int index = fileName.lastIndexOf(".");
		
		String dirName = fileName.substring(0, index);
		String extension = fileName.substring(index);
		
		if (".zip".equals(extension)) {
			String filePath = fileProperties.getString("filePath") + dirName + "/";
			
			File file = new File(filePath);
			file.mkdirs();
			
			boolean isCompleted = FileUtils.unZip(uploadFile.getInputStream(), filePath);
			
			if (isCompleted) {
				resultMap.put("result", "OK");
				resultMap.put("msg", "압축이 해제되었습니다.");
			} else {
				resultMap.put("result", "ERROR");
				resultMap.put("msg", "다시 시도해주세요.");
			}
		} else {
			resultMap.put("result", "ERROR");
			resultMap.put("msg", "zip 파일을 업로드 해주세요.");
		}
		
		return resultMap;
	}
	
	
	/**
	 * 엑셀 파일 파싱
	 * @param uploadFile
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/test/file/excel")
	public Map<String, Object> readExcel(@RequestParam("file") MultipartFile uploadFile) throws IOException {
		Map<String, Object> resultMap = new HashMap<>();
		
		String fileName = uploadFile.getOriginalFilename();
		String extension = fileName.substring(fileName.lastIndexOf("."));
		
		// TODO : xls, xlsx 구분해서 Workbook을 사용해야함(HSSF, XSSF)
		// 현재 XSSFWorkBook만 가능
		if (".xls".equals(extension) || ".xlsx".equals(extension)) {
			Map<String, List<List<Object>>> excelMap = new LinkedHashMap<>();
			
			excelMap = ExcelUtils.read(uploadFile.getInputStream());
			
			if (!excelMap.isEmpty()) {
				resultMap.put("result", "OK");
				resultMap.put("msg", "완료되었습니다.");
			} else {
				resultMap.put("result", "ERROR");
				resultMap.put("msg", "다시 시도해주세요.");
			}
		} else {
			resultMap.put("result", "ERROR");
			resultMap.put("msg", "엑셀 파일을 업로드 해주세요.");
		}
		
		return resultMap;
	}
	
	
	/**
	 * WebSocket에 연결되어 있는 Session에게 Message를 보냄
	 * @param paramMap
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/test/socket")
	public void sendWebsocket(@RequestBody Map<String, String> paramMap) throws IOException {
		String message = paramMap.getOrDefault("message", "");
		
		for (Session session : WebSocket.sessionList) {
			session.getBasicRemote().sendText(message);
		}
	}
	
	
	/**
	 * topic으로 message를 보내고 받아옴
	 * @param paramMap
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/test/kafka")
	public Map<String, Object> sendKafka(@RequestBody Map<String, String> paramMap) throws InterruptedException, ExecutionException {
		Map<String, Object> resultMap = new HashMap<>();
		
		String pubTopic = "test_rq_user";
		String subTopic = "test_rp_user";
		String message = paramMap.getOrDefault("message", "");
		
		String result = KafkaUtils.sendAndReceive(pubTopic, message, subTopic);
		
		if (!"".equals(result)) {
			resultMap.put("result", "OK");
			resultMap.put("msg", result);
		} else {
			resultMap.put("result", "ERROR");
			resultMap.put("msg", "다시 시도해주세요.");
		}

		return resultMap;
	}
	
	
	/**
	 * topic(rp_user)에서 메세지를 가져옴
	 */
	@KafkaListener(topics = "rp_user", groupId = "test")
	public void receiveKafka(ConsumerRecord<String, String> record) {
		System.out.println("record : " + record);
	}
	
}
