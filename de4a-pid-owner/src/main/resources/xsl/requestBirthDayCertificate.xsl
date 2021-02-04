<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ns0="http://intermediacion.redsara.es/scsp/esquemas/V3/peticion"
	xmlns:ns1="http://intermediacion.redsara.es/scsp/esquemas/datosespecificos"
	xmlns:ns2="http://justicia.es/esb/comun/xsd-schemas/V1" 
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" /> 
	<xsl:param name="idPeticion" />
	<xsl:param name="timeStamp" />
	<xsl:param name="nifSolicitante" />	
	<xsl:param name="procedimiento" />	
	<xsl:param name="tipoDocumentacion" />
	<xsl:param name="documentacion" />	
	<xsl:param name="nombre" />
	<xsl:param name="ap1" />
	<xsl:param name="ap2" />
	<xsl:param name="fechaNacimiento" />
	<xsl:template match="/"> 
				<ns0:Peticion> 
						<ns0:Atributos>
							<ns0:IdPeticion>
								<xsl:value-of select="string($idPeticion)" />
							</ns0:IdPeticion>
							<ns0:NumElementos>1</ns0:NumElementos>
							<ns0:TimeStamp><xsl:value-of select="string($timeStamp)" /></ns0:TimeStamp>
							<ns0:CodigoCertificado><xsl:value-of select="string('SVDSCCNWS01')" /></ns0:CodigoCertificado>
						</ns0:Atributos>
						<ns0:Solicitudes> 
								<ns0:SolicitudTransmision> 
										<ns0:DatosGenericos>
											<ns0:Emisor>
												<ns0:NifEmisor>
													<xsl:value-of select="string('S2813001A')" />
												</ns0:NifEmisor>
												<ns0:NombreEmisor>
													<xsl:value-of select="string('MINISTERIO DE JUSTICIA')" />
												</ns0:NombreEmisor>
											</ns0:Emisor>
											<ns0:Solicitante>
												<ns0:IdentificadorSolicitante>
													<xsl:value-of select="string($nifSolicitante)" />
												</ns0:IdentificadorSolicitante>
												<ns0:NombreSolicitante>
													<xsl:value-of select="string($nifSolicitante)" />
												</ns0:NombreSolicitante> 
													<ns0:UnidadTramitadora><xsl:value-of select="string($nifSolicitante)" /></ns0:UnidadTramitadora>  
													<ns0:Procedimiento> 
															<ns0:CodProcedimiento><xsl:value-of select="string($procedimiento)" /></ns0:CodProcedimiento> 
															<ns0:NombreProcedimiento><xsl:value-of select="string($procedimiento)" /></ns0:NombreProcedimiento> 
													</ns0:Procedimiento> 
												<ns0:Finalidad><xsl:value-of select="string('Test from De4a')" /></ns0:Finalidad>
												<ns0:Consentimiento> <xsl:value-of select="string('Si')" /></ns0:Consentimiento>
												<ns0:Funcionario>
													<ns0:NombreCompletoFuncionario>
														<xsl:value-of select="string('Juan Español Español')" />
													</ns0:NombreCompletoFuncionario>
													<ns0:NifFuncionario>
																<xsl:value-of select="string('99999999R')" />
													</ns0:NifFuncionario>
												</ns0:Funcionario>
											</ns0:Solicitante>
											<ns0:Titular>  
													<ns0:TipoDocumentacion><xsl:value-of select="string($tipoDocumentacion)" /></ns0:TipoDocumentacion> 
													<ns0:Documentacion>
														<xsl:value-of select="string($documentacion)" />
													</ns0:Documentacion>  
													<ns0:Nombre>
														<xsl:value-of select="string($nombre)" />
													</ns0:Nombre> 
													<ns0:Apellido1> 
														<xsl:value-of select="string($ap1)" />
													</ns0:Apellido1> 
													<ns0:Apellido2> 
														<xsl:value-of select="string($ap2)" />
													</ns0:Apellido2> 
											</ns0:Titular>
											<ns0:Transmision>
												<ns0:CodigoCertificado>
													<xsl:value-of select="string('SVDSCCNWS01')" />
												</ns0:CodigoCertificado>
												<ns0:IdSolicitud><xsl:value-of select="string('1')" />  </ns0:IdSolicitud>
												<ns0:IdTransmision />
												<ns0:FechaGeneracion />
											</ns0:Transmision>
										</ns0:DatosGenericos> 
										<ns1:DatosEspecificos> 
												<ns1:Consulta> 
														<ns1:DatosAdicionalesTitularConsulta> 
																<ns1:FechaHechoRegistral> <xsl:value-of select="string($fechaNacimiento)" /></ns1:FechaHechoRegistral> 
																<ns1:AusenciaSegundoApellido>
																	<xsl:choose>
																		<xsl:when test="$ap2"><xsl:value-of select="string('false')" /></xsl:when>
																		<xsl:otherwise><xsl:value-of select="string('true')" /></xsl:otherwise>
																	</xsl:choose>
																</ns1:AusenciaSegundoApellido>
														</ns1:DatosAdicionalesTitularConsulta> 
												</ns1:Consulta> 
										</ns1:DatosEspecificos> 
								</ns0:SolicitudTransmision> 
						</ns0:Solicitudes> 
				</ns0:Peticion> 
	</xsl:template>
</xsl:stylesheet>
