package eu.de4a.connector.as4.owner;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.de4a.connector.api.controller.error.ExternalModuleError;
import eu.de4a.connector.api.controller.error.FamilyErrorType;
import eu.de4a.connector.api.controller.error.LayerError;
import eu.de4a.connector.api.controller.error.OwnerException;
import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.repository.OwnerAddressesRepository;

@Component
public class OwnerLocator {
	@Autowired
	private OwnerAddressesRepository evidenceEntityRepository;

	public OwnerAddresses lookupOwnerAddress(String dataOwnerId) throws OwnerException {
		OwnerAddresses evidence = evidenceEntityRepository.findById(dataOwnerId.toLowerCase(Locale.ROOT)).orElse(null);
		if (evidence == null) {
			throw new OwnerException().withFamily(FamilyErrorType.CONNECTION_ERROR).withLayer(LayerError.CONFIGURATION).withMessage("error.owner.not.found").
			withMessageArg(dataOwnerId).withModule(ExternalModuleError.DATA_OWNER);
		}
		return evidence;
	}
}
