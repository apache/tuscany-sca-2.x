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
package org.apache.tuscany.core.marshaller.extensions;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.AbstractMarshallerExtension;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Abstract marshaller that supports extensible model objects.
 * 
 * @version $Revision$ $Date: 2007-03-04 18:23:24 +0000 (Sun, 04 Mar
 *          2007) $
 * @param <MD>
 */
public abstract class AbstractExtensibleMarshallerExtension<MD extends ModelObject> extends
    AbstractMarshallerExtension<MD> {

    /**
     * Create the concrete model object.
     * 
     * @return Concrete model object.
     */
    protected abstract MD getConcreteModelObject();

    /**
     * Handles extensions for unmarshalling.
     * 
     * @param modelObject Concrete model object.
     * @param reader Reader from which marshalled data is read.
     */
    protected abstract void handleExtension(MD modelObject, XMLStreamReader reader) throws MarshalException;

    /**
     * Handles extensions for marshalling.
     * 
     * @param modelObject Concrete model object.
     * @param reader Writer to which marshalled data is written.
     */
    protected abstract void handleExtension(MD modelObject, XMLStreamWriter writer) throws MarshalException;

}
