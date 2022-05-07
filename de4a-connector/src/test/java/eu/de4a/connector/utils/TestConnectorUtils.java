package eu.de4a.connector.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestConnectorUtils {

    @Test
    public void testURIBuilder() {
        
        String baseEndpoint1 = "http://somehost.com/";
        String baseEndpoint2 = "https://somehost.com/path";
        String baseEndpoint3 = "https://somehost.com/path2/";
        String baseEndpoint4 = "https://somehost.com/path2/resource?param=1";
        
        //No paths or variables
        URIBuilder uriBuilder = APIRestUtils.buildURI(baseEndpoint1, new String[] {}, null, null);
        assertEquals(baseEndpoint1, uriBuilder.toString());
        
        uriBuilder = APIRestUtils.buildURI(baseEndpoint2, new String[] {}, null, null);
        assertEquals(baseEndpoint2, uriBuilder.toString());
        
        uriBuilder = APIRestUtils.buildURI(baseEndpoint3, new String[] {}, null, null);
        assertEquals(baseEndpoint3, uriBuilder.toString());
        
        uriBuilder = APIRestUtils.buildURI(baseEndpoint4, new String[] {}, null, null);
        assertEquals(baseEndpoint4, uriBuilder.toString());
        
        //Including path and/or variables
        uriBuilder = APIRestUtils.buildURI(baseEndpoint1, new String[] {"newPath"}, null, null);
        assertEquals(baseEndpoint1 + "newPath", uriBuilder.toString());
        
        uriBuilder = APIRestUtils.buildURI(baseEndpoint1, new String[] {"newPath", "anotherPath"}, null, null);
        assertEquals(baseEndpoint1 + "newPath/anotherPath", uriBuilder.toString());
        
        uriBuilder = APIRestUtils.buildURI(baseEndpoint2, new String[] {"newPath"}, new String[] {"var1"}, new String[] {"value1"});
        assertEquals(baseEndpoint2 + "/newPath?var1=value1", uriBuilder.toString());
        
    }
    
    @Test
    public void TestMessageSources() {
        String message = MessageUtils.format("error.as4.communications", new Object[]{"SMP", "Data not found"});
        
        assertTrue(message.contains("Data not found"));
    }
    
}
