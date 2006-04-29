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
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidRootElementException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.config.SidefileLoadException;
import org.apache.tuscany.core.extension.config.ImplementationProcessor;
import org.apache.tuscany.core.config.impl.Java5ComponentTypeIntrospector;
import org.apache.tuscany.core.config.processor.ProcessorUtils;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.loader.assembly.AssemblyConstants;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

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

    @Autowire
    public void setRegistry(StAXLoaderRegistry registry) {
        this.registry = registry;
    }

    @Autowire
    public void setFactory(JavaAssemblyFactory factory) {
        this.factory = factory;
        introspector = new Java5ComponentTypeIntrospector(factory);
        //FIXME JFM HACK
        List<ImplementationProcessor> processors = ProcessorUtils.createCoreProcessors(factory);
        for (ImplementationProcessor processor : processors) {
            introspector.registerProcessor(processor);
        }
        // END hack
    }

    @Init(eager = true)
    public void start() {
        registry.registerLoader(IMPLEMENTATION_JAVA, this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(IMPLEMENTATION_JAVA, this);
    }

    public JavaImplementation load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        JavaImplementation javaImpl = factory.createJavaImplementation();
        String typeName = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = getImplementationClass(loaderContext.getResourceLoader(), typeName);
        javaImpl.setImplementationClass(implementationClass);
        javaImpl.setComponentInfo(loadComponentType(loaderContext, implementationClass));
        return javaImpl;
    }

    protected Class<?> getImplementationClass(ResourceLoader resourceLoader, String typeName) throws ConfigurationLoadException {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        try {
            // set TCCL in case the application code needs it
            Thread.currentThread().setContextClassLoader(resourceLoader.getClassLoader());
            return resourceLoader.loadClass(typeName);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationLoadException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
    }

    protected ComponentInfo loadComponentType(LoaderContext loaderContext, Class<?> implClass) throws ConfigurationLoadException, XMLStreamException {
        String baseName = JavaIntrospectionHelper.getBaseName(implClass);
        URL sidefile = implClass.getResource(baseName + ".componentType");
        if (sidefile == null) {
            return loadComponentTypeByIntrospection(implClass);
        } else {
            return loadComponentTypeFromSidefile(sidefile, loaderContext);
        }
    }

    protected ComponentInfo loadComponentTypeByIntrospection(Class<?> implClass) throws ConfigurationLoadException {
        return introspector.introspect(implClass);
    }

    protected ComponentInfo loadComponentTypeFromSidefile(URL sidefile, LoaderContext loaderContext) throws SidefileLoadException {
        try {
            XMLStreamReader reader;
            InputStream is;
            is = sidefile.openStream();
            try {
                reader = xmlFactory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    if (!AssemblyConstants.COMPONENT_TYPE.equals(reader.getName())) {
                        InvalidRootElementException e = new InvalidRootElementException(AssemblyConstants.COMPONENT_TYPE, reader.getName());
                        e.setResourceURI(sidefile.toString());
                        throw e;
                    }
                    return (ComponentInfo) registry.load(reader, loaderContext);
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
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(sidefile.toString());
            throw sfe;
        } catch (XMLStreamException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(sidefile.toString());
            throw sfe;
        } catch (ConfigurationLoadException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(sidefile.toString());
            throw sfe;
        }
    }
}
