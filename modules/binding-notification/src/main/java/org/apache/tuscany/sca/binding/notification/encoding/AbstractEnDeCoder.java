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

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractEnDeCoder<E extends EncodingObject> implements EnDeCoder<E> {

    protected EncodingRegistry registry;
    
    protected AbstractEnDeCoder(EncodingRegistry registry) {
        
        this.registry = registry;
    }

    public void start() {
        Class<E> encodingType = getEncodingObjectType();
        QName encodingQName = getEncodingObjectQName();

        registry.registerEnDeCoder(encodingType, encodingQName, this);
    }

    public void stop() {
        Class<E> encodingType = getEncodingObjectType();
        QName encodingQName = getEncodingObjectQName();

        registry.unregisterEnDeCoder(encodingType, encodingQName);
    }

    /**
     * Gets the qualified name of the XML fragment for the Encoding
     * object.
     * 
     * @return Qualified name of the XML fragment.
     */
    protected abstract QName getEncodingObjectQName();

    /**
     * Returns the type of the encoding object.
     * 
     * @return Encoding object type.
     */
    protected abstract Class<E> getEncodingObjectType();
}
