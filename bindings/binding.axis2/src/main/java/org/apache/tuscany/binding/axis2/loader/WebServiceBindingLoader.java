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
package org.apache.tuscany.binding.axis2.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.binding.axis2.assembly.WebServiceAssemblyFactory;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.assembly.impl.WebServiceAssemblyFactoryImpl;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class WebServiceBindingLoader implements StAXElementLoader<WebServiceBinding> {
    public static final QName BINDING_WS = new QName("http://www.osoa.org/xmlns/sca/0.9", "binding.ws");

    private static final WebServiceAssemblyFactory WS_FACTORY = new WebServiceAssemblyFactoryImpl();

    protected StAXLoaderRegistry registry;

    @Autowire
    public void setRegistry(StAXLoaderRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void start() {
        registry.registerLoader(BINDING_WS, this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(BINDING_WS, this);
    }

    @SuppressWarnings("deprecation")
    public WebServiceBinding load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {

        WebServiceBinding binding = WS_FACTORY.createWebServiceBinding();
        binding.setURI(reader.getAttributeValue(null, "uri"));
        binding.setPortURI(reader.getAttributeValue(null, "port"));
        binding.setTypeHelper(registry.getContext().getTypeHelper());
        binding.setResourceLoader(loaderContext.getResourceLoader());
        return binding;
    }
}
