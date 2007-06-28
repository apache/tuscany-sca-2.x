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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @version $Rev$ $Date$
 */
public class DefaultEncodingRegistry implements EncodingRegistry {

    private final Map<Class<? extends EncodingObject>, EnDeCoder> encoderRegistry =
        new ConcurrentHashMap<Class<? extends EncodingObject>, EnDeCoder>();

    private final Map<QName, EnDeCoder> decoderRegistry = new ConcurrentHashMap<QName, EnDeCoder>();
    
    public DefaultEncodingRegistry() {
    }
    
    public <E extends EncodingObject> void registerEnDeCoder(Class<E> encodingClass, QName qname, EnDeCoder<E> enDeCoder) {
        
        encoderRegistry.put(encodingClass, enDeCoder);
        decoderRegistry.put(qname, enDeCoder);
    }

    public <E extends EncodingObject> void unregisterEnDeCoder(Class<E> encodingClass, QName qname) {
        
        encoderRegistry.remove(encodingClass);
        decoderRegistry.remove(qname);
    }

    @SuppressWarnings("unchecked")
    public void encode(EncodingObject encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        EnDeCoder encoder = encoderRegistry.get(encodingObject.getClass());
        if (encoder == null) {
            throw new EncodingException("No encoder defined for " + encodingObject.getClass());
        }
        encoder.encode(encodingObject, writer);
    }

    public EncodingObject decode(XMLStreamReader reader) throws EncodingException {
        
        QName qname = reader.getName();

        EnDeCoder decoder = decoderRegistry.get(qname);
        if (decoder == null) {
            throw new EncodingException("No decoder defined for " + qname);
        }
        return decoder.decode(reader);
    }
}
