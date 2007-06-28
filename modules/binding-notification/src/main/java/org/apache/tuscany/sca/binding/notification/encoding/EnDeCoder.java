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
package org.apache.tuscany.sca.binding.notification.encoding;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @version $Rev$ $Date$
 */
public interface EnDeCoder<E extends EncodingObject> {

    /**
     * Encodes an object to the specified stream writer.
     * 
     * @param encodingObject Object to be serialized.
     * @param writer Stream writer to which the infoset is serialized.
     * @throws EncodingException In case of any encoding error.
     */
    void encode(E encodingObject, XMLStreamWriter writer) throws EncodingException;

    /**
     * Decodes an XML stream to an object.
     * 
     * @param reader XML stream from where the encoded XML is read.
     * @return Encoding object.
     * @throws EncodingException In case of any encoding error.
     */
    E decode(XMLStreamReader reader) throws EncodingException;
}
