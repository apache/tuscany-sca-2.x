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
package org.apache.tuscany.binding.ejb;

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

public class EJBBindingLoader extends LoaderExtension {
    public QName getXMLType() {
        return EJBBindingDefinition.BINDING_EJB;
    }

    public EJBBindingLoader(@Autowire
    LoaderRegistry registry) {
        super(registry);
    }

    public EJBBindingDefinition load(CompositeComponent composite,
                           ModelObject object,
                           XMLStreamReader reader,
                           DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        // construct EJBBinding model in memory
        EJBBindingDefinition binding = new EJBBindingDefinition();
        binding.setURI(reader.getAttributeValue(null, "uri"));
        binding.setHomeInterface(reader.getAttributeValue(null, "homeInterface"));
        binding.setEjbLinkName(reader.getAttributeValue(null, "ejb-link-name"));
        return binding;
    }
}
