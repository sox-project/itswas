package egovframework.utils;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PrintUtils {
	
	/**
	 * Request metadata 출력
	 * @param id
	 * @param object
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static void printRequest(String id, JSONObject object) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		JsonNode jsonNode = objectMapper.readTree(object.toString());
		String json = objectWriter.writeValueAsString(jsonNode);
		
		System.out.println(id + System.lineSeparator() + json);
	}
}
