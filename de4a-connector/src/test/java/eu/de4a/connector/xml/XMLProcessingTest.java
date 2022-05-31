package eu.de4a.connector.xml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import eu.de4a.connector.utils.DOMUtils;
import eu.de4a.iem.cev.de4a.t41.DE4AT41Marshaller;
import eu.de4a.iem.jaxb.t41.uc1.hed.v2021_04_13.HigherEducationDiplomaType;

@RunWith (SpringRunner.class)
public class XMLProcessingTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (XMLProcessingTest.class);

  @Test
  public void testRegRepWrapMessage () throws IOException
  {
    final InputStream isReq = this.getClass ().getClassLoader ().getResourceAsStream ("xml/request-usi.xml");

    final Document doc = DOMUtils.byteToDocument (isReq.readAllBytes ());
    assertNotNull (doc);

    final String newDoc = DOMUtils.documentToString (doc);
    LOGGER.info (newDoc);
    assertNotNull (newDoc);
  }

  @Test
  public void testMarshaller ()
  {
    final String evidencia = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                             "<HigherEducationDiploma xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"urn:eu-de4a:xsd:CanonicalEvidenceType::HigherEducationEvidence:v1.0\" id=\"urn:credential:2008171279\">\n" +
                             "      <identifier spatialID=\"PT\" xmlns=\"http://data.europa.eu/europass/model/credentials#\">2008029508</identifier>\n" +
                             "      <title xmlns=\"http://data.europa.eu/europass/model/credentials#\">\n" +
                             "        <text lang=\"pt\" content-type=\"text/html\">MSc Program in Computer Science and Engineering</text>\n" +
                             "      </title>\n" +
                             "      <degree lang=\"pt\" content-type=\"text/html\">Master in Computer Science and Engineering</degree>\n" +
                             "      <country>http://publications.europa.eu/resource/authority/country/PRT</country>\n" +
                             "      <institutionName lang=\"pt\" content-type=\"text/html\">Instituto Superior TÃ©cnico</institutionName>\n" +
                             "      <studyProgramme lang=\"pt\" content-type=\"text/html\">MSc Program in Computer Science and Engineering</studyProgramme>\n" +
                             "      <mainFieldOfStudy uri=\"http://data.europa.eu/snb/isced-f/061\" />\n" +
                             "      <modeOfStudy>http://data.europa.eu/europass/learningScheduleType/fullTime</modeOfStudy>\n" +
                             "      <durationOfEducation>P2Y</durationOfEducation>\n" +
                             "      <scope>29</scope>\n" +
                             "      <dateOfIssue>2021-10-29</dateOfIssue>\n" +
                             "      <placeOfIssue>\n" +
                             "        <name xmlns=\"http://data.europa.eu/europass/model/credentials#\">\n" +
                             "          <text lang=\"pt\" content-type=\"text/html\">Lisboa - Portugal</text>\n" +
                             "        </name>\n" +
                             "      </placeOfIssue>\n" +
                             "      <holderOfAchievement id=\"urn:person:pt:15000029\">\n" +
                             "        <nationalId spatialID=\"http://publications.europa.eu/resource/authority/country/PRT\" xmlns=\"http://data.europa.eu/europass/model/credentials#\">15000029</nationalId>\n" +
                             "        <givenNames xmlns=\"http://data.europa.eu/europass/model/credentials#\">\n" +
                             "          <text lang=\"pt\" content-type=\"text/html\">Manuel</text>\n" +
                             "        </givenNames>\n" +
                             "        <familyName xmlns=\"http://data.europa.eu/europass/model/credentials#\">\n" +
                             "          <text lang=\"pt\" content-type=\"text/html\">Silva</text>\n" +
                             "        </familyName>\n" +
                             "        <dateOfBirth xmlns=\"http://data.europa.eu/europass/model/credentials#\">1950-01-24</dateOfBirth>\n" +
                             "      </holderOfAchievement>\n" +
                             "    </HigherEducationDiploma>";
    final Document doc = DOMUtils.stringToDocument (evidencia);
    final DE4AT41Marshaller <HigherEducationDiplomaType> marshaller = DE4AT41Marshaller.higherEducationDiploma ();
    final HigherEducationDiplomaType diplomaType = marshaller.read (doc);
    assertNotNull (diplomaType);
  }

  @Test
  public void testUjiResponse () throws ParserConfigurationException
  {
    final String XPATH_TITLE_NAME_FILTER = "//*[local-name()='HigherEducationDiploma'][./*[local-name()='title']/*[local-name()='text'='%s']]";
    final String xml = "<TitleEvidenceResponse xmlns:p2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:udt=\"urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2\" xmlns:clm54217=\"urn:un:unece:uncefact:codelist:specification:54217:2001\" xmlns:edci=\"http://data.europa.eu/europass/model/credentials#\" xmlns:cred=\"http://data.europa.eu/europass/model/credentials/w3c#\" xmlns:p3=\"urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2\" xmlns:sa=\"urn:eu-de4a:xsd:CanonicalEvidenceType::HigherEducationEvidence:v1.0\" xmlns:clm66411=\"urn:un:unece:uncefact:codelist:specification:66411:2001\">\n" +
                       "<titles>\n" +
                       "<title>\n" +
                       "<codigoRUCTTitulacion>10110000000</codigoRUCTTitulacion>\n" +
                       "<codigoRUCTUniversidad>040</codigoRUCTUniversidad>\n" +
                       "<sa:HigherEducationDiploma id=\"urn:credential:2008171279\" xsi:schemaLocation=\"urn:eu-de4a:xsd:CanonicalEvidenceType::HigherEducationEvidence:v1.0 SA-UC1-20-04-2021.xsd\">\n" +
                       "<edci:identifier spatialID=\"ES\">2008171279</edci:identifier>\n" +
                       "<edci:title>\n" +
                       "<edci:text lang=\"en\" content-type=\"text/plain\">Degree in Computer Science Engineering (2001 Programme of Study)</edci:text>\n" +
                       "</edci:title>\n" +
                       "<sa:degree lang=\"en\" content-type=\"text/plain\">Degree in Computer Science Engineering (2001 Programme of Study)</sa:degree>\n" +
                       "<sa:country>http://publications.europa.eu/resource/authority/country/ESP</sa:country>\n" +
                       "<sa:institutionName lang=\"en\" content-type=\"text/plain\">Jaume I University</sa:institutionName>\n" +
                       "<sa:studyProgramme lang=\"en\" content-type=\"text/plain\">Degree in Computer Science Engineering (2001 Programme of Study)</sa:studyProgramme>\n" +
                       "<sa:mainFieldOfStudy uri=\"http://data.europa.eu/snb/isced-f/0610\"/>\n" +
                       "<sa:modeOfStudy>http://data.europa.eu/europass/learningScheduleType/fullTime</sa:modeOfStudy>\n" +
                       "<sa:durationOfEducation>P5Y</sa:durationOfEducation>\n" +
                       "<sa:scope schemeID=\"http://data.europa.eu/europass/educationalCreditPointSystem/ects\">352</sa:scope>\n" +
                       "<sa:dateOfIssue>2008-07-22</sa:dateOfIssue>\n" +
                       "<sa:placeOfIssue>\n" +
                       "<edci:name>\n" +
                       "<edci:text lang=\"en\" content-type=\"text/plain\">Castellon</edci:text>\n" +
                       "</edci:name>\n" +
                       "</sa:placeOfIssue>\n" +
                       "<sa:holderOfAchievement id=\"urn:person:es:99999142H\">\n" +
                       "<edci:nationalId spatialID=\"http://publications.europa.eu/resource/authority/country/ESP\">99999142H</edci:nationalId>\n" +
                       "<edci:givenNames>\n" +
                       "<edci:text lang=\"en\" content-type=\"text/plain\">Usuario</edci:text>\n" +
                       "</edci:givenNames>\n" +
                       "<edci:familyName>\n" +
                       "<edci:text lang=\"en\" content-type=\"text/plain\">Prueba</edci:text>\n" +
                       "</edci:familyName>\n" +
                       "<edci:dateOfBirth>1970-01-01</edci:dateOfBirth>\n" +
                       "</sa:holderOfAchievement>\n" +
                       "</sa:HigherEducationDiploma>\n" +
                       "</title>\n" +
                       "</titles>\n" +
                       "</TitleEvidenceResponse>";

    final Document docUji = DOMUtils.stringToDocument (xml);

    final Element title = (Element) DOMUtils.getNodeFromXpath (String.format (XPATH_TITLE_NAME_FILTER,
                                                                              "Degree in Computer Science Engineering (2001 Programme of Study)"),
                                                               docUji.getDocumentElement ());
    LOGGER.info (DOMUtils.nodeToString (title, false));

    final Document docTitle = DOMUtils.newDocumentFromNode (title, "HigherEducationDiploma");
    LOGGER.info (DOMUtils.documentToString (docTitle));

    final DE4AT41Marshaller <HigherEducationDiplomaType> marshaller = DE4AT41Marshaller.higherEducationDiploma ();
    final HigherEducationDiplomaType diplomaType1 = marshaller.read (title);
    final HigherEducationDiplomaType diplomaType = marshaller.read (docTitle);

    assertTrue (diplomaType != null || diplomaType1 != null);
  }
}
