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

package org.apache.tuscany.sca.core;

import java.util.ArrayList;
import java.util.List;

public class ExtensionPointRegistryLocator {
    
    private static List<ExtensionPointRegistry> extensionPointRegistries = new ArrayList<ExtensionPointRegistry>();

    public static ExtensionPointRegistry getExtensionPointRegistry() {
        return extensionPointRegistries.size() > 0 ? extensionPointRegistries.get(0) : null;
    }
    
    public static List<ExtensionPointRegistry> getExtensionPointRegistries() {
        return extensionPointRegistries;
    }

    public static void addExtensionPointRegistry(ExtensionPointRegistry extensionPointRegistry) {
        extensionPointRegistries.add(extensionPointRegistry);
    }

    public static void removeExtensionPointRegistry(ExtensionPointRegistry extensionPointRegistry) {
        extensionPointRegistries.remove(extensionPointRegistry);
    }
}
