<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ns0="http://intermediacion.redsara.es/scsp/esquemas/V3/peticion"
	xmlns:ns1="http://intermediacion.redsara.es/scsp/esquemas/datosespecificos" 
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"    
	xmlns:lists="xalan://java.util.List" 
	xmlns:domestic="xalan://eu.de4a.iem.jaxb.common.types.DomesticEvidenceType" 
    exclude-result-prefixes="lists domestic"> 

<!-- xmlns:lists="java:java.util.List" 
	xmlns:domestic= "java:eu.de4a.conn.api.requestor.DomesticEvidenceType" -->
	<xsl:output method="xml" encoding="UTF-8" indent="yes" /> 
	<xsl:param name="idPeticion" />
	<xsl:param name="timeStamp" />
	<xsl:param name="lugarNacimiento" />	
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
	<xsl:param name="nameMunicipio" />
	<xsl:param name="sexo" />
	<xsl:template match="/"> 
<ResponseExtractEvidence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.de4a.eu/2020/data/requestor/pattern/intermediate iem.xsd">
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
		<DataSubjectPerson>	
			<Identifier><xsl:value-of select="$documentacion" /></Identifier>
			<GivenName><xsl:value-of select="$nombre" /></GivenName>
			<FamilyName><xsl:value-of select="$nombre" /><xsl:text> </xsl:text><xsl:value-of select="$ap1" /></FamilyName>
			<DateOfBirth><xsl:value-of select="$fechaNacimiento" /></DateOfBirth>
			<Gender></Gender>
			<BirthName></BirthName>
			<PlaceOfBirth><xsl:value-of select="$paisNacimiento" /></PlaceOfBirth>
			<CurrentAddress></CurrentAddress>
		</DataSubjectPerson>
	</DataRequestSubject>
	<CanonicalEvidenceId><xsl:value-of select="$canonicalEvidenceId" /></CanonicalEvidenceId>
	<CanonicalEvidence>
			<BirthEvidence xsi:noNamespaceSchemaLocation="BirthEvidence.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
				<IssuingDate><xsl:value-of select="$fechaNacimiento" /></IssuingDate>
				<IssuingAuthority>
					<PrefLabel>Registro Civil Central. Ministerio de Justicia</PrefLabel>
				</IssuingAuthority>
				<CerfifiedBirth>
					<Child>
						<DateOfBirth><xsl:value-of select="$fechaNacimiento" /></DateOfBirth>
						<PlaceOfBirth>
							<GeographicName><xsl:value-of select="$nameMunicipio" /></GeographicName>
						</PlaceOfBirth>
						<Gender>
							<xsl:choose>
								<xsl:when test="$sexo = 'V' ">
										<xsl:text>http://publications.europa.eu/resource/authority/human-sex/MALE"</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>http://publications.europa.eu/resource/authority/human-sex/FEMALE"</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</Gender>
						<GivenName><xsl:value-of select="$nombre" /></GivenName>
						<FamilyName><xsl:value-of select="$ap1" /></FamilyName>
					</Child>
				</CerfifiedBirth>
			</BirthEvidence>
			 
	</CanonicalEvidence>  
		 <xsl:variable name="count"  select="lists:size($domesticEvidences) - 1"/>  
	   	 <DomesticEvidenceList>
			<xsl:call-template name="domesticEvidences_template">
					          <xsl:with-param name="count" select="$count"/>
					          <xsl:with-param name="domesticEvidences" select="$domesticEvidences"/>
			</xsl:call-template>
		</DomesticEvidenceList> 
</ResponseExtractEvidence>
</xsl:template>
<xsl:template name="domesticEvidences_template">
  					<xsl:param name="count"/>
  					<xsl:param name="domesticEvidences"/> 
		 	   		<xsl:if test="$count > -1">
		 	   			 <xsl:variable name="entity" select="lists:get( $domesticEvidences, $count )" />  
			    	 	 <DomesticEvidence xmlns="http://www.de4a.eu/2020/data/requestor/pattern/intermediate">
							<IssuingType><xsl:value-of  select="domestic:getIssuingType($entity)"/></IssuingType>
							<MimeType><xsl:value-of  select="domestic:getMimeType($entity)"/></MimeType>
							<DataLanguage><xsl:value-of  select="domestic:getDataLanguage($entity)"/></DataLanguage>
							<EvidenceData/>
						</DomesticEvidence> 
						<xsl:call-template name="domesticEvidences_template">
				          <xsl:with-param name="count" select="$count - 1"/>
				          <xsl:with-param name="domesticEvidences" select="$domesticEvidences"/>
				        </xsl:call-template>
       
			      	</xsl:if>
	</xsl:template>
</xsl:stylesheet>
