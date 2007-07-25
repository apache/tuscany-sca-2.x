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
package org.apache.tuscany.sca.tools.registryinspector.inspector;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.http.ExtensibleServletHost;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.tools.registryinspector.extension.RegistryInspectorModuleActivator;
import org.osoa.sca.annotations.Reference;


/**
 * An implementation of the ExtensionPointRegistryInspector service.
 */
public class ExtensionPointRegistryInspectorImpl implements ExtensionPointRegistryInspector {

    public String eprAsString() {
        StringBuffer extensionPointRegistryString = new StringBuffer("Extension Point Registry \n");
        
        try {
            // get the extension point registry we are hanging onto
            // We have to assume the type of the extension point registry here!
            DefaultExtensionPointRegistry extensionPointRegistry = (DefaultExtensionPointRegistry)
                                                                    RegistryInspectorModuleActivator.getExtensionPointRegistry();
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
                
                // do whatever we want with each entry
                if ( key == DefaultModelFactoryExtensionPoint.class){
                }
                
                // the registry entry that holds all of the real servlet hosts
                if ( key == ServletHostExtensionPoint.class){
                    ServletHostExtensionPoint shep = (ServletHostExtensionPoint)extensionPoints.get(key);
                    extensionPointRegistryString.append("  Registered Servlet hosts for " + shep.toString() +  "\n");
                    List<ServletHost> servletHosts = shep.getServletHosts();
                    
                    for ( ServletHost servletHost : servletHosts  ) {
                        extensionPointRegistryString.append( "    - " + servletHost.toString() + "\n");
                    }           
                } 
                
                // the registry entry that holds the proxy to servlet hosts
                if ( key == ServletHost.class){                    
                    ExtensibleServletHost server = (ExtensibleServletHost)extensionPoints.get(key);
                    
                    extensionPointRegistryString.append("  Registered Servlets for " + server.toString() +  "\n");
 
                    for (String uri : server.getURIList()){
                        extensionPointRegistryString.append("    - " + uri + "\n");
                    }
        
                }                 
            }
            
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return extensionPointRegistryString.toString();
    }

}
