/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.container.java;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.loader.AssemblyConstants;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.ComponentType;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentTypeLoader extends ComponentTypeLoaderExtension<JavaImplementation> {

    public JavaComponentTypeLoader() {
        super();
    }

    public void load(JavaImplementation implementation, DeploymentContext deploymentContext) {
        Class<?> implClass = implementation.getImplementationClass();
        URL resource = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        try {
            if (resource == null) {
                loadByIntrospection(implementation);
            } else {
                loadFromSidefile(implementation, resource, deploymentContext);
            }
        } catch (LoaderException e) {
            // throw new TuscanyRuntimeException(e);
        }
    }

    protected void loadByIntrospection(JavaImplementation implementation) {
        Class<?> implClass = implementation.getImplementationClass();
        PojoComponentType componentType = null; // FIXME: introspector.introspect(implClass);
        implementation.setComponentType(componentType);
    }

    protected ComponentType loadFromSidefile(JavaImplementation implementation, URL sidefile, DeploymentContext deploymentContext)
            throws LoaderException {
        try {
            XMLStreamReader reader;
            InputStream is;
            is = sidefile.openStream();
            try {
                XMLInputFactory factory = deploymentContext.getXmlFactory();
                reader = factory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    if (!AssemblyConstants.COMPONENT_TYPE.equals(reader.getName())) {
                        UnrecognizedElementException e = new UnrecognizedElementException(reader.getName());
                        e.setResourceURI(sidefile.toString());
                        throw e;
                    }
                    return (ComponentType) loaderRegistry.load(reader, deploymentContext);
                } finally {
                    try {
                        reader.close();
                    } catch (XMLStreamException e) {
                        // ignore
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (IOException e) {
            LoaderException sfe = new LoaderException(e.getMessage());
            sfe.setResourceURI(sidefile.toString());
            throw sfe;
        } catch (XMLStreamException e) {
            LoaderException sfe = new LoaderException(e.getMessage());
            sfe.setResourceURI(sidefile.toString());
            throw sfe;
        }
    }

    @Override
    protected Class<JavaImplementation> getTypeClass() {
        return JavaImplementation.class;
    }

}
