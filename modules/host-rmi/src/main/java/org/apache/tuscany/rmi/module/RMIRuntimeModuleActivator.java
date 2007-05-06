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

package org.apache.tuscany.rmi.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.rmi.DefaultRMIHost;
import org.apache.tuscany.rmi.RMIHost;

/**
 * @version $Rev: 529327 $ $Date: 2007-04-16 22:40:43 +0530 (Mon, 16 Apr 2007) $
 */
public class RMIRuntimeModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(RMIHost.class, new DefaultRMIHost());
        return map;
    }

    public void start(ExtensionPointRegistry extensionPointRegistry) {
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
