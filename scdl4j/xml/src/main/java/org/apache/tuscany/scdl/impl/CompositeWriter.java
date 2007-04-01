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

package org.apache.tuscany.scdl.impl;

import org.apache.tuscany.assembly.model.Component;
import org.apache.tuscany.assembly.model.ComponentProperty;
import org.apache.tuscany.assembly.model.ComponentReference;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.Composite;
import org.apache.tuscany.assembly.model.CompositeReference;
import org.apache.tuscany.assembly.model.CompositeService;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Reference;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.util.Attr;
import org.apache.tuscany.scdl.util.BaseWriter;
import org.xml.sax.SAXException;

/**
 * A test handler to test the usability of the assembly model API when writing
 * SCDL
 * 
 * @version $Rev$ $Date$
 */
public class CompositeWriter extends BaseWriter {

    private Composite composite;

    public CompositeWriter(Composite composite) {
        this.composite = composite;
    }

    protected void write() throws SAXException {

        start(Constants.COMPOSITE, new Attr(Constants.CONSTRAINING_TYPE, getConstrainingType(composite)));

        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            ComponentService promotedService = compositeService.getPromotedService();
            String promote = promotedService != null ? promotedService.getName() : null;
            start(Constants.SERVICE, new Attr(Constants.NAME, service.getName()), new Attr(Constants.PROMOTE, promote));
            if (service.getCallback() != null) {
                start(Constants.CALLBACK);
                end(Constants.CALLBACK);
            }
            end(Constants.SERVICE);
        }

        for (Component component : composite.getComponents()) {
            start(Constants.COMPONENT, new Attr(Constants.NAME, component.getName()));

            for (ComponentService service : component.getServices()) {
                start(Constants.SERVICE, new Attr(Constants.NAME, service.getName()));
                end(Constants.SERVICE);
                if (service.getCallback() != null) {
                    start(Constants.CALLBACK);
                    end(Constants.CALLBACK);
                }
            }

            for (ComponentReference reference : component.getReferences()) {
                // TODO handle multivalued target attribute
                String target = reference.getTargets().isEmpty() ? null : reference.getTargets().get(0).getName();
                start(Constants.REFERENCE, new Attr(Constants.NAME, reference.getName()), new Attr(Constants.TARGET,
                                                                                                   target));
                if (reference.getCallback() != null) {
                    start(Constants.CALLBACK);
                    end(Constants.CALLBACK);
                }
                end(Constants.REFERENCE);
            }

            for (ComponentProperty property : component.getProperties()) {
                start(Constants.PROPERTY, new Attr(Constants.NAME, property.getName()));
                end(Constants.PROPERTY);
            }

            end(Constants.COMPONENT);
        }

        for (Reference reference : composite.getReferences()) {
            // TODO handle multivalued promote attribute
            CompositeReference compositeReference = (CompositeReference)reference;
            String promote;
            if (!compositeReference.getPromotedReferences().isEmpty())
                promote = compositeReference.getPromotedReferences().get(0).getName();
            else
                promote = null;
            start(Constants.REFERENCE, new Attr(Constants.NAME, reference.getName()), new Attr(Constants.PROMOTE,
                                                                                               promote));
            if (reference.getCallback() != null) {
                start(Constants.CALLBACK);
                end(Constants.CALLBACK);
            }
            end(Constants.REFERENCE);
        }

        for (Property property : composite.getProperties()) {
            start(Constants.PROPERTY, new Attr(Constants.NAME, property.getName()));
            end(Constants.PROPERTY);
        }

        end(Constants.COMPOSITE);
    }

}
