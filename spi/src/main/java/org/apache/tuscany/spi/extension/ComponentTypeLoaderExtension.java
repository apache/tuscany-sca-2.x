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
package org.apache.tuscany.spi.extension;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.spi.loader.ComponentTypeLoader;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * @version $Rev$ $Date$
 */
public abstract class ComponentTypeLoaderExtension<I extends Implementation> implements ComponentTypeLoader<I> {
    protected LoaderRegistry loaderRegistry;

    protected ComponentTypeLoaderExtension() {
    }

    protected ComponentTypeLoaderExtension(LoaderRegistry loaderRegistry) {
        this.loaderRegistry = loaderRegistry;
    }

    @Property
    public void setLoaderRegistry(LoaderRegistry loaderRegistry) {
        this.loaderRegistry = loaderRegistry;
    }

    protected abstract Class<I> getImplementationClass();

    @Init
    public void start() {
        loaderRegistry.registerLoader(getImplementationClass(), this);
    }

    @Destroy
    public void stop() {
        loaderRegistry.unregisterLoader(getImplementationClass());
    }

    protected <CT extends ComponentType> CT loadFromSidefile(Class<CT> type, URL sidefile, DeploymentContext deploymentContext) throws LoaderException {
        try {
            XMLStreamReader reader;
            InputStream is;
            is = sidefile.openStream();
            try {
                XMLInputFactory factory = deploymentContext.getXmlFactory();
                reader = factory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    QName name = reader.getName();
                    ModelObject mo = loaderRegistry.load(reader, deploymentContext);
                    if (type.isInstance(mo)) {
                        return type.cast(mo);
                    } else {
                        UnrecognizedElementException e = new UnrecognizedElementException(name);
                        e.setResourceURI(sidefile.toString());
                        throw e;
                    }
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

}
