package egovframework.utils;


import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class KafkaUtils {
	private static final String REQUEST_TOPIC_PATTERN = "rq_";
	private static final String RESPONSE_TOPIC_PATTERN = "rp_.*";
	
	private static final int TIMEOUT_COUNT_THRESOLD = 10;
	private static final int REQUEST_INTERVAL = 2000;
	
	private static Map<String, String> receiveMap = Collections.synchronizedMap(new HashMap<>());
	
	private static KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	public KafkaUtils(KafkaTemplate<String, String> kafkaTemplate) {
		KafkaUtils.kafkaTemplate = kafkaTemplate;
	}
	
	
	/**
	 * pubTopic으로 Message를 보내고, subTopic에서 Message를 받아옴
	 * @param topicName		Kafka Topic 이름
	 * @param sendMessage
	 * @return
	 * @throws InterruptedException 
	 */
	public static String sendAndReceive(String uuid, String topicName, String sendMessage) throws InterruptedException {
		// topic으로 Message를 보냄
		kafkaTemplate.send(REQUEST_TOPIC_PATTERN + topicName, sendMessage);
		
		// Message를 가져옴
		String result = "";
		int count = 0;
		
		while ("".equals(result) && count < TIMEOUT_COUNT_THRESOLD) {
			if (receiveMap.containsKey(uuid)) {
				result = receiveMap.get(uuid);
				
				receiveMap.remove(uuid);
			} else {
				Thread.sleep(REQUEST_INTERVAL);
				count++;
			}
		}
		
		return result;
	}
	
	
	/**
	 * 메세지를 받음
	 * @param consumerRecord
	 * @throws UnsupportedEncodingException
	 */
	@KafkaListener(topicPattern = RESPONSE_TOPIC_PATTERN)
	public void receive(ConsumerRecord<String, String> consumerRecord) throws UnsupportedEncodingException {
		String value = consumerRecord.value();
		
		JSONObject object = new JSONObject(value);
		String encodeUuid = object.getJSONObject("res_info").getString("res_key");
		
		receiveMap.put(Base64Utils.decodeToString(encodeUuid), value);
	}
	
	
	/**
	 * [BCITS-AIAS-IF-008] 사용자 관리용 암호화 Public Key 요청
	 * @param uuid
	 * @param reqObject
	 * @return
	 * @throws InterruptedException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static String getPublicKey(String uuid, JSONObject reqObject) throws InterruptedException, JsonMappingException, JsonProcessingException {
		String topic = "user_public_key";
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-008] 사용자 관리용 암호화 Public Key 요청", reqObject);
		
		// Response
		String receiveMsg = sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
	
	
	/**
	 * [BCITS-AIAS-IF-019] Public Key 요청
	 * @param uuid
	 * @param reqObject
	 * @return
	 * @throws InterruptedException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static String getAuthPublicKey(String uuid, JSONObject reqObject) throws InterruptedException, JsonMappingException, JsonProcessingException {
		String topic = "auth_public_key";
		
		PrintUtils.printRequest("[BCITS-AIAS-IF-019] Public Key 요청", reqObject);
		
		// Response
		String receiveMsg = sendAndReceive(uuid, topic, reqObject.toString());
		
		return receiveMsg;
	}
}
