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

import org.apache.tuscany.assembly.AbstractContract;
import org.apache.tuscany.assembly.AbstractProperty;
import org.apache.tuscany.assembly.AbstractReference;
import org.apache.tuscany.assembly.AbstractService;
import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.assembly.xml.Loader;
import org.apache.tuscany.assembly.xml.LoaderRegistry;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.sca.idl.Interface;
import org.apache.tuscany.sca.idl.Operation;

/**
 * A contrainingType content handler.
 * 
 * @version $Rev$ $Date$
 */
public class ConstrainingTypeLoader extends BaseLoader implements Loader<ConstrainingType> {
    private AssemblyFactory factory;
    private LoaderRegistry registry;

    /**
     * Construct a new constrainingType loader.
     * @param factory
     * @param policyFactory
     * @param registry
     */
    public ConstrainingTypeLoader(AssemblyFactory factory, PolicyFactory policyFactory, LoaderRegistry registry) {
        super(factory, policyFactory);
        this.factory = factory;
        this.registry = registry;
    }

    public ConstrainingType load(XMLStreamReader reader) throws XMLStreamException {
        ConstrainingType constrainingType = null;
        AbstractService abstractService = null;
        AbstractReference abstractReference = null;
        AbstractProperty abstractProperty = null;
        AbstractContract abstractContract = null;
        QName name = null;
        
        // Read the constrainingType document
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {

                case START_ELEMENT:
                    name = reader.getName();
                    
                    // Read a <constrainingType>
                    if (Constants.CONSTRAINING_TYPE_QNAME.equals(name)) {
                        constrainingType = factory.createConstrainingType();
                        constrainingType.setName(getQName(reader, Constants.NAME));
                        readIntents(constrainingType, reader);

                    } else if (Constants.SERVICE_QNAME.equals(name)) {
                        
                        // Read a <service>
                        abstractService = factory.createAbstractService();
                        abstractContract = abstractService;
                        abstractService.setName(getString(reader, Constants.NAME));
                        constrainingType.getServices().add(abstractService);
                        readIntents(abstractService, reader);

                    } else if (Constants.REFERENCE_QNAME.equals(name)) {
                        
                        // Read a <reference>
                        abstractReference = factory.createAbstractReference();
                        abstractContract = abstractReference;
                        abstractReference.setName(getString(reader, Constants.NAME));
                        readMultiplicity(abstractReference, reader);
                        constrainingType.getReferences().add(abstractReference);
                        readIntents(abstractReference, reader);

                    } else if (Constants.PROPERTY_QNAME.equals(name)) {
                        
                        // Read a <property>
                        abstractProperty = factory.createAbstractProperty();
                        readAbstractProperty(abstractProperty, reader);
                        constrainingType.getProperties().add(abstractProperty);
                        readIntents(abstractProperty, reader);
                        
                    } else if (OPERATION.equals(name)) {

                        // Read an <operation>
                        Operation operation = factory.createOperation();
                        operation.setName(getString(reader, NAME));
                        operation.setUnresolved(true);
                        readIntents(abstractContract, operation, reader);
                        
                    } else {

                        // Read an extension element
                        Object extension = registry.load(reader);
                        if (extension instanceof Interface) {
                            // <service><interface> and <reference><interface>
                            abstractContract.setInterface((Interface)extension);
                        }
                    }
                    break;

                case END_ELEMENT:
                    name = reader.getName();

                    // Clear current state when reading reaching end element
                    if (SERVICE_QNAME.equals(name)) {
                        abstractService = null;
                        abstractContract = null;
                    } else if (REFERENCE_QNAME.equals(name)) {
                        abstractReference = null;
                        abstractContract = null;
                    } else if (PROPERTY_QNAME.equals(name)) {
                        abstractProperty = null;
                    }
                    break;
            }
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return constrainingType;

    }

}
