package eu.de4a.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileUtils {

	private static final String DE4A_PREFIX = "de4a-";

	FileUtils() {
		//empty constructor
	}

	public static File convert(MultipartFile file, File tempdir) throws IOException {
		File convFile = null;
		convFile = File.createTempFile(DE4A_PREFIX, ".xml", tempdir);
		try(FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
			return convFile;
		}
	}

	public static MultipartFile getMultipart(String label, String mimetype, byte[] data) throws IOException {
		File tempFile = null;
		tempFile = File.createTempFile(DE4A_PREFIX, null);

		DiskFileItem item = new DiskFileItem(label, mimetype, false, label, 1, tempFile.getParentFile());
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		OutputStream out = item.getOutputStream();
		org.apache.commons.io.IOUtils.copy(in, out);
		in.close();
		out.close();
		return new CommonsMultipartFile(item);
	}

	public static byte[] packageZip(File tempDir) throws IOException {
		String fileSeparator = System.getProperty("file.separator");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		byte[] bytes = new byte[2048];
		for (String fileName : tempDir.list()) {
			FileInputStream fis = new FileInputStream(tempDir.getPath() + fileSeparator + fileName);
			try (BufferedInputStream bis = new BufferedInputStream(fis)) {
				zos.putNextEntry(new ZipEntry(fileName));
				int bytesRead;
				while ((bytesRead = bis.read(bytes)) != -1) {
					zos.write(bytes, 0, bytesRead);
				}
				zos.closeEntry();
			}
			fis.close();
		}
		zos.flush();
		baos.flush();
		zos.close();
		baos.close();
		return baos.toByteArray();
	}
}
