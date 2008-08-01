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
package org.apache.tuscany.sca.binding.gdata;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.monitor.Monitor;

public class GDataImplementationProcessor implements StAXArtifactProcessor<GDataBinding> {

    private GDataBindingFactory gdataFactory;
    protected static final QName BINDING_GDATA = new QName(Constants.SCA10_TUSCANY_NS, "binding.gdata");

    public GDataImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {

        this.gdataFactory = (GDataBindingFactory) modelFactories.getFactory(GDataBindingFactory.class);
    }

    public GDataBinding read(XMLStreamReader reader) {

        assert BINDING_GDATA.equals(reader.getName());

        GDataBinding gdataBinding = gdataFactory.createAtomBinding();

        /*
         * <tuscany:binding.gdata uri="..." 
         *  login="..." 
         *  password="..."/>           
         */

        String uri = reader.getAttributeValue(null, "uri");
        String serviceType = reader.getAttributeValue(null, "serviceType");
        String login = reader.getAttributeValue(null, "username");
        String password = reader.getAttributeValue(null, "password");

        gdataBinding.setURI(uri);
        gdataBinding.setServiceType(serviceType);
        gdataBinding.setUsername(login);
        gdataBinding.setPassword(password);

        return gdataBinding;
    }

    public void write(GDataBinding gdataBinding, XMLStreamWriter writer) throws XMLStreamException {
        if (gdataBinding != null) {
            writer.writeStartElement(BINDING_GDATA.getNamespaceURI(), BINDING_GDATA.getLocalPart());

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
    }

    public QName getArtifactType() {
        return BINDING_GDATA;
    }

    public void resolve(GDataBinding arg0, ModelResolver arg1) throws ContributionResolveException {
    }

    public Class<GDataBinding> getModelType() {
        return GDataBinding.class;
    }
}
