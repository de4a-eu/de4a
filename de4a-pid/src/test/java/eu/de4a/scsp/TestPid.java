package eu.de4a.scsp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.de4a.scsp.manager.ScspGateway;
import eu.de4a.scsp.spring.ConfPid;

@SpringBootTest(classes={ConfPid.class}) 
@RunWith(SpringRunner.class)   
public class TestPid {
	@Autowired 
	private ScspGateway scspGateway; 
	@Test 
	public void testScspBirth() {  
//		String certificado=EvidenceMapper.BIRTH_CERTIFICATE; 
//		try {
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	        DocumentBuilder dBuilder = factory.newDocumentBuilder();
//	        Document doc = dBuilder.parse(this.getClass().getClassLoader()  .getResourceAsStream( "edm/request.xml")); 
////	        Element response=scspGateway.sendEvidenceRequest(doc.getDocumentElement(), certificado,null); 
////	        Assert.assertNotNull(response );
//		} catch ( Exception | MessageException e) { 
//			e.printStackTrace();
//			Assert.assertNotNull(null );
//		}
//		
	}
	 
}
