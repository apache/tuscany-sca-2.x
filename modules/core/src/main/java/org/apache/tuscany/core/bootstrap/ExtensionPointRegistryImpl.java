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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;

/**
 * A registry to hold all the extension points and extensions
 * 
 * @version $Rev$ $Date$
 */
public class ExtensionPointRegistryImpl implements ExtensionPointRegistry {
    private Map<Class, Object> extensionPoints = new HashMap<Class, Object>();

    public <T> void addExtensionPoint(Class<T> extensionPointType, T extensionPoint) {
        extensionPoints.put(extensionPointType, extensionPoint);
    }

    public <T> T getExtensionPoint(Class<T> extensionPointType) {
        return extensionPointType.cast(extensionPoints.get(extensionPointType));
    }

    public void removeExtensionPoint(Class extensionPoint) {
        extensionPoints.remove(extensionPoint);
    }

}
