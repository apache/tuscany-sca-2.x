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

import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.annotation.Autowire;

import org.osoa.sca.annotations.Constructor;

/**
 * Loader that handles &lt;import.sdo&gt; elements.
 *
 * @version $Rev: 419382 $ $Date: 2006-07-05 16:14:37 -0700 (Wed, 05 Jul 2006) $
 */
public class ImportSDOLoader extends LoaderExtension {
    public static final QName IMPORT_SDO = new QName("http://www.osoa.org/xmlns/sca/0.9", "import.sdo");

    @Constructor({"registry"})
    public ImportSDOLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return IMPORT_SDO;
    }

    public ModelObject load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext loaderContext)
        throws XMLStreamException, LoaderException {
        assert IMPORT_SDO.equals(reader.getName());
        importFactory(reader, loaderContext);
        importWSDL(reader, loaderContext);
        LoaderUtil.skipToEndElement(reader);
        return null;
    }

    private void importFactory(XMLStreamReader reader, DeploymentContext loaderContext) throws LoaderException {
        String factoryName = reader.getAttributeValue(null, "factory");
        if (factoryName != null) {
            ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            try {
                // set TCCL as SDO needs it
                ClassLoader cl = loaderContext.getClassLoader();
                Thread.currentThread().setContextClassLoader(cl);
                Class<?> factoryClass = cl.loadClass(factoryName);
                SDOUtil.registerStaticTypes(factoryClass);
            } catch (ClassNotFoundException e) {
                throw new LoaderException(e.getMessage(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }

    private void importWSDL(XMLStreamReader reader, DeploymentContext loaderContext) throws LoaderException {
        String wsdLLocation = reader.getAttributeValue(null, "wsdlLocation");
        if (wsdLLocation != null) {
            URL wsdlURL = loaderContext.getClassLoader().getResource(wsdLLocation);
            try {
                InputStream xsdInputStream = wsdlURL.openStream();
                try {
                    // TODO: How do we get the associated TypeHelper for the given DeploymentContext?
                    TypeHelper typeHelper = TypeHelper.INSTANCE;
                    XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);
                    xsdHelper.define(xsdInputStream, null);
                } finally {
                    xsdInputStream.close();
                }
            } catch (IOException e) {
                LoaderException sfe = new LoaderException(e.getMessage());
                sfe.setResourceURI(wsdLLocation);
                throw sfe;
            }
        }
    }
}
