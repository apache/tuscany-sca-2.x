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
package org.apache.tuscany.binding.rmi.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.binding.rmi.assembly.RMIAssemblyFactory;
import org.apache.tuscany.binding.rmi.assembly.RMIBinding;
import org.apache.tuscany.binding.rmi.assembly.impl.RMIAssemblyFactoryImpl;
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
public class RMIBindingLoader implements StAXElementLoader<RMIBinding> {
    public static final QName BINDING_RMI = new QName("http://www.osoa.org/xmlns/sca/0.9", "binding.rmi");

    private static final RMIAssemblyFactory RMI_FACTORY = new RMIAssemblyFactoryImpl();

    protected StAXLoaderRegistry registry;

    @Autowire
    public void setRegistry(StAXLoaderRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void start() {
        registry.registerLoader(BINDING_RMI, this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(BINDING_RMI, this);
    }

    @SuppressWarnings("deprecation")
    public RMIBinding load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {

        RMIBinding binding = RMI_FACTORY.createRMIBinding();
        binding.setRMIHostName(reader.getAttributeValue(null, "host"));
        binding.setRMIServerName(reader.getAttributeValue(null, "server"));
        binding.setRMIPort(reader.getAttributeValue(null, "port"));
        binding.setTypeHelper(registry.getContext().getTypeHelper());
        binding.setResourceLoader(loaderContext.getResourceLoader());
        return binding;
    }
}
