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

package org.apache.tuscany.sca.implementation.osgi.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.BindingDescriptions;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptionsFactory;
import org.apache.tuscany.sca.monitor.Monitor;

/*
<?xml version="1.0" encoding="UTF-8"?>
<bindings xmlns="http://www.osgi.org/xmlns/sd/v1.0.0" xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200903">
    <binding.ws/>
    <binding.sca/>
</bindings>
*/
public class BindingDescriptionsProcessor implements StAXArtifactProcessor<BindingDescriptions> {
    private Monitor monitor;
    private StAXArtifactProcessor processor;
    private ServiceDescriptionsFactory factory;

    public BindingDescriptionsProcessor(ExtensionPointRegistry registry,
                                        StAXArtifactProcessor processor,
                                        Monitor monitor) {
        this.monitor = monitor;
        this.processor = processor;
        this.factory =
            registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(ServiceDescriptionsFactory.class);
    }

    public BindingDescriptions read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        BindingDescriptions bindings = factory.createBindingDescriptions();
        boolean exit = false;
        while (!exit) {
            int event = reader.getEventType();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    if (!"bindings".equals(name.getLocalPart())) {
                        Object element = null;
                        try {
                            element = processor.read(reader);
                        } catch (ContributionReadException e) {
                            throw e;
                        }
                        if (element instanceof Binding) {
                            bindings.add((Binding)element);
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    name = reader.getName();
                    if ("bindings".equals(name.getLocalPart())) {
                        exit = true;
                    }
                    break;
            }
            if (reader.hasNext()) {
                reader.next();
            } else {
                exit = true;
            }
        }
        return bindings;
    }

    public QName getArtifactType() {
        return BindingDescriptions.BINDINGS_QNAME;
    }

    public void write(BindingDescriptions model, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        writer.writeStartElement(BindingDescriptions.OSGI_SD_NS, "bindings");
        for (Binding binding : model) {
            processor.write(model, writer);
        }
        writer.writeEndElement();
    }

    public Class<BindingDescriptions> getModelType() {
        return BindingDescriptions.class;
    }

    public void resolve(BindingDescriptions model, ModelResolver resolver) throws ContributionResolveException {
        // TODO: To be implemented
    }
}
