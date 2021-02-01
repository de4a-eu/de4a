<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:java="http://xml.apache.org/xslt/java"  exclude-result-prefixes="java"> 

	<xsl:output version="1.0" method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="no" /> 
	<xsl:param name="ProviderId" />
	<xsl:param name="ProviderName" />  
	<xsl:param name="CurrentTime" />	
	
	<xsl:template match="/">  
		<query:QueryResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		    xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:4.0"
		    xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0"
		    xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:4.0"
		    requestId="c4369c4d-740e-4b64-80f0-7b209a66d629"
		    status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success">
		   
		    <!-- SPECIFICATION IDENTIFIER -->
		    <rim:Slot name="SpecificationIdentifier">
		        <rim:SlotValue xsi:type="rim:StringValueType">
		            <rim:Value>toop-edm:v2.1</rim:Value>
		        </rim:SlotValue>
		    </rim:Slot>
		   
		    <!-- ISSUE DATE / TIME  -->
		    <rim:Slot name="IssueDateTime">
		        <rim:SlotValue xsi:type="rim:DateTimeValueType">
		            <rim:Value><xsl:value-of select="$CurrentTime" /></rim:Value>
		        </rim:SlotValue>
		    </rim:Slot>
		    
		    <!--   Data Provider Metadata  -->
		    <rim:Slot name="DataProvider">
		        <rim:SlotValue xsi:type="rim:AnyValueType">
		            <!--   Expression of DP information using Agent class of CAGV   -->
		            <cagv:Agent xmlns:cagv="https://semic.org/sa/cv/cagv/agent-2.0.0#"
		                xmlns:cbc="https://data.europe.eu/semanticassets/ns/cv/common/cbc_v2.0.0#"
		                xmlns:locn="http://www.w3.org/ns/locn#">
		                <cbc:id schemeID="VAT"><xsl:value-of select="$ProviderId" /></cbc:id>
		                <cbc:name><xsl:value-of select="$ProviderName" /></cbc:name>
		            </cagv:Agent>  
		        </rim:SlotValue>
		    </rim:Slot>
		    
		    <rim:RegistryObjectList>
		        <rim:RegistryObject id="341341341-740e-4b64-80f0-3153513529">
		        
		            <rim:Slot name="ConceptValues">
		                <rim:SlotValue xsi:type="rim:CollectionValueType"
		                    collectionType="urn:oasis:names:tc:ebxml-regrep:CollectionType:Set">
		                    <rim:Element xsi:type="rim:AnyValueType">
		                        <cccev:concept xmlns:cccev = "https://data.europe.eu/semanticassets/ns/cv/cccev_v2.0.0#"
		                        xmlns:cbc="https://data.europe.eu/semanticassets/ns/cv/common/cbc_v2.0.0#"
		                        xmlns:toop="http://toop.eu/registered-organization"
		                        xsi:schemaLocation="https://data.europe.eu/semanticassets/ns/cv/cccev_v2.0.0# ../../../../xsd/cccev/2.0.0/xml/xsd/cccev-2.0.0.xsd">  
		                            <cbc:id>ConceptID-2</cbc:id>
		                            <cbc:qName>toop:Concept-Name-2</cbc:qName> 
		                        </cccev:concept>
		                    </rim:Element> 
		                </rim:SlotValue>
		            </rim:Slot>
		        </rim:RegistryObject>
		    </rim:RegistryObjectList>
		</query:QueryResponse> 
	</xsl:template>
</xsl:stylesheet>
	

