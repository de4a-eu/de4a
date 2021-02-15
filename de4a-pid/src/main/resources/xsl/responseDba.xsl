<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ns0="http://intermediacion.redsara.es/scsp/esquemas/V3/peticion"
	xmlns:ns1="http://intermediacion.redsara.es/scsp/esquemas/datosespecificos" 
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"    
	xmlns:lists="xalan://java.util.List" 
	xmlns:domestic="xalan://eu.de4a.conn.api.requestor.DomesticEvidenceType" 
	xmlns:entity="xalan://eu.de4a.scsp.mock.dba.Entity" 
    exclude-result-prefixes="lists domestic entity"> 

<!-- xmlns:lists="java:java.util.List" 
	xmlns:domestic= "java:eu.de4a.conn.api.requestor.DomesticEvidenceType" -->
	<xsl:output method="xml" encoding="UTF-8" indent="yes" /> 
	<xsl:param name="idPeticion" />
	<xsl:param name="timeStamp" />
	<xsl:param name="entity" />	
	<xsl:param name="paisNacimiento" />	
	<xsl:param name="tipoDocumentacion" />
	<xsl:param name="documentacion" />	
	<xsl:param name="nombre" />
	<xsl:param name="ap1" /> 
	<xsl:param name="fechaNacimiento" />
	<xsl:param name="evaluatorId" />
	<xsl:param name="evaluatorName" />
	<xsl:param name="ownerId" />
	<xsl:param name="ownerName" />
	<xsl:param name="canonicalEvidenceId" />
	<xsl:param name="domesticEvidences" /> 
	<xsl:template match="/"> 
<ResponseTransferEvidence xmlns="http://www.de4a.eu/2020/data/requestor/pattern/intermediate" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.de4a.eu/2020/data/requestor/pattern/intermediate iem.xsd">
	<RequestId><xsl:value-of select="$idPeticion" /></RequestId>
	<SpecificationId>De4a-1.0.0</SpecificationId>
	<TimeStamp><xsl:value-of select="$timeStamp" /></TimeStamp>
	<DataEvaluator xmlns:csbc="https://semic.org/sa/cv/common/cbc-2.0.0#" xmlns:cagv="https://semic.org/sa/cv/cagv/agent-2.0.0#">
			<csbc:id ><xsl:value-of select="$evaluatorId" /></csbc:id>
			<csbc:name><xsl:value-of select="$evaluatorName" /></csbc:name> 
	</DataEvaluator>
	<DataOwner xmlns:csbc="https://semic.org/sa/cv/common/cbc-2.0.0#" xmlns:cagv="https://semic.org/sa/cv/cagv/agent-2.0.0#">
			<csbc:id ><xsl:value-of select="$ownerId" /></csbc:id>
			<csbc:name><xsl:value-of select="$ownerName" /></csbc:name> 
	</DataOwner>
	<DataRequestSubject>
		<DataSubjectCompany>	
			<LegalEntityIdentifier><xsl:value-of select="entity:getType($entity)" /></LegalEntityIdentifier>
			<LegalEntityName><xsl:value-of select="entity:getName($entity)" /></LegalEntityName> 
		</DataSubjectCompany>
	</DataRequestSubject>
	<CanonicalEvidenceId><xsl:value-of select="$canonicalEvidenceId" /></CanonicalEvidenceId>
	<CanonicalEvidence>
		 <LegalEntity xmlns:xml="http://www.w3.org/XML/1998/namespace" xmlns:nsA="http://www.w3.org/1999/xhtml" xsi:noNamespaceSchemaLocation="schema.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<CompanyName>
					<LegalEntityName><xsl:value-of  select="entity:getName($entity)"/></LegalEntityName>
				</CompanyName>
				<CompanyType><xsl:value-of  select="entity:getType($entity)"/></CompanyType>
				<CompanyStatus><xsl:value-of  select="entity:getStatus($entity)"/></CompanyStatus>
				<CompanyActivity>
					<NaceCode><xsl:value-of  select="entity:getActivity($entity)"/></NaceCode>
				</CompanyActivity>
				<RegistrationDate><xsl:value-of  select="entity:getRegistrationDate($entity)"/></RegistrationDate>
				<CompanyEUID><xsl:value-of  select="entity:getId($entity)"/></CompanyEUID>
				<CompanyContactData>
					<Email><xsl:value-of  select="entity:getEmail($entity)"/></Email>
					<Telephone><xsl:value-of  select="entity:getTlf($entity)"/></Telephone>
				</CompanyContactData>
				<RegisteredAddress>
			 		<Thoroughfare><xsl:value-of  select="entity:getVia($entity)"/></Thoroughfare>
					<LocationDesignator><xsl:value-of  select="entity:getLocationDesignator($entity)"/></LocationDesignator>
					<PostCode><xsl:value-of  select="entity:getCp($entity)"/></PostCode>
					<PostName><xsl:value-of  select="entity:getCpname($entity)"/></PostName>
					<AdminUnitL1><xsl:value-of  select="entity:getCountry($entity)"/></AdminUnitL1>
				</RegisteredAddress>
				<PostalAddress>
					<Thoroughfare><xsl:value-of  select="entity:getVia($entity)"/></Thoroughfare>
					<LocationDesignator><xsl:value-of  select="entity:getLocationDesignator($entity)"/></LocationDesignator>
					<PostCode><xsl:value-of  select="entity:getCp($entity)"/></PostCode>
					<PostName><xsl:value-of  select="entity:getCpname($entity)"/></PostName>
					<AdminUnitL1><xsl:value-of  select="entity:getCountry($entity)"/></AdminUnitL1>
				</PostalAddress> 
			</LegalEntity>
		</CanonicalEvidence>  
		 <xsl:variable name="count"  select="lists:size($domesticEvidences) - 1"/>  
	   	 <DomesticEvidenceList>
			<xsl:call-template name="domesticEvidences_template">
					          <xsl:with-param name="count" select="$count"/>
					          <xsl:with-param name="domesticEvidences" select="$domesticEvidences"/>
			</xsl:call-template>
		</DomesticEvidenceList> 
</ResponseTransferEvidence>
</xsl:template>
<xsl:template name="domesticEvidences_template">
  					<xsl:param name="count"/>
  					<xsl:param name="domesticEvidences"/> 
		 	   		<xsl:if test="$count > -1">
		 	   			 <xsl:variable name="entity" select="lists:get( $domesticEvidences, $count )" />  
			    	 	 <DomescticEvidence xmlns="http://www.de4a.eu/2020/data/requestor/pattern/intermediate" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.de4a.eu/2020/data/requestor/pattern/intermediate iem.xsd">
							<DomescticEvidenceIdRef><xsl:value-of  select="domestic:getDomesticEvidenceIdRef($entity)"/></DomescticEvidenceIdRef>
							<IssuingType><xsl:value-of  select="domestic:getIssuingType($entity)"/></IssuingType>
							<MimeType><xsl:value-of  select="domestic:getMimeType($entity)"/></MimeType>
							<DataLanguage><xsl:value-of  select="domestic:getDataLanguage($entity)"/></DataLanguage>
						</DomescticEvidence> 
						<xsl:call-template name="domesticEvidences_template">
				          <xsl:with-param name="count" select="$count - 1"/>
				          <xsl:with-param name="domesticEvidences" select="$domesticEvidences"/>
				        </xsl:call-template>
       
			      	</xsl:if>
	</xsl:template>
</xsl:stylesheet>
