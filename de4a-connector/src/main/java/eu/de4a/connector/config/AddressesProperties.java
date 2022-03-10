package eu.de4a.connector.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Mapping class for the external services URLs </br>
 * Check - {@link application.yml}
 *
 */
@Component
@ConfigurationProperties(prefix = "addresses")
@Getter
@Setter
public class AddressesProperties {
    private Map<String, Map<String, String>> dataOwners;
    private Map<String, Map<String, String>> dataEvaluators;
    
    public String getDataOwnerByType(final String doID, 
            final String endpointType) {
        return this.dataOwners.get(doID).get(endpointType);
    }
    
    public String getDataEvaluatorByType(final String deID, 
            final String endpointType) {
        return this.dataEvaluators.get(deID).get(endpointType);
    }
}
