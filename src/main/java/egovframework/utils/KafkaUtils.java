package egovframework.utils;


import java.util.Arrays;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtils {
	private static final String REQUEST_TOPIC_PATTERN = "rq_";
	private static final String RESPONSE_TOPIC_PATTERN = "rp_";
	
	private static KafkaTemplate<String, String> kafkaTemplate;
	private static Consumer<String, String> consumer;
	
	@Autowired
	public KafkaUtils(KafkaTemplate<String, String> kafkaTemplate, Consumer<String, String> consumer) {
		KafkaUtils.kafkaTemplate = kafkaTemplate;
		KafkaUtils.consumer = consumer;
	}
	
	
	/**
	 * pubTopic으로 Message를 보내고, subTopic에서 Message를 받아옴
	 * @param topicName		Kafka Topic 이름
	 * @param sendMessage
	 * @return
	 */
	public static String sendAndReceive(String topicName, String sendMessage) {
		String result = "";
		
		String pubTopic = REQUEST_TOPIC_PATTERN + topicName;
		String subTopic = RESPONSE_TOPIC_PATTERN + topicName;
		
		// pubTopic으로 Message를 보냄
		kafkaTemplate.send(pubTopic, sendMessage);
		
		// subTopic을 구독
		consumer.subscribe(Arrays.asList(subTopic));
		
		// Timeout를 대신하여 count 사용
		int count = 0;
		while ("".equals(result) && count < 10) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			
			for (ConsumerRecord<String, String> record : records) {
				result = record.value();
			}
			
			count++;
		}
		
		return result;
	}
	
}
