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
package org.apache.tuscany.sca.tools.inspector;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.impl.NodeImpl;


/**
 * An implementation of the ExtensionPointRegistryInspector service.
 */
public class RegistryInspector {

    public String registryAsString(SCANode2 node) {
        StringBuffer extensionPointRegistryString = new StringBuffer("Extension Point Registry \n");
        
        // Get the interesting extension points out of the registry and print them out
        
        try {
            
            // get the extension point registry we are hanging onto
            // We have to assume the type of the extension point registry here!
            DefaultExtensionPointRegistry extensionPointRegistry = (DefaultExtensionPointRegistry)((NodeImpl)node).getExtensionPointRegistry();
                                                                    
            // get the Map of extension points
            // This is a private (!) field so a bit of sneaky reflection is required
            Field extensionPointsField = extensionPointRegistry.getClass().getDeclaredField("extensionPoints");
            extensionPointsField.setAccessible(true);
            Map<Class<?>, Object> extensionPoints = (Map<Class<?>, Object>) extensionPointsField.get(extensionPointRegistry);
            
            // Record all the registered extension points
            Set<Class<?>> keySet = extensionPoints.keySet();
            for(Class<?>key : keySet){
                extensionPointRegistryString.append(key.getName());
                extensionPointRegistryString.append("\n");                 
            }
            
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return extensionPointRegistryString.toString();
    }

}
