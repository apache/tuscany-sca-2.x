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

package org.apache.tuscany.sca.tools.registryinspector.extension;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
//import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
//import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
//import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * Implements a module activator for the RegistryInspector implementation extension module.
 * This is currently a cheat just to gain back door access to the ExtensionPointRegistry
 */
public class RegistryInspectorModuleActivator implements ModuleActivator {
    
    private static ExtensionPointRegistry epr = null;

    public static ExtensionPointRegistry getExtensionPointRegistry(){
        return epr;
    }
    public Object[] getExtensionPoints() {
        // This module extension does not contribute any new extension point
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        // store away the ExtensionPointRegistry reference so that 
        // we can get its hands on it
        epr = registry;
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
