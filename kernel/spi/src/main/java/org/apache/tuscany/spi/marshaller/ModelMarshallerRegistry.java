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

import org.apache.tuscany.spi.model.ModelObject;

/**
 * A registry of model marshallers.
 * 
 * @version $Rev$ $Date$
 */
public interface ModelMarshallerRegistry {

    /**
     * Registers a marshaller.
     * 
     * @param <MD> Model object type.
     * @param qname Qualified name of the marshalled xml data.
     * @param modelObjectClass Model object class.
     * @param modelMarshaller Model object marshaller.
     */
    <MD extends ModelObject> void registerMarshaller(QName qname,
                                                     Class<MD> modelObjectClass,
                                                     ModelMarshaller<MD> modelMarshaller);
    
    /**
     * Gets a model object for marshalling the specified type.
     * 
     * @param <MD> Model object type.
     * @param modelObjectClass Model object class.
     * @param modelMarshaller Model object marshaller.
     */
    <MD extends ModelObject> ModelMarshaller<MD> getMarshaller(Class<MD> modelObjectClass);
    
    /**
     * Gets a model object for unmarshalling the specified type.
     * 
     * @param <MD> Model object type.
     * @param qname Model object qualified name.
     * @param modelMarshaller Model object marshaller.
     */
    <MD extends ModelObject> ModelMarshaller<MD> getMarshaller(QName qname);

}
