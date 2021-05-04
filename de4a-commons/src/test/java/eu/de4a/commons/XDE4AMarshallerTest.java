package eu.de4a.commons;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import javax.annotation.Nonnull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.helger.commons.wrapper.Wrapper;
import com.helger.jaxb.GenericJAXBMarshaller;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;


public final class XDE4AMarshallerTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (XDE4AMarshallerTest.class);
  private static final String BASE_PATH = "src/test/resources/";

  @SuppressWarnings ("unused")
  private static <T> void _receiveViaHttp (@Nonnull final GenericJAXBMarshaller <T> aMarshaller, @Nonnull final File aFile) throws Exception
  {
    final Wrapper <Exception> aExWrapper = new Wrapper <> ();
    aMarshaller.readExceptionCallbacks ().removeAll ();
    aMarshaller.readExceptionCallbacks ().add (ex -> aExWrapper.set (ex));
    aMarshaller.readExceptionCallbacks ().add (ex -> LOGGER.error ("Failed to parse XML", ex));

    final T aRead = aMarshaller.read (aFile);
    if (aRead == null)
    {
      if (aExWrapper.isSet ())
        throw aExWrapper.get ();
      throw new Exception ("HTTP 400");
    }
  }

  private static <T> void _testReadWrite (@Nonnull final GenericJAXBMarshaller <T> aMarshaller, @Nonnull final File aFile)
  {
    assertTrue ("Test file does not exists " + aFile.getAbsolutePath (), aFile.exists ());

    if (false)
    {
      aMarshaller.readExceptionCallbacks ().set (ex -> LOGGER.error ("Read error", ex));
      aMarshaller.writeExceptionCallbacks ().set (ex -> LOGGER.error ("Write error", ex));
    }

    final T aRead = aMarshaller.read (aFile);
    assertNotNull ("Failed to read " + aFile.getAbsolutePath (), aRead);
    final String strRead = aMarshaller.getAsString(aRead);
    assertNotNull("Get as string getting null", strRead);
    final T bRead = aMarshaller.read(strRead);
    assertNotNull("read from string getting null", bRead);
    final byte [] aBytes = aMarshaller.getAsBytes (aRead);
    assertNotNull ("Failed to re-write " + aFile.getAbsolutePath (), aBytes);

    if (false)
    {
      aMarshaller.setFormattedOutput (true);
      LOGGER.info (aMarshaller.getAsString (aRead));
    }
  }

  @Test
  public void testDE_USI ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.deUsiRequestMarshaller (IDE4ACanonicalEvidenceType.NONE),
                    new File (BASE_PATH + "xde4a/DE1-USI-request.xml"));
    _testReadWrite (XDE4AMarshaller.deUsiRequestMarshaller (EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
                    new File (BASE_PATH + "xde4a/t4.2/0.4/DE1-USI-request-T42.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.deUsiResponseMarshaller (), new File (BASE_PATH + "xde4a/DE1-USI-response.xml"));
  }

  @Test
  public void testDE_USI_1 ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.deUsiRequestMarshaller (IDE4ACanonicalEvidenceType.NONE),
                    new File (BASE_PATH + "xde4a/RequestForwardEvidence.xml"));
  }

  @Test
  public void testDO_IM ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.doImRequestMarshaller (), new File (BASE_PATH + "xde4a/DO1-IM-request.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.doImResponseMarshaller (IDE4ACanonicalEvidenceType.NONE),
                    new File (BASE_PATH + "xde4a/DO1-IM-response.xml"));
    _testReadWrite (XDE4AMarshaller.doImResponseMarshaller (EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
                    new File (BASE_PATH + "xde4a/t4.2/0.4/DO1-IM-response-T42.xml"));
  }

  @Test
  public void testDO_USI ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.doUsiRequestMarshaller (), new File (BASE_PATH + "xde4a/DO1-USI-request.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.doUsiResponseMarshaller (), new File (BASE_PATH + "xde4a/DO1-USI-response.xml"));
  }

  @Test
  public void testDR_IM ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.drImRequestMarshaller (), new File (BASE_PATH + "xde4a/DR1-IM-request.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.drImResponseMarshaller (IDE4ACanonicalEvidenceType.NONE),
                    new File (BASE_PATH + "xde4a/DR1-IM-response.xml"));
    _testReadWrite (XDE4AMarshaller.drImResponseMarshaller (EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
                    new File (BASE_PATH + "xde4a/t4.2/0.4/DR1-IM-response-T42.xml"));
  }

  @Test
  public void testDR_USI ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.drUsiRequestMarshaller (), new File (BASE_PATH + "xde4a/DR1-USI-request.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.drUsiResponseMarshaller (), new File (BASE_PATH + "xde4a/DR1-USI-response.xml"));
  }

  @Test
  public void testDT_USI ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.dtUsiRequestMarshaller (IDE4ACanonicalEvidenceType.NONE),
                    new File (BASE_PATH + "xde4a/DT1-USI-request.xml"));
    _testReadWrite (XDE4AMarshaller.dtUsiRequestMarshaller (EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
                    new File (BASE_PATH + "xde4a/t4.2/0.4/DT1-USI-request-T42.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.dtUsiResponseMarshaller (), new File (BASE_PATH + "xde4a/DT1-USI-response.xml"));
  }

  @Test
  public void testIDK_LookupRoutingInformation ()
  {
    // Request
    _testReadWrite (XDE4AMarshaller.idkRequestLookupRoutingInformationMarshaller (),
                    new File (BASE_PATH + "xde4a/DR-DT1-IDK-request-routing.xml"));

    // Response
    _testReadWrite (XDE4AMarshaller.idkResponseLookupRoutingInformationMarshaller (),
                    new File (BASE_PATH + "xde4a/DR-DT1-IDK-response-routing.xml"));
  }
}
