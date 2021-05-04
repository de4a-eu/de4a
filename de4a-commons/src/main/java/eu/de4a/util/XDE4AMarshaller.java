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
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.jaxb.JAXBContextCache;
import eu.de4a.conn.api.canonical.EvidencesResources;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;

/**
 * XDE4A Marshaller factory for the core data format
 *
 * Walk-around to include on jaxb context object factories of canonical evidence not included on de4a-iem
 *
 * @author Philip Helger
 */
public class XDE4AMarshaller<JAXBTYPE> extends DE4AMarshaller<JAXBTYPE> {
	public XDE4AMarshaller ( final Class <JAXBTYPE> aType,
	           final List <? extends ClassPathResource> aXSDs,
	           final Function <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> aJAXBElementWrapper) {
			super (aType, aXSDs, aJAXBElementWrapper);
	}

	@Override
	protected JAXBContext getJAXBContext(final ClassLoader aClassLoader) throws JAXBException {

		if (isUseContextCache())
			return JAXBContextCache.getInstance().getFromCache(new CommonsArrayList<>(EvidencesResources.aClasses));
		return JAXBContext.newInstance(EvidencesResources.aClasses);
	}
}
