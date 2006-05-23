/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.extension.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.loader.LoaderContext;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Scope;

/**
 * Abstract implementation loader.
 *
 */
@Scope("MODULE")
public abstract class AbstractImplementationLoader<T extends Implementation> implements StAXElementLoader<T> {

//	// Injected Stax loader registry.
//    protected StAXLoaderRegistry registry;
//
//    // XML input factory.
//    protected XMLInputFactory xmlFactory;
//
//    /**
//     * Initializes the XML input factory.
//     *
//     */
//    public AbstractImplementationLoader() {
//        xmlFactory = XMLInputFactory.newInstance();
//    }
//
//    /**
//     * Injection method for Stax loader registry.
//     * @param registry Stax loader registry.
//     */
//    @Autowire
//    public void setRegistry(StAXLoaderRegistry registry) {
//        this.registry = registry;
//    }
//
//    /**
//     * Loads the Groovy implementation.
//     *
//     * @param reader XML stream reader.
//     * @param loaderContext Loader context.
//     * @return Groovy implementation.
//     */
//	public T load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
//
//		T assemblyObject = getAssemblyObject(reader, loaderContext);
//
//		URL componentTypeFile = getSideFile(reader, loaderContext);
//        ComponentType componentType = loadComponentType(componentTypeFile, loaderContext);
//
//		assemblyObject.setComponentType(componentType);
//
//		return assemblyObject;
//
//	}
//
//	/**
//	 * Required to be implemented by the concrete classes.
//	 * @return Implementation object.
//	 */
//	protected abstract T getAssemblyObject(XMLStreamReader reader, LoaderContext loaderContext);
//
//    /**
//     * Gets the side file.
//     *
//     * @param reader Reader for the module file.
//     * @param loaderContext Loader context.
//     * @return Side file Url.
//     * @throws java.util.MissingResourceException
//     */
//	protected abstract URL getSideFile(XMLStreamReader reader, LoaderContext loaderContext) throws MissingResourceException;
//
//	/**
//	 * Loads the SIDE file to get the component information.
//	 *
//	 * @param scriptFile SCript file name.
//	 * @param loaderContext Loader context.
//	 * @return Component information.
//	 * @throws SidefileLoadException
//	 * @throws MissingResourceException
//	 */
//    private ComponentType loadComponentType(URL componentTypeFile, LoaderContext loaderContext) throws SidefileLoadException, MissingResourceException{
//
//    	XMLStreamReader reader = null;
//    	InputStream is = null;
//
//        try {
//            is = componentTypeFile.openStream();
//            reader = xmlFactory.createXMLStreamReader(is);
//            reader.nextTag();
//            if (!AssemblyConstants.COMPONENT_TYPE.equals(reader.getName())) {
//                InvalidRootElementException e = new InvalidRootElementException(AssemblyConstants.COMPONENT_TYPE, reader.getName());
//                e.setResourceURI(componentTypeFile.toString());
//                throw e;
//            }
//            return (ComponentType) registry.load(reader, loaderContext);
//        } catch (IOException e) {
//            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
//            sfe.setResourceURI(componentTypeFile.toString());
//            throw sfe;
//        } catch (XMLStreamException e) {
//            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
//            sfe.setResourceURI(componentTypeFile.toString());
//            throw sfe;
//        } catch (ConfigurationLoadException e) {
//            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
//            sfe.setResourceURI(componentTypeFile.toString());
//            throw sfe;
//        } finally {
//            try {
//            	if(reader != null) {
//            		reader.close();
//            	}
//            } catch (XMLStreamException e) {
//                // ignore
//            }
//            try {
//            	if(is != null) {
//            		is.close();
//            	}
//            } catch (IOException e) {
//                // ignore
//            }
//        }
//    }

}
