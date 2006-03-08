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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JavaImplementationLoader implements StAXElementLoader<JavaImplementation> {
    public static final QName IMPLEMENTATION_JAVA = new QName("http://www.osoa.org/xmlns/sca/0.9", "implementation.java");

    private static final JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();

    protected StAXLoaderRegistry registry;

    @Autowire
    public void setRegistry(StAXLoaderRegistry registry) {
        this.registry = registry;
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

    public Class getModelType() {
        return JavaImplementation.class;
    }

    public JavaImplementation load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        JavaImplementation javaImpl = factory.createJavaImplementation();
        String typeName = reader.getAttributeValue(null, "class");
        try {
            // todo the type information should not require loading of an application class, save until build time
            Class<?> type = resourceLoader.loadClass(typeName);
            javaImpl.setImplementationClass(type);
        } catch (ClassNotFoundException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
        }
        return javaImpl;
    }
}
