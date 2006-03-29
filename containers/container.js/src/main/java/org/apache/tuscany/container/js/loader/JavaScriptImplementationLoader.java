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
package org.apache.tuscany.container.js.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.js.assembly.JavaScriptAssemblyFactory;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.assembly.impl.JavaScriptAssemblyFactoryImpl;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JavaScriptImplementationLoader implements StAXElementLoader<JavaScriptImplementation> {
    public static final QName IMPLEMENTATION_JS = new QName("http://org.apache.tuscany/xmlns/js/0.9", "implementation.js");

    private static final JavaScriptAssemblyFactory factory = new JavaScriptAssemblyFactoryImpl();

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
        return IMPLEMENTATION_JS;
    }

    public Class getModelType() {
        return JavaScriptImplementation.class;
    }

    public JavaScriptImplementation load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        JavaScriptImplementation jsImpl = factory.createJavaScriptImplementation();
        jsImpl.setScriptFile(reader.getAttributeValue(null, "scriptFile"));
        return jsImpl;
    }
}
