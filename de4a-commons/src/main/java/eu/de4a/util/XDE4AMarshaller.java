package eu.de4a.util;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.jaxb.JAXBContextCache;

import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceUSIType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.CDE4AJAXB;
import eu.de4a.iem.xml.de4a.DE4ANamespaceContext;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;

/**
 * DE4A Marshaller factory for the core data format
 *
 * @author Philip Helger
 */
public class XDE4AMarshaller<JAXBTYPE> extends GenericJAXBMarshaller<JAXBTYPE> {
	public XDE4AMarshaller ( final Class <JAXBTYPE> aType,
	           final List <? extends ClassPathResource> aXSDs,
	           final Function <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> aJAXBElementWrapper) {
			super (aType, aXSDs, aJAXBElementWrapper);
			setNamespaceContext (DE4ANamespaceContext.getInstance ());
	}
	
	@Override
	protected JAXBContext getJAXBContext(final ClassLoader aClassLoader) throws JAXBException {

		if (isUseContextCache())
			return JAXBContextCache.getInstance().getFromCache(new CommonsArrayList<>(DE4AConstants.aClasses));
		return JAXBContext.newInstance(DE4AConstants.aClasses);
	}
	

	/**
	 * Enable formatted output. Syntactic sugar.
	 *
	 * @return this for chaining
	 */
	
