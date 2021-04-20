package eu.de4a.connector.as4.owner;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.repository.OwnerAddressesRepository;
import eu.de4a.exception.MessageException;

@Component
public class OwnerLocator {
	@Autowired
	private OwnerAddressesRepository evidenceEntityRepository;

	public OwnerAddresses lookupOwnerAddress(String dataOwnerId) throws MessageException {
		OwnerAddresses evidence = evidenceEntityRepository.findById(dataOwnerId.toLowerCase(Locale.ROOT)).orElse(null);
		if (evidence == null) {
			throw new MessageException(String.format("Does not exists information for Owner id {}", dataOwnerId));
		}
		return evidence;
	}
}
