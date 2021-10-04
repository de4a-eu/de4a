<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:ext="http://exslt.org/common"
    exclude-result-prefixes="java ext">

    <xsl:output version="1.0" method="xml" encoding="UTF-8"
        omit-xml-declaration="yes" indent="no" />
    <xsl:param name="requestId" />
    <xsl:param name="ProviderId" />
    <xsl:param name="ProviderName" />
    <xsl:param name="PersonalId" />
    <xsl:param name="familyName" />
    <xsl:param name="givenName" />
    <xsl:param name="birthDate" />
    <xsl:param name="evidenceTypeId" />
    <xsl:param name="messageType" />
    <xsl:param name="CurrentTime" />

    <xsl:template match="/">    
        <query:QueryResponse
            xmlns="http://www.de4a.eu/2020/commons/type"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:4.0"
            xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0"
            xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:4.0"
            requestId="{$requestId}"
            status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success">
            
            <rim:Slot name="IssueDateTime">
                <rim:SlotValue xsi:type="rim:DateTimeValueType">
                    <rim:Value><xsl:value-of select="$CurrentTime" /></rim:Value>
                </rim:SlotValue>
            </rim:Slot>
            
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
                <rim:RegistryObject id="{$requestId}">
                    <rim:Slot name="ConceptValues">
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
                </rim:RegistryObject>
            </rim:RegistryObjectList>
        </query:QueryResponse>
    </xsl:template>
</xsl:stylesheet>
