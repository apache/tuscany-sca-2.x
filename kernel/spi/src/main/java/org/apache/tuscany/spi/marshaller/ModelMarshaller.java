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

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Interface for marshalling/unmarshalling internal model objects.
 * 
 * @version $Rev$ $Date$
 *
 */
public interface ModelMarshaller<MD extends ModelObject> {
    
    /**
     * Marshalls the model object to the specified stream writer.
     * 
     * @param modelObject Model object to be serialized.
     * @param writer Stream writer to which the infoset is serialized.
     * @throws MarshalException In case of any marshalling error.
     */
    void marshall(MD modelObject, XMLStreamWriter writer) throws MarshalException;
    
    /**
     * Unmarshalls an XML stream to a model object.
     * 
     * @param reader XML stream from where the marshalled XML is read.
     * @param upconvert Whether to upconvert the object is the current runtime
     * supports a higher version of the model object.
     * @return Hydrated model object.
     * @throws MarshalException In case of any unmarshalling error.
     */
    MD unmarshall(XMLStreamReader reader, boolean upconvert) throws MarshalException;

}
