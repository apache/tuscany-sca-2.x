/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.servicemix.sca.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.servicemix.sca.assembly.JbiAssemblyFactory;
import org.apache.servicemix.sca.assembly.JbiBinding;
import org.apache.servicemix.sca.assembly.impl.JbiAssemblyFactoryImpl;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXElementLoader;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("MODULE")
public class JbiBindingLoader implements StAXElementLoader<JbiBinding>{

    public static final QName BINDING_JBI = new QName("http://www.osoa.org/xmlns/sca/0.9", "binding.jbi");

    private static final JbiAssemblyFactory jbiFactory = new JbiAssemblyFactoryImpl();

    private StAXLoaderRegistry registry;

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
        return BINDING_JBI;
    }

    public Class<JbiBinding> getModelType() {
        return JbiBinding.class;
    }

    public JbiBinding load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        JbiBinding binding = jbiFactory.createJbiBinding();
        binding.setURI(reader.getAttributeValue(null, "uri"));
        binding.setPortURI(reader.getAttributeValue(null, "port"));
        return binding;
    }
}
