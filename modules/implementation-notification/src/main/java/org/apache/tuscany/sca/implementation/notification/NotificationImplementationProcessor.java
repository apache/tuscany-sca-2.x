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
package org.apache.tuscany.sca.implementation.notification;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWireException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * Loader for handling Notification <implementation.notification> elements.
 * 
 * @version $Rev$ $Date$
 */
public class NotificationImplementationProcessor implements StAXArtifactProcessor<NotificationImplementationImpl> {

    protected static final QName IMPLEMENTATION_NOTIFICATION =
        new QName(Constants.SCA10_TUSCANY_NS, "implementation.notification");
    
    private NotificationImplementationFactory implementationFactory;

    public NotificationImplementationProcessor(NotificationImplementationFactory implementationFactory) {
        this.implementationFactory = implementationFactory;
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_NOTIFICATION;
    }
    
    public Class<NotificationImplementationImpl> getModelType() {
        return NotificationImplementationImpl.class;
    }

    public NotificationImplementationImpl read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert IMPLEMENTATION_NOTIFICATION.equals(reader.getName());
        String componentTypeName = reader.getAttributeValue(null, "name");
        String implementationType = reader.getAttributeValue(null, "type");

        NotificationImplementationImpl implementation = implementationFactory.createNotificationImplementation();
        implementation.setComponentTypeName(componentTypeName);
        implementation.setImplementationType(implementationType);
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_NOTIFICATION.equals(reader.getName())) {
                break;
            }
        }
        
        ComponentType componentType = new DefaultAssemblyFactory().createComponentType();
        componentType.setURI(componentTypeName + ".componentType");
        componentType.setUnresolved(true);
        implementation.setComponentType(componentType);
        
        return implementation;
    }

    public void resolve(NotificationImplementationImpl impl, ModelResolver resolver) throws ContributionResolveException {
        ComponentType componentType = resolver.resolveModel(ComponentType.class, impl.getComponentType());
        
        if (componentType.isUnresolved()) {
            throw new ContributionResolveException("ComponentType still unresolved");
        }
        impl.setComponentType(componentType);
        impl.setUnresolved(false);
    }
        
    public void wire(NotificationImplementationImpl model) throws ContributionWireException {
    }
        
    public void write(NotificationImplementationImpl model, XMLStreamWriter outputSource)
            throws ContributionWriteException, XMLStreamException {
        
        //FIXME Implement this method
    }
}
