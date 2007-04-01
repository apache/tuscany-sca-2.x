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

import org.apache.tuscany.assembly.model.AbstractContract;
import org.apache.tuscany.assembly.model.AbstractProperty;
import org.apache.tuscany.assembly.model.AbstractReference;
import org.apache.tuscany.assembly.model.AbstractService;
import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.scdl.stax.Constants;
import org.apache.tuscany.scdl.stax.Loader;
import org.apache.tuscany.scdl.stax.LoaderRegistry;

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
    public ConstrainingTypeLoader(AssemblyFactory factory,
                                  PolicyFactory policyFactory,
                                  LoaderRegistry registry) {
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
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    QName name = reader.getName();
                    if (Constants.CONSTRAINING_TYPE_QNAME.equals(name)) {
                        constrainingType = factory.createConstrainingType();
                        constrainingType.setName(getQName(reader, Constants.NAME));
                        readIntents(constrainingType, reader);

                    } else if (Constants.SERVICE_QNAME.equals(name)) {
                        abstractService = factory.createAbstractService();
                        abstractContract = abstractService;
                        abstractService.setName(getString(reader, Constants.NAME));
                        constrainingType.getServices().add(abstractService);
                        readIntents(abstractService, reader);

                    } else if (Constants.REFERENCE_QNAME.equals(name)) {
                        abstractReference = factory.createAbstractReference();
                        abstractContract = abstractReference;
                        abstractReference.setName(getString(reader, Constants.NAME));
                        constrainingType.getReferences().add(abstractReference);
                        readIntents(abstractReference, reader);

                    } else if (Constants.PROPERTY_QNAME.equals(name)) {
                        abstractProperty = factory.createAbstractProperty();
                        readAbstractProperty(abstractProperty, reader);
                        constrainingType.getProperties().add(abstractProperty);
                        readIntents(abstractProperty, reader);

                    }
            }
            if (reader.hasNext()) {
                reader.next();
            }

        }
        return constrainingType;

    }

}
