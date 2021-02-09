<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ns0="http://intermediacion.redsara.es/scsp/esquemas/V3/peticion"
	xmlns:ns1="http://intermediacion.redsara.es/scsp/esquemas/datosespecificos" 
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"     
	xmlns:entity="xalan://eu.de4a.scsp.mock.dba.Entity" 
    exclude-result-prefixes="entity"> 

<!-- xmlns:lists="java:java.util.List" 
	xmlns:domestic= "java:eu.de4a.conn.api.requestor.DomesticEvidenceType" -->
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />  
	<xsl:param name="entity" />	  
	<xsl:template match="/"> 
		<NationalDBA>
			<BusinessEntity xmlns:xml="http://www.w3.org/XML/1998/namespace" xmlns:nsA="http://www.w3.org/1999/xhtml" xsi:noNamespaceSchemaLocation="schema.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
					</BusinessEntity>
		</NationalDBA>
	 </xsl:template>
</xsl:stylesheet>
