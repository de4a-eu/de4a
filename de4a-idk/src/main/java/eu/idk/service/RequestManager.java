package eu.idk.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import eu.de4a.iem.jaxb.common.types.AtuLevelType;
import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.de4a.iem.jaxb.common.types.ParamType;
import eu.de4a.iem.jaxb.common.types.ParamsSetType;
import eu.de4a.iem.jaxb.common.types.ParamsType;
import eu.de4a.iem.jaxb.common.types.ProvisionItemType;
import eu.de4a.iem.jaxb.common.types.ProvisionType;
import eu.de4a.iem.jaxb.common.types.ProvisionTypeType;
import eu.de4a.iem.jaxb.common.types.ProvisionsItemType;
import eu.de4a.iem.jaxb.common.types.SourceType;
import eu.idk.model.ProvisionItem;
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
			ProvisionsItemType provisionsItem = new ProvisionsItemType();
			provisionsItem.setProvisionItem(new ArrayList<>());
			extractProvisionItems(source, provisionsItem, dataOwnerId);
			sourceType.setProvisionsItem(provisionsItem);
			availableSources.addSource(sourceType);
		});
	}

	private void extractProvisionItems(Source source, ProvisionsItemType provisionsItem, String dataOwnerId) {
		source.getProvisionItems().stream().forEach(x -> {
			if (StringUtils.isEmpty(dataOwnerId)
					|| !StringUtils.isEmpty(dataOwnerId) && dataOwnerId.equals(x.getDataOwnerId())) {
				ProvisionItemType provisionItem = new ProvisionItemType();
				provisionItem.setAtuCode(x.getAtuCode());
				provisionItem.setAtuLatinName(x.getAtuLatinName());
				provisionItem.setDataOwnerId(x.getDataOwnerId());
				provisionItem.setDataOwnerPrefLabel(x.getDataOwnerPrefLabel());
				ProvisionType provision = new ProvisionType();
				extractProvision(provision, provisionItem,x);
				provisionsItem.addProvisionItem(provisionItem);
			}
		});
	}
	
	private void extractProvision(ProvisionType provision, ProvisionItemType provisionItemDst, ProvisionItem provisionItemOrg) {
		if(provisionItemOrg.getProvision() != null) {
			provision.setRedirectURL(provisionItemOrg.getProvision().getRedirectURL());
			provision.setProvisionType(ProvisionTypeType.fromValue(provisionItemOrg.getProvision().getProvisionType()));
			ParamsType params = new ParamsType();
			if (!CollectionUtils.isEmpty(provisionItemOrg.getProvision().getParams())) {
				provisionItemOrg.getProvision().getParams().stream().forEach(p -> {
					ParamType param = new ParamType();
					param.setTitle(p.getTitle());
					ParamsSetType paramsSet = new ParamsSetType();
					if (p.getParamsSet() != null) {
						p.getParamsSet().stream().forEach(ps -> {
							paramsSet.addParamSet(ps.getParamValue());
						});
					}
					param.setParamsSet(paramsSet);
				});
				provision.setParams(params);
			}
			provisionItemDst.setProvision(provision);
		}
	}
}
