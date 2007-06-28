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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @version $Rev$ $Date$
 */
public interface EncodingRegistry {

    /**
     * Registers an en/de coder.
     * 
     * @param <E> Encoding object type.
     * @param encodingClass Encoding obejct class.
     * @param qname Qualified name of the root element of the encoded XML.
     * @param enDeCoder Encoding object enDeCoder.
     */
    <E extends EncodingObject> void registerEnDeCoder(Class<E> encodingClass, QName qname, EnDeCoder<E> enDeCoder);

    <E extends EncodingObject> void unregisterEnDeCoder(Class<E> encodingClass, QName qname);
    
    /**
     * Encodes an object.
     * 
     * @param encodingObject Encoding object to be encoded.
     * @param writer Writer to which encoded information is written.
     */
    void encode(EncodingObject encodingObject, XMLStreamWriter writer) throws EncodingException;

    /**
     * Decodes an XML stream to an encoding object.
     * 
     * @param reader Reader from which encoded information is read.
     * @return Encoding object from the encoded stream.
     */
    EncodingObject decode(XMLStreamReader reader) throws EncodingException;
}
