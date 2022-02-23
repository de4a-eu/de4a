package eu.de4a.connector.service.spring;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "addresses")
public class AddressesProperties {
    private Map<String, String> dataOwners;
    private Map<String, String> dataEvaluators;
    
    
    public Map<String, String> getDataOwners() {
        return dataOwners;
    }
    public void setDataOwners(Map<String, String> dataOwners) {
        this.dataOwners = dataOwners;
    }
    public Map<String, String> getDataEvaluators() {
        return dataEvaluators;
    }
    public void setDataEvaluators(Map<String, String> dataEvaluators) {
        this.dataEvaluators = dataEvaluators;
    }
    
}
