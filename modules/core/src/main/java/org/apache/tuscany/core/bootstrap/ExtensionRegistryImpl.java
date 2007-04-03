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

package org.apache.tuscany.core.bootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry to hold all the extension points and extensions
 * 
 * @version $Rev$ $Date$
 */
public class ExtensionRegistryImpl implements ExtensionRegistry {
    private Map<Class, List<Object>> extensions = new HashMap<Class, List<Object>>();

    public <T> void addExtension(Class<T> extensionPoint, T extension) {
        List<Object> list = extensions.get(extensionPoint);
        if (list == null) {
            list = new ArrayList<Object>();
            extensions.put(extensionPoint, list);
        }
        if (!list.contains(extension)) {
            list.add(extension);
        }
    }

    public <T> T getExtension(Class<T> extensionPoint) {
        List<Object> list = extensions.get(extensionPoint);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return extensionPoint.cast(list.get(0));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getExtensions(Class<T> extensionPoint) {
        return (List<T>)extensions.get(extensionPoint);
    }

    public <T> void removeExtension(Class<T> extensionPoint, T extension) {
        List<T> list = getExtensions(extensionPoint);
        if (list != null) {
            list.remove(extension);
        }
    }

    public void removeExtensionPoint(Class extensionPoint) {
        extensions.remove(extensionPoint);
    }

}
