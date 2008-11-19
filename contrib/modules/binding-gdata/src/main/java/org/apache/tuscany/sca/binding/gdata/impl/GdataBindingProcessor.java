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
package org.apache.tuscany.sca.binding.gdata.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.binding.gdata.GdataBinding;
import org.apache.tuscany.sca.binding.gdata.GdataBindingFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * A processor for <binding.gdata> elements.
 * 
 * @version $Rev$ $Date$
 */
public class GdataBindingProcessor implements StAXArtifactProcessor<GdataBinding> {

    private QName BINDING_GDATA = new QName("http://tuscany.apache.org/xmlns/sca/1.0", "binding.gdata");
    
    private final GdataBindingFactory factory;
    
    public GdataBindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(GdataBindingFactory.class);
        System.out.println("[Debug Info]GdataBindingProcessor reached");
    }

    public QName getArtifactType() {
        return BINDING_GDATA;
    }

    public Class<GdataBinding> getModelType() {
        return GdataBinding.class;
    }

    public GdataBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
    	GdataBinding gdataBinding = factory.createGdataBinding();

    	String name = reader.getAttributeValue(null, "name");
    	if (name != null) {
    		gdataBinding.setName(name);
    	}

    	String uri = reader.getAttributeValue(null, "uri");
    	if (uri != null) {
    		gdataBinding.setURI(uri);
    	}

    	String serviceType = reader.getAttributeValue(null, "serviceType");
    	if (serviceType != null) {
    		gdataBinding.setServiceType(serviceType);
    	}

    	String username = reader.getAttributeValue(null, "username");
    	if (username != null) {
    		gdataBinding.setUsername(username);
    	}

    	String password = reader.getAttributeValue(null, "password");
    	if (password != null) {
    		gdataBinding.setPassword(password);
    	}        


    	return gdataBinding;
    }
    
    public void write(GdataBinding gdataBinding, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
    	writer.writeStartElement(BINDING_GDATA.getNamespaceURI(), BINDING_GDATA.getLocalPart());

    	if (gdataBinding.getName() != null) {
    		writer.writeAttribute("name", gdataBinding.getName());
    	}

    	if (gdataBinding.getURI() != null) {
    		writer.writeAttribute("uri", gdataBinding.getURI());
    	}

    	if (gdataBinding.getServiceType() != null) {
    		writer.writeAttribute("serviceType", gdataBinding.getServiceType());
    	}

    	if (gdataBinding.getUsername() != null) {
    		writer.writeAttribute("username", gdataBinding.getUsername());
    	}

    	if (gdataBinding.getPassword() != null) {
    		writer.writeAttribute("password", gdataBinding.getPassword());
    	}

    	writer.writeEndElement();
    }

    
    public void resolve(GdataBinding gdataBinding, ModelResolver resolver) throws ContributionResolveException {

    }
}
