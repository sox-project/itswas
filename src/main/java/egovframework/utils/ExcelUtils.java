package egovframework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExcelUtils {
	private static final int COLUMN_MAX_COUNT = 15;
	
	/**
	 * 엑셀 파일을 읽어 Map<String, List<List<Object>>> 형태로 반환
	 * Map의 key는 엑셀 sheet 이름, List는 내용
	 * Map를 가지고 엑셀을 만들어 본 결과, Formula와 Cell 형식 등을 지정할 수 없음
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<List<Object>>> read(InputStream is) throws IOException {
		// 엑셀 Sheet 순서를 보장하기 위해 LinkedHashMap 사용 
		Map<String, List<List<Object>>> excelMap = new LinkedHashMap<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(is);
		
		int sheetCount = workbook.getNumberOfSheets();
		for (int i = 0; i < sheetCount; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			
			List<List<Object>> rowList = new ArrayList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				List<Object> cellList = new ArrayList<>();
				Iterator<Cell> cellIterator = row.iterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					CellType cellType = cell.getCellTypeEnum();
					
					Object cellValue = "";
					if (cellType == CellType.FORMULA) {
						cellValue = cell.getCellFormula();
					} else if (cellType == CellType.STRING) {
						cellValue = cell.getStringCellValue();
					} else if (cellType == CellType.NUMERIC) {
						cellValue = cell.getNumericCellValue();
					} else if (cellType == CellType.BOOLEAN) {
						cellValue = cell.getBooleanCellValue();
					} else if (cellType == CellType.BLANK) {
						cellValue = " ";
					} else if (cellType == CellType.ERROR) {
						cellValue = cell.getErrorCellValue();
					}
					
					cellList.add(cellValue);
				}
				
				rowList.add(cellList);
			}
			
			String sheetName = sheet.getSheetName();
			excelMap.put(sheetName, rowList);
		}
		
		workbook.close();
		
		return excelMap;
	}
	
	
	/**
	 * Cell 형태로 저장하여, Style, Formula를 사용
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<List<Cell>>> readToCell(InputStream is) throws IOException {
		// 엑셀 Sheet 순서를 보장하기 위해 LinkedHashMap 사용 
		Map<String, List<List<Cell>>> excelMap = new LinkedHashMap<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(is);
		
		int sheetCount = workbook.getNumberOfSheets();
		for (int i = 0; i < sheetCount; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			
			List<List<Cell>> rowList = new ArrayList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				List<Cell> cellList = new ArrayList<>();
				Iterator<Cell> cellIterator = row.iterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					
					cellList.add(cell);
				}
				
				rowList.add(cellList);
			}
			
			String sheetName = sheet.getSheetName();
			excelMap.put(sheetName, rowList);
		}
		
		workbook.close();
		
		return excelMap;
	}
	
	
	/**
	 * CSV 파일을 읽어 JSON으로 변환
	 * @param is
	 * @return
	 */
	public static JSONArray readCsvToJson(InputStream is) throws IOException {
		JSONArray array = new JSONArray();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		if (reader.ready()) {
			// CSV 첫째줄 삭제
			reader.readLine();
			
			while(reader.ready()) {
				String str = reader.readLine();
				String[] contents = str.split(",", COLUMN_MAX_COUNT);
				
				// TODO : CSV 양식에 따라 content index 변경해야함
				JSONObject object = new JSONObject();
				
				object.put("c_name", contents[1]);
				object.put("c_id", contents[2]);
				object.put("c_password", contents[3]);
				object.put("c_ip", contents[4]);
				object.put("c_port", contents[5]);
				object.put("c_rtsp_url", contents[6]);
				object.put("c_width", contents[7]);
				object.put("c_height", contents[8]);
				object.put("c_location", contents[9]);
				object.put("c_latitude", contents[10]);
				object.put("c_longitude", contents[11]);
				object.put("c_description", contents[12]);
				object.put("c_iw_device_id", contents[13]);
				
				array.put(object);
			}
		}
		
		return array;
	}
	
}