	public final XDE4AMarshaller<JAXBTYPE> formatted() {
		setFormattedOutput(true);
		return this;
	}

	
	@Nonempty
	private static ICommonsList<ClassPathResource> _getXSDs( final ClassPathResource aCoreXSD,
			final ICommonsList<? extends ClassPathResource> aCanonicalEvidenceXSDs) {
		final ICommonsList<ClassPathResource> ret = new CommonsArrayList<>();
		ret.addAll(CDE4AJAXB.XSDS);
		ret.add(aCoreXSD);
		if (aCanonicalEvidenceXSDs != null)
			ret.addAll(aCanonicalEvidenceXSDs);
		return ret;
	}

	
	public static XDE4AMarshaller<RequestForwardEvidenceType> deUsiRequestMarshaller(
			 final IDE4ACanonicalEvidenceType aCanonicalEvidenceType) {
		return new XDE4AMarshaller<>(RequestForwardEvidenceType.class,
				_getXSDs(CDE4AJAXB.XSD_DE_USI, aCanonicalEvidenceType.getAllXSDs()),
				new eu.de4a.iem.jaxb.de_usi.ObjectFactory()::createRequestForwardEvidence);
	}

	
	public static XDE4AMarshaller<ResponseErrorType> deUsiResponseMarshaller() {
		return new XDE4AMarshaller<>(ResponseErrorType.class, _getXSDs(CDE4AJAXB.XSD_DE_USI, null),
				new eu.de4a.iem.jaxb.de_usi.ObjectFactory()::createResponseForwardEvidence);
	}

	
	public static XDE4AMarshaller<RequestExtractEvidenceIMType> doImRequestMarshaller() {
		return new XDE4AMarshaller<>(RequestExtractEvidenceIMType.class, _getXSDs(CDE4AJAXB.XSD_DT_DO_IM, null),
				new eu.de4a.iem.jaxb.do_im.ObjectFactory()::createRequestExtractEvidence);
	}

	
	public static XDE4AMarshaller<ResponseExtractEvidenceType> doImResponseMarshaller(
			 final IDE4ACanonicalEvidenceType aCanonicalEvidenceType) {
		return new XDE4AMarshaller<>(ResponseExtractEvidenceType.class,
				_getXSDs(CDE4AJAXB.XSD_DT_DO_IM, aCanonicalEvidenceType.getAllXSDs()),
				new eu.de4a.iem.jaxb.do_im.ObjectFactory()::createResponseExtractEvidence);
	}

	
	public static XDE4AMarshaller<RequestExtractEvidenceUSIType> doUsiRequestMarshaller() {
		return new XDE4AMarshaller<>(RequestExtractEvidenceUSIType.class, _getXSDs(CDE4AJAXB.XSD_DO_USI, null),
				new eu.de4a.iem.jaxb.do_usi.ObjectFactory()::createRequestExtractEvidence);
	}

	
	public static XDE4AMarshaller<ResponseErrorType> doUsiResponseMarshaller() {
		return new XDE4AMarshaller<>(ResponseErrorType.class, _getXSDs(CDE4AJAXB.XSD_DO_USI, null),
				new eu.de4a.iem.jaxb.do_usi.ObjectFactory()::createResponseExtractEvidence);
	}

	
	public static XDE4AMarshaller<RequestTransferEvidenceUSIIMDRType> drImRequestMarshaller() {
		return new XDE4AMarshaller<>(RequestTransferEvidenceUSIIMDRType.class, _getXSDs(CDE4AJAXB.XSD_DR_DE_IM, null),
				new eu.de4a.iem.jaxb.dr_im.ObjectFactory()::createRequestTransferEvidence);
	}

	
	public static XDE4AMarshaller<ResponseTransferEvidenceType> drImResponseMarshaller(
			 final IDE4ACanonicalEvidenceType aCanonicalEvidenceType) {
		return new XDE4AMarshaller<>(ResponseTransferEvidenceType.class,
				_getXSDs(CDE4AJAXB.XSD_DR_DE_IM, aCanonicalEvidenceType.getAllXSDs()),
				new eu.de4a.iem.jaxb.dr_im.ObjectFactory()::createResponseTransferEvidence);
	}

	
	public static XDE4AMarshaller<RequestTransferEvidenceUSIIMDRType> drUsiRequestMarshaller() {
		return new XDE4AMarshaller<>(RequestTransferEvidenceUSIIMDRType.class, _getXSDs(CDE4AJAXB.XSD_DR_USI, null),
				new eu.de4a.iem.jaxb.dr_usi.ObjectFactory()::createRequestTransferEvidence);
	}

	
	public static XDE4AMarshaller<ResponseErrorType> drUsiResponseMarshaller() {
		return new XDE4AMarshaller<>(ResponseErrorType.class, _getXSDs(CDE4AJAXB.XSD_DR_USI, null),
				new eu.de4a.iem.jaxb.dr_usi.ObjectFactory()::createResponseTransferEvidence);
	}

	
	public static XDE4AMarshaller<RequestTransferEvidenceUSIDTType> dtUsiRequestMarshaller(
			 final IDE4ACanonicalEvidenceType aCanonicalEvidenceType) {
		return new XDE4AMarshaller<>(RequestTransferEvidenceUSIDTType.class,
				_getXSDs(CDE4AJAXB.XSD_DT_USI, aCanonicalEvidenceType.getAllXSDs()),
				new eu.de4a.iem.jaxb.dt_usi.ObjectFactory()::createRequestTransferEvidence);
	}

	
	public static XDE4AMarshaller<ResponseErrorType> dtUsiResponseMarshaller() {
		return new XDE4AMarshaller<>(ResponseErrorType.class, _getXSDs(CDE4AJAXB.XSD_DT_USI, null),
				new eu.de4a.iem.jaxb.dt_usi.ObjectFactory()::createResponseTransferEvidence);
	}

	
	public static XDE4AMarshaller<RequestLookupRoutingInformationType> idkRequestLookupRoutingInformationMarshaller() {
		return new XDE4AMarshaller<>(RequestLookupRoutingInformationType.class, _getXSDs(CDE4AJAXB.XSD_DR_DT_IDK, null),
				new eu.de4a.iem.jaxb.idk.ObjectFactory()::createRequestLookupRoutingInformation);
	}

	
	public static XDE4AMarshaller<ResponseLookupRoutingInformationType> idkResponseLookupRoutingInformationMarshaller() {
		return new XDE4AMarshaller<>(ResponseLookupRoutingInformationType.class, _getXSDs(CDE4AJAXB.XSD_DR_DT_IDK, null),
				new eu.de4a.iem.jaxb.idk.ObjectFactory()::createResponseLookupRoutingInformation);
	}
}
