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
package org.apache.tuscany.spi.marshaller;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;

/**
 * A registry for model object marshallers.
 * 
 * @version $Rev$ $Date$
 */
public interface ModelMarshallerRegistry {

    /**
     * Registers a model object marshaller.
     * 
     * @param <MD> Model object type.
     * @param modelClass Model obejct class.
     * @param qname Qualified name of the root element of the marshalled XML.
     * @param marshaller Model object marshaller.
     */
    <MD extends ModelObject> void registerMarshaller(Class<MD> modelClass, QName qname, ModelMarshaller<MD> marshaller);

    /**
     * Gets a marshaller for marshalling.
     * 
     * @param <MD> Model object type.
     * @param modelClass Model obejct class.
     * @return Model object marshaller.
     */
    <MD extends ModelObject> ModelMarshaller<MD> getMarshaller(Class<MD> modelClass);
    
    /**
     * Gets a marshaller for unmarshalling.
     * 
     * @param <MD> Model object type.
     * @param qname Qualified name of the root element of the marshalled XML.
     * @return Model object marshaller.
     */
    <MD extends ModelObject> ModelMarshaller<MD> getMarshaller(QName qname);

    /**
     * Marshalls a physical change set.
     * 
     * @param changeSet Physical chaneg set to be marshalled.
     * @param writer Writer to which marshalled information is written.
     */
    void marshall(PhysicalChangeSet changeSet, XMLStreamWriter writer);

    /**
     * Unmarshalls an XML stream to a physical change set.
     * 
     * @param reader Reader from which marshalled information is read.
     * @return Physical chnage set from the marshalled stream.
     */
    PhysicalChangeSet unmarshall(XMLStreamReader reader);

}
