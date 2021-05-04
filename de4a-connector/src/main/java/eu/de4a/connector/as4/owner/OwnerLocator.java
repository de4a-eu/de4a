package eu.de4a.connector.as4.owner;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import eu.de4a.connector.error.exceptions.OwnerException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.model.MessageKeys;
import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.repository.OwnerAddressesRepository;
import eu.de4a.connector.service.spring.MessageUtils;

@Component
public class OwnerLocator {
	@Autowired
	private OwnerAddressesRepository evidenceEntityRepository;

	public OwnerAddresses lookupOwnerAddress(String dataOwnerId) throws OwnerException {
		OwnerAddresses evidence = evidenceEntityRepository.findById(dataOwnerId.toLowerCase(Locale.ROOT)).orElse(null);
		if (evidence == null) {
			throw new OwnerException().withFamily(FamilyErrorType.SAVING_DATA_ERROR)
			.withLayer(LayerError.CONFIGURATION)
			.withModule(ExternalModuleError.CONNECTOR_DT)
			.withMessageArg(new MessageUtils(MessageKeys.ERROR_OWNER_NOT_FOUND, new Object[] {dataOwnerId}))
			.withHttpStatus(HttpStatus.OK);
		}
		return evidence;
	}
}
