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

package org.apache.tuscany.assembly.xml.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.Binding;
import org.apache.tuscany.assembly.model.Callback;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.ComponentType;
import org.apache.tuscany.assembly.model.Contract;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Reference;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.assembly.xml.Loader;
import org.apache.tuscany.assembly.xml.LoaderRegistry;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.sca.idl.Interface;
import org.apache.tuscany.sca.idl.Operation;

/**
 * A componentType loader.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoader extends BaseLoader implements Loader<ComponentType> {
    private AssemblyFactory factory;
    private LoaderRegistry registry;

    /**
     * Constructs a new componentType loader.
     * 
     * @param factory
     * @param policyFactory
     * @param registry
     */
    public ComponentTypeLoader(AssemblyFactory factory, PolicyFactory policyFactory, LoaderRegistry registry) {
        super(factory, policyFactory);
        this.factory = factory;
        this.registry = registry;
    }

    public ComponentType load(XMLStreamReader reader) throws XMLStreamException {
        ComponentType componentType = null;
        Service service = null;
        Reference reference = null;
        Contract contract = null;
        Property property = null;
        Callback callback = null;
        QName name = null;

        // Read the componentType document
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    name = reader.getName();

                    if (Constants.COMPONENT_TYPE_QNAME.equals(name)) {

                        // Read a <componentType>
                        componentType = factory.createComponentType();
                        componentType.setConstrainingType(getConstrainingType(reader));
                        readPolicies(componentType, reader);

                    } else if (Constants.SERVICE_QNAME.equals(name)) {

                        // Read a <service>
                        service = factory.createService();
                        contract = service;
                        service.setName(getString(reader, Constants.NAME));
                        componentType.getServices().add(service);
                        readPolicies(service, reader);

                    } else if (Constants.REFERENCE_QNAME.equals(name)) {

                        // Read a <reference>
                        reference = factory.createReference();
                        contract = reference;
                        reference.setName(getString(reader, Constants.NAME));
                        readMultiplicity(reference, reader);

                        // TODO support multivalued attribute
                        ComponentService target = factory.createComponentService();
                        target.setUnresolved(true);
                        target.setName(getString(reader, Constants.TARGET));
                        reference.getTargets().add(target);

                        componentType.getReferences().add(reference);
                        readPolicies(reference, reader);

                    } else if (Constants.PROPERTY_QNAME.equals(name)) {

                        // Read a <property>
                        property = factory.createProperty();
                        readProperty(property, reader);
                        componentType.getProperties().add(property);
                        readPolicies(property, reader);

                    } else if (Constants.CALLBACK_QNAME.equals(name)) {

                        // Read a <callback>
                        callback = factory.createCallback();
                        contract.setCallback(callback);
                        readPolicies(callback, reader);

                    } else if (OPERATION.equals(name)) {

                        // Read an <operation>
                        Operation operation = factory.createOperation();
                        operation.setName(getString(reader, NAME));
                        operation.setUnresolved(true);
                        if (callback != null) {
                            readPolicies(callback, operation, reader);
                        } else {
                            readPolicies(contract, operation, reader);
                        }
                    } else {

                        // Read an extension element
                        Object extension = registry.load(reader);
                        if (extension != null) {
                            if (extension instanceof Interface) {

                                // <service><interface> and <reference><interface>
                                contract.setInterface((Interface)extension);

                            } else if (extension instanceof Binding) {

                                // <service><binding> and <reference><binding>
                                contract.getBindings().add((Binding)extension);
                            }
                        }
                    }
                    break;

                case END_ELEMENT:
                    name = reader.getName();

                    // Clear current state when reading reaching end element
                    if (SERVICE_QNAME.equals(name)) {
                        service = null;
                        contract = null;
                    } else if (REFERENCE_QNAME.equals(name)) {
                        reference = null;
                        contract = null;
                    } else if (PROPERTY_QNAME.equals(name)) {
                        property = null;
                    } else if (CALLBACK_QNAME.equals(name)) {
                        callback = null;
                    }
                    break;
            }
            
            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return componentType;
    }
}
