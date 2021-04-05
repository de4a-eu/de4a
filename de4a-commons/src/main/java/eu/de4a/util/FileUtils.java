package eu.de4a.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.Document;

import eu.de4a.exception.MessageException;
import eu.toop.connector.api.rest.TCPayload;

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
	
	public static byte[] empaquetarZip(File tempDir) throws IOException {
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
	
	public static byte[] buildResponse(InputStream inputStream, String xPathNationalResp) throws MessageException, IOException {
		List<TCPayload> payloads = new ArrayList<TCPayload>();
		File temp = Files.createTempFile(DE4A_PREFIX, null).toFile();
		IOUtils.copy(inputStream, new FileOutputStream(temp));
		try(ZipFile zip = new ZipFile(temp)) {
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
	
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				byte[] data = zip.getInputStream(entry).readAllBytes();
				TCPayload payload = new TCPayload();
				String name = getName(data, entry.getName(), xPathNationalResp);
				payload.setContentID(name);
				payload.setValue(data);
				payloads.add(payload);
			}
			zip.close();
			TCPayload canonicalPayload = payloads.stream()
					.filter(p -> p.getContentID().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null);
			if (canonicalPayload == null) {
				throw new MessageException("Not exists payload with tag name:" + DE4AConstants.TAG_EVIDENCE_RESPONSE);
			}
			return canonicalPayload.getValue();
		}
	}

	public static String getName(byte[] data, String name, String xPathResp) {
		try {
			Document doc = DOMUtils.byteToDocument(data);
			String value = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EXTRACT_EVIDENCE_RESPONSE, doc.getDocumentElement());
			if (value != null && !value.isEmpty())
				return DE4AConstants.TAG_EVIDENCE_RESPONSE;
			value = DOMUtils.getValueFromXpath(xPathResp, doc.getDocumentElement());
			if (value != null && !value.isEmpty())
				return DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE;
		} catch (MessageException e) {
			
		}
		return name;
	}

}
