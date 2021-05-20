package eu.de4a.connector.model.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.de4a.connector.model.EvaluatorAddresses;
import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.repository.EvaluatorAddressesRepository;
import eu.de4a.connector.repository.OwnerAddressesRepository;

@Component
public class AgentsLocator {
	@Autowired
	private OwnerAddressesRepository ownerAddressesRepository;
	@Autowired
    private EvaluatorAddressesRepository evaluatorAddressesRepository;

	public OwnerAddresses lookupOwnerAddress(String dataOwnerId) {
		return ownerAddressesRepository.findById(dataOwnerId.toLowerCase(Locale.ROOT)).orElse(null);		
	}
	
	public EvaluatorAddresses lookupEvaluatorAddress(String dataEvaluatorId) {
        return evaluatorAddressesRepository.findById(dataEvaluatorId.toLowerCase(Locale.ROOT)).orElse(null);        
    }
}
