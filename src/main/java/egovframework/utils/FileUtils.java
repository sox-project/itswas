package egovframework.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
	
	/**
	 * .zip 파일 압축 해제
	 * @param is
	 * @param parent
	 * @return
	 */
	public static boolean unZip(InputStream is, String parent) {
		boolean isComplete = false;
		
		ZipInputStream zis = new ZipInputStream(is, Charset.forName("EUC-KR"));
		ZipEntry zipEntry = null;
		
		FileOutputStream fos = null;
		
		try {
			while ((zipEntry = zis.getNextEntry()) != null) {
				String fileName = zipEntry.getName();
				File file = new File(parent + fileName);
				
				if (zipEntry.isDirectory()) {
					file.mkdir();
				} else {
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[2048];
					int size = 0;
					
					while ((size = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, size);
					}
				}
			}
			
			isComplete = true;
		} catch (IOException e) {
			e.printStackTrace();
			
			isComplete = false;
		} finally {
			try { 
				if (zis != null) {
					zis.close();
				}
				
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return isComplete;
	}
}
