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

package org.apache.tuscany.scdl.stax.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.Base;
import org.apache.tuscany.assembly.model.Callback;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.ComponentType;
import org.apache.tuscany.assembly.model.Contract;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Reference;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.scdl.stax.Constants;
import org.apache.tuscany.scdl.stax.Loader;
import org.apache.tuscany.scdl.stax.LoaderRegistry;

/**
 * A componentType content handler.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoader extends BaseLoader implements Loader<ComponentType> {

    public ComponentTypeLoader(AssemblyFactory factory, PolicyFactory policyFactory, LoaderRegistry registry) {
        super(factory, policyFactory, registry);
    }

    public ComponentType load(Base parent, XMLStreamReader reader) throws XMLStreamException {
        ComponentType componentType = null;
        Service service = null;
        Reference reference = null;
        Contract contract = null;
        Property property = null;
        Callback callback = null;
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    QName name = reader.getName();

                    if (Constants.COMPONENT_TYPE_QNAME.equals(name)) {
                        componentType = factory.createComponentType();
                        componentType.setConstrainingType(getConstrainingType(reader));
                        readRequiredIntents(componentType, reader);
                        readPolicySets(componentType, reader);

                    } else if (Constants.SERVICE_QNAME.equals(name)) {
                        service = factory.createService();
                        contract = service;
                        service.setName(getString(reader, Constants.NAME));
                        componentType.getServices().add(service);
                        readRequiredIntents(service, reader);
                        readPolicySets(service, reader);

                    } else if (Constants.REFERENCE_QNAME.equals(name)) {
                        reference = factory.createReference();
                        contract = reference;
                        reference.setName(getString(reader, Constants.NAME));

                        // TODO support multivalued attribute
                        ComponentService target = factory.createComponentService();
                        target.setUnresolved(true);
                        target.setName(getString(reader, Constants.TARGET));
                        reference.getTargets().add(target);

                        componentType.getReferences().add(reference);
                        readRequiredIntents(reference, reader);
                        readPolicySets(reference, reader);

                    } else if (Constants.PROPERTY_QNAME.equals(name)) {
                        property = factory.createProperty();
                        readProperty(property, reader);
                        componentType.getProperties().add(property);
                        readRequiredIntents(property, reader);
                        readPolicySets(property, reader);

                    } else if (Constants.CALLBACK_QNAME.equals(name)) {
                        callback = factory.createCallback();
                        contract.setCallback(callback);
                        readRequiredIntents(callback, reader);
                        readPolicySets(callback, reader);

                    }
            }
            if (reader.hasNext()) {
                reader.next();
            }

        }
        return componentType;
    }
}
