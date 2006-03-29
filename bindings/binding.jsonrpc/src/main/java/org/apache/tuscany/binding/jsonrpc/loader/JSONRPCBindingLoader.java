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
package org.apache.tuscany.binding.jsonrpc.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCBinding;
import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCAssemblyFactory;
import org.apache.tuscany.binding.jsonrpc.assembly.impl.JSONRPCAssemblyFactoryImpl;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JSONRPCBindingLoader implements StAXElementLoader<JSONRPCBinding> {
    public static final QName BINDING_ISONRPC = new QName("http://org.apache.tuscany/xmlns/jsonrpc/0.9", "binding.jsonrpc");

    private static final JSONRPCAssemblyFactory jsonFactory = new JSONRPCAssemblyFactoryImpl();

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
        return BINDING_ISONRPC;
    }

    public Class<JSONRPCBinding> getModelType() {
        return JSONRPCBinding.class;
    }

    public JSONRPCBinding load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        JSONRPCBinding binding = jsonFactory.createJSONRPCBinding();
        binding.setURI(reader.getAttributeValue(null, "uri"));
        return binding;
    }
}
