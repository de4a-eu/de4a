package eu.de4a.commons;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;

@RunWith(JUnit4.class)
public class TestFileUtils {
	private static final String BASE_PATH = "src/test/resources/";

	@Test
	public void test1() {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;

			docBuilder = dbfac.newDocumentBuilder();

			Document doc = docBuilder.parse(BASE_PATH + "ResponseExtractEvidence.xml");
			MultipartFile mpFile = FileUtils.getMultipart(DE4AConstants.TAG_EXTRACT_EVIDENCE_RESPONSE,
					MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(doc.getOwnerDocument()));
			assertNotNull(mpFile);

			List<File> files = new ArrayList<>(1);
			File tempdir = Files.createTempDirectory("de4a-temp").toFile();
			files.add(FileUtils.convert(mpFile, tempdir));
			byte[] encodedFile = FileUtils.packageZip(tempdir);
			assertNotNull(encodedFile);
		} catch (ParserConfigurationException | SAXException | IOException | MessageException e) {
			e.printStackTrace();
		}
	}
}
