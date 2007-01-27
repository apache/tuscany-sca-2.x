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
package org.apache.tuscany.binding.jsonrpc;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.annotations.Scope;

/**
 * Loader for handling <binding.jsonrpc> elements.
 * 
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class JSONRPCBindingLoader extends LoaderExtension<JSONRPCBindingDefinition> {
    public static final QName BINDING_JSON = new QName(XML_NAMESPACE_1_0, "binding.jsonrpc");

    public JSONRPCBindingLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return BINDING_JSON;
    }

    public JSONRPCBindingDefinition load(CompositeComponent parent, ModelObject object, XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException,
            LoaderException {

        return new JSONRPCBindingDefinition();
    }
}
