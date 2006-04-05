/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.container.java.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.config.impl.Java5ComponentTypeIntrospector;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.loader.assembly.AssemblyConstants;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.ComponentType;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JavaImplementationLoader implements StAXElementLoader<JavaImplementation> {
    public static final QName IMPLEMENTATION_JAVA = new QName("http://www.osoa.org/xmlns/sca/0.9", "implementation.java");

    private StAXLoaderRegistry registry;
    private XMLInputFactory xmlFactory;

    private JavaAssemblyFactory factory;
    private ComponentTypeIntrospector introspector;

    public JavaImplementationLoader() {
        // todo make this a reference to a system service
        xmlFactory = XMLInputFactory.newInstance();
    }

    @Autowire(required = true)
    public void setRegistry(StAXLoaderRegistry registry) {
        this.registry = registry;
    }

    @Autowire(required = true)
    public void setFactory(JavaAssemblyFactory factory) {
        this.factory = factory;
        introspector = new Java5ComponentTypeIntrospector(factory);
    }

    @Init(eager = true)
    public void start() {
        registry.registerLoader(this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(this);
    }

    public QName getXMLType() {
        return IMPLEMENTATION_JAVA;
    }

    public Class<JavaImplementation> getModelType() {
        return JavaImplementation.class;
    }

    public JavaImplementation load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        JavaImplementation javaImpl = factory.createJavaImplementation();
        String typeName = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = getImplementationClass(resourceLoader, typeName);
        javaImpl.setImplementationClass(implementationClass);
        javaImpl.setComponentType(loadComponentType(resourceLoader, implementationClass));
        return javaImpl;
    }

    protected Class<?> getImplementationClass(ResourceLoader resourceLoader, String typeName) throws ConfigurationLoadException {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        try {
            // set TCCL in case the application code needs it
            Thread.currentThread().setContextClassLoader(resourceLoader.getClassLoader());
            return resourceLoader.loadClass(typeName);
        } catch (ClassNotFoundException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
    }

    protected ComponentType loadComponentType(ResourceLoader loader, Class<?> implClass) throws ConfigurationLoadException, XMLStreamException {
        String baseName = JavaIntrospectionHelper.getBaseName(implClass);
        URL sidefile = implClass.getResource(baseName + ".componentType");
        if (sidefile == null) {
            return loadComponentTypeByIntrospection(implClass);
        } else {
            return loadComponentTypeFromSidefile(sidefile, loader);
        }
    }

    protected ComponentType loadComponentTypeByIntrospection(Class<?> implClass) throws ConfigurationLoadException {
        try {
            return introspector.introspect(implClass);
        } catch (ConfigurationException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
        }
    }

    protected ComponentType loadComponentTypeFromSidefile(URL sidefile, ResourceLoader loader) throws ConfigurationLoadException, XMLStreamException {
        XMLStreamReader reader;
        InputStream is;
        try {
            is = sidefile.openStream();
        } catch (IOException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
        }
        try {
            try {
                reader = xmlFactory.createXMLStreamReader(is);
            } catch (XMLStreamException e) {
                throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
            }
            try {
                reader.nextTag();
                if (!AssemblyConstants.COMPONENT_TYPE.equals(reader.getName())) {
                    throw new ConfigurationLoadException(sidefile + " is not a <componentType> document");
                }
                return (ComponentType) registry.load(reader, loader);
            } finally{
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    // ignore
                }
            }
        } finally{
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
