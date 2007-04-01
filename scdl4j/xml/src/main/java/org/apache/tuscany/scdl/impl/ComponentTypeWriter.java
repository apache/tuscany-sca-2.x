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

import org.apache.tuscany.assembly.model.ComponentType;
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
public class ComponentTypeWriter extends BaseWriter {

    private ComponentType componentType;

    public ComponentTypeWriter(ComponentType componentType) {
        this.componentType = componentType;
    }

    protected void write() throws SAXException {

        start(Constants.COMPONENT_TYPE, new Attr(Constants.CONSTRAINING_TYPE, getConstrainingType(componentType)));

        for (Service service : componentType.getServices()) {
            start(Constants.SERVICE, new Attr(Constants.NAME, service.getName()));
            if (service.getCallback() != null) {
                start(Constants.CALLBACK);
                end(Constants.CALLBACK);
            }
            end(Constants.SERVICE);
        }

        for (Reference reference : componentType.getReferences()) {
            // TODO handle multivalued target attribute
            String target = reference.getTargets().isEmpty() ? null : reference.getTargets().get(0).getName();
            start(Constants.REFERENCE,
                  new Attr(Constants.NAME, reference.getName()),
                  new Attr(Constants.TARGET, target));
            if (reference.getCallback() != null) {
                start(Constants.CALLBACK);
                end(Constants.CALLBACK);
            }
            end(Constants.REFERENCE);
        }

        for (Property property : componentType.getProperties()) {
            start(Constants.PROPERTY, new Attr(Constants.NAME, property.getName()));
            end(Constants.PROPERTY);
        }

        end(Constants.COMPONENT_TYPE);
    }

}
