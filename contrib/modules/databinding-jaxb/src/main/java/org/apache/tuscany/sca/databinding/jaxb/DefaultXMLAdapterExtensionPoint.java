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
    private Map<Class<?>, Class<? extends XmlAdapter>> adapters =
        new ConcurrentHashMap<Class<?>, Class<? extends XmlAdapter>>();

    public void addAdapter(Class<?> boundType, Class<? extends XmlAdapter> adapter) {
        adapters.put(boundType, adapter);
    }

    public Class<? extends XmlAdapter> getAdapter(Class<?> boundType) {
        Class<? extends XmlAdapter> cls = adapters.get(boundType);
        if (cls != null) {
            return cls;
        }
        for (Map.Entry<Class<?>, Class<? extends XmlAdapter>> e : adapters.entrySet()) {
            if (e.getKey().isAssignableFrom(boundType)) {
                return e.getValue();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends XmlAdapter> removeAdapter(Class<?> boundType) {
        return adapters.remove(boundType);
    }

    public Map<Class<?>, Class<? extends XmlAdapter>> getAdapters() {
        return adapters;
    }

}
