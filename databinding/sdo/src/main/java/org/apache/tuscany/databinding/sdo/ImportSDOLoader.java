/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.databinding.sdo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import commonj.sdo.helper.XSDHelper;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.SidefileLoadException;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.loader.assembly.AbstractLoader;
import org.apache.tuscany.core.loader.assembly.AssemblyConstants;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.osoa.sca.annotations.Scope;

/**
 * Loader that handles &lt;import.sdo&gt; elements.
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ImportSDOLoader extends AbstractLoader {
    public static final QName IMPORT_SDO = new QName(AssemblyConstants.SCA_NAMESPACE, "import.sdo");

    public QName getXMLType() {
        return IMPORT_SDO;
    }

    public AssemblyObject load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        assert IMPORT_SDO.equals(reader.getName());
        importFactory(reader, loaderContext);
        importWSDL(reader, loaderContext);
        StAXUtil.skipToEndElement(reader);
        return null;
    }

    private void importFactory(XMLStreamReader reader, LoaderContext loaderContext) throws ConfigurationLoadException {
        String factoryName = reader.getAttributeValue(null, "factory");
        if (factoryName != null) {
            ResourceLoader resourceLoader = loaderContext.getResourceLoader();
            ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            try {
                // set TCCL as SDO needs it
                Thread.currentThread().setContextClassLoader(resourceLoader.getClassLoader());
                Class<?> factoryClass = resourceLoader.loadClass(factoryName);
                SDOUtil.registerStaticTypes(factoryClass);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationLoadException(e.getMessage(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }

    private void importWSDL(XMLStreamReader reader, LoaderContext loaderContext) throws ConfigurationLoadException {
        String wsdLLocation = reader.getAttributeValue(null, "wsdlLocation");
        if (wsdLLocation != null) {
            ResourceLoader resourceLoader = loaderContext.getResourceLoader();
            URL wsdlURL = resourceLoader.getResource(wsdLLocation);
            ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            try {
//                Thread.currentThread().setContextClassLoader(resourceLoader.getClassLoader());
                InputStream xsdInputStream = wsdlURL.openStream();
                try {
                    AssemblyContext context = registry.getContext();
                    XSDHelper xsdHelper = SDOUtil.createXSDHelper(context.getTypeHelper());
                    xsdHelper.define(xsdInputStream, null);
                } finally {
                    xsdInputStream.close();
                }
            } catch (IOException e) {
                SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
                sfe.setResourceURI(wsdLLocation);
                throw sfe;
            } finally {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }
}
