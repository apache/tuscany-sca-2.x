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
     * Marshalls a model object.
     * 
     * @param modelObject Model object to be marshalled.
     * @param writer Writer to which marshalled information is written.
     */
    void marshall(ModelObject modelObject, XMLStreamWriter writer) throws MarshalException;

    /**
     * Unmarshalls an XML stream to a model object.
     * 
     * @param reader Reader from which marshalled information is read.
     * @return Model object from the marshalled stream.
     */
    ModelObject unmarshall(XMLStreamReader reader) throws MarshalException;

}
