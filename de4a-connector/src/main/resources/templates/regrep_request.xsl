<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:java="http://xml.apache.org/xslt/java"  exclude-result-prefixes="java">

    <xsl:output version="1.0" method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="no" />
    <xsl:param name="ConsumerId" />
    <xsl:param name="ConsumerName" />
    <xsl:param name="PersonalId" />
    <xsl:param name="RequestId" />
    <xsl:param name="CurrentTime" />

    <xsl:template match="/">
        <query:QueryRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		    xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:4.0"
		    xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0"
		    xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:4.0"
		    xmlns:xlink="http://www.w3.org/1999/xlink"
		    xmlns:toop="urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0:toop"
		    id="c4369c4d-740e-4b64-80f0-7b209a66d629">
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
            <rim:Slot name="DataConsumer">
		        <!--   Expression of DC information using Agent class of CAGV   -->
                <rim:SlotValue xsi:type="rim:AnyValueType">
                    <cagv:Agent xmlns:cagv="https://semic.org/sa/cv/cagv/agent-2.0.0#"
		                        xmlns:cbc="https://data.europe.eu/semanticassets/ns/cv/common/cbc_v2.0.0#"
		                        xmlns:locn="http://www.w3.org/ns/locn#">
                        <cbc:id schemeID="EIDAS"><xsl:value-of select="$ConsumerId" /></cbc:id>
                        <cbc:name><xsl:value-of select="$ConsumerName" /></cbc:name>
                    </cagv:Agent>
                </rim:SlotValue>
            </rim:Slot>
            <query:ResponseOption returnType="LeafClassWithRepositoryItem"/>
            <query:Query queryDefinition="ConceptQuery">
                <rim:Slot name="NaturalPerson">
                    <rim:SlotValue xsi:type="rim:AnyValueType">
		                <!--     Core Person Vocabulary (CPV) Expression of the LegalRepresentative -->
                        <cva:CorePerson xmlns:cva="http://www.w3.org/ns/corevocabulary/AggregateComponents"
		                    xmlns:cvb="http://www.w3.org/ns/corevocabulary/BasicComponents">
                            <cvb:PersonID schemeID="EIDAS"><xsl:value-of select="$PersonalId" /></cvb:PersonID>
                            <cvb:PersonFamilyName>XXXX</cvb:PersonFamilyName>
                            <cvb:PersonGivenName>ZZZZZ</cvb:PersonGivenName>
                            <cvb:PersonBirthDate>1900-01-01</cvb:PersonBirthDate>
                        </cva:CorePerson>
                    </rim:SlotValue>
                </rim:Slot>
                <rim:Slot name="ConceptRequestList">
                    <rim:SlotValue xsi:type="rim:CollectionValueType"
		                collectionType="urn:oasis:names:tc:ebxml-regrep:CollectionType:Set">
                        <rim:Element xsi:type="rim:AnyValueType">
                        </rim:Element>
                        <rim:Element xsi:type="rim:AnyValueType">
                            <cccev:concept xmlns:cccev = "https://data.europe.eu/semanticassets/ns/cv/cccev_v2.0.0#"
		                        xmlns:cbc="https://data.europe.eu/semanticassets/ns/cv/common/cbc_v2.0.0#"
		                        xmlns:toop="http://toop.eu/registered-organization"
		                        xsi:schemaLocation="https://data.europe.eu/semanticassets/ns/cv/cccev_v2.0.0# ../../../../xsd/cccev/2.0.0/xml/xsd/cccev-2.0.0.xsd">
                                <cbc:id>ConceptID-1</cbc:id>
                                <cbc:qName>toop:CompanyData</cbc:qName>
                            </cccev:concept>
                        </rim:Element>

                    </rim:SlotValue>
                </rim:Slot>
            </query:Query>
        </query:QueryRequest>
    </xsl:template>
</xsl:stylesheet>


