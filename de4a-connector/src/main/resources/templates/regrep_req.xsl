<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:ext="http://exslt.org/common"
    exclude-result-prefixes="java ext">

    <xsl:output version="1.0" method="xml" encoding="UTF-8"
        omit-xml-declaration="yes" indent="no" />
    <xsl:param name="requestId" />
    <xsl:param name="ConsumerId" />
    <xsl:param name="ConsumerName" />
    <xsl:param name="PersonalId" />
    <xsl:param name="familyName" />
    <xsl:param name="givenName" />
    <xsl:param name="birthDate" />
    <xsl:param name="evidenceTypeId" />
    <xsl:param name="messageType" />
    <xsl:param name="CurrentTime" />

    <xsl:template match="/">    
        <query:QueryRequest
            xmlns="http://www.de4a.eu/2020/commons/type"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:4.0"
            xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0"
            xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:4.0"
            id="{$requestId}">
            
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
            
            <query:ResponseOption
                returnType="LeafClassWithRepositoryItem" />
            <query:Query
                queryDefinition="data_element_request">
                
                <rim:Slot name="NaturalPerson">
                    <rim:SlotValue xsi:type="rim:AnyValueType">
                        <!--     Core Person Vocabulary (CPV) Expression of the LegalRepresentative -->
                        <cva:CorePerson xmlns:cva="http://www.w3.org/ns/corevocabulary/AggregateComponents"
                            xmlns:cvb="http://www.w3.org/ns/corevocabulary/BasicComponents">
                            <cvb:PersonID schemeID="EIDAS"><xsl:value-of select="$PersonalId" /></cvb:PersonID>                            
                            <cvb:PersonFamilyName><xsl:value-of select="$familyName" /></cvb:PersonFamilyName>
                            <cvb:PersonGivenName><xsl:value-of select="$givenName" /></cvb:PersonGivenName>
                            <cvb:PersonBirthDate><xsl:value-of select="$birthDate" /></cvb:PersonBirthDate>
                        </cva:CorePerson>
                    </rim:SlotValue>
                </rim:Slot>
                
                <rim:Slot name="ConceptRequestList">
                    <rim:SlotValue xsi:type="rim:CollectionValueType"
                        collectionType="urn:oasis:names:tc:ebxml-regrep:CollectionType:Set">
                        <rim:Element xsi:type="rim:AnyValueType">
                            <cccev:concept xmlns:cccev = "https://data.europe.eu/semanticassets/ns/cv/cccev_v2.0.0#"
                                xmlns:cbc="https://data.europe.eu/semanticassets/ns/cv/common/cbc_v2.0.0#">
                                <cbc:id><xsl:value-of select="$evidenceTypeId" /></cbc:id>
                                <cbc:qName><xsl:value-of select="$messageType" /></cbc:qName>                            
                            </cccev:concept>
                        </rim:Element>
                        <rim:Element xsi:type="rim:AnyValueType">
                            <MessageContent>content</MessageContent>
                        </rim:Element>
                    </rim:SlotValue>
                </rim:Slot>
            </query:Query>
        </query:QueryRequest>
    </xsl:template>
</xsl:stylesheet>
