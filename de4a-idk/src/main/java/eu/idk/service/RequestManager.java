package eu.idk.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import eu.de4a.iem.jaxb.common.types.AtuLevelType;
import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.de4a.iem.jaxb.common.types.ProvisionItemType;
import eu.de4a.iem.jaxb.common.types.ProvisionItemsType;
import eu.de4a.iem.jaxb.common.types.SourceType;
import eu.idk.model.Source;

@Component
public class RequestManager {

	public void extractSourceInfo(List<Source> sources, AvailableSourcesType availableSources, String dataOwnerId) {
		sources.stream().forEach(source -> {
			SourceType sourceType = new SourceType();
			sourceType.setAtuLevel(AtuLevelType.fromValue(source.getAtuLevel()));
			sourceType.setCountryCode(source.getCountryCode());
			sourceType.setNumProvisions(source.getNumProvisions());
			sourceType.setOrganisation(source.getOrganisation());
			ProvisionItemsType provisionsItem = new ProvisionItemsType();
			provisionsItem.setProvisionItem(new ArrayList<>());
			extractProvisionItems(source, provisionsItem, dataOwnerId);
			sourceType.setProvisionItems(provisionsItem);
			if(!provisionsItem.getProvisionItem().isEmpty()) {
				availableSources.addSource(sourceType);
			}
		});
	}

	private void extractProvisionItems(Source source, ProvisionItemsType provisionItems, String dataOwnerId) {
		source.getProvisionItems().stream().forEach(x -> {
			if (ObjectUtils.isEmpty(dataOwnerId)
					|| !ObjectUtils.isEmpty(dataOwnerId) && dataOwnerId.equals(x.getDataOwnerId())) {
				ProvisionItemType provisionItem = new ProvisionItemType();
				provisionItem.setAtuCode(x.getAtuCode());
				provisionItem.setAtuLatinName(x.getAtuLatinName());
				provisionItem.setDataOwnerId(x.getDataOwnerId());
				provisionItem.setDataOwnerPrefLabel(x.getDataOwnerPrefLabel());

				provisionItems.addProvisionItem(provisionItem);
			}
		});
	}
}
