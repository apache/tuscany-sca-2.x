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

package org.apache.tuscany.sca.databinding.jaxb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @version $Rev$ $Date$
 */
public class DefaultXMLAdapterExtensionPoint implements XMLAdapterExtensionPoint {
    private Map<Class<?>, Class<? extends XmlAdapter<?, ?>>> adapters =
        new ConcurrentHashMap<Class<?>, Class<? extends XmlAdapter<?, ?>>>();

    public <B, A extends XmlAdapter<?, B>> void addAdapter(Class<B> boundType, Class<A> adapter) {
        adapters.put(boundType, adapter);
    }

    @SuppressWarnings("unchecked")
    public <B, A extends XmlAdapter<?, B>> Class<A> getAdapter(Class<B> boundType) {
        Class<A> cls = (Class<A>)adapters.get(boundType);
        if (cls != null) {
            return cls;
        }
        for (Map.Entry<Class<?>, Class<? extends XmlAdapter<?, ?>>> e : adapters.entrySet()) {
            if (e.getKey().isAssignableFrom(boundType)) {
                return ((Class<A>)e.getValue());
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <B, A extends XmlAdapter<?, B>> Class<A> removeAdapter(Class<B> boundType) {
        return (Class<A>)adapters.remove(boundType);
    }

    public Map<Class<?>, Class<? extends XmlAdapter<?, ?>>> getAdapters() {
        return adapters;
    }

}
