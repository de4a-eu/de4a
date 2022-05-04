package eu.de4a.connector.config;

import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mapping class for the external services URLs </br>
 * Check - {@link application.yml}
 *
 */
@Component
@ConfigurationProperties(prefix = "addresses")
public class AddressesProperties {
    private Map<String, Map<String, String>> dataOwners;
    private Map<String, Map<String, String>> dataEvaluators;

    @Nullable
    public Map<String, Map<String, String>> getDataOwners (){
      return dataOwners;
    }


    public void setDataOwners (final Map<String, Map<String, String>> a){
      dataOwners = a;
    }

    public String getDataOwnerByType(final String doID,
            final String endpointType) {
        return this.dataOwners.get(doID).get(endpointType);
    }

    @Nullable
    public Map<String, Map<String, String>> getDataEvaluators (){
      return dataEvaluators;
    }

    public void setDataEvaluators (final Map<String, Map<String, String>> a){
      dataEvaluators = a;
    }

    public String getDataEvaluatorByType(final String deID,
            final String endpointType) {
        return this.dataEvaluators.get(deID).get(endpointType);
    }
}
