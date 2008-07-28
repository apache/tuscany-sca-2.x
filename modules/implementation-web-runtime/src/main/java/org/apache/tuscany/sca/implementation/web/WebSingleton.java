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

package org.apache.tuscany.sca.implementation.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Singleton instance to share data between the ModuleActivator,
 * ContextScriptProcessorExtensionPoint, and Taglib tags.
 * 
 * TODO: find a way to share the data without needing a singleton
 */
public class WebSingleton {

    private List<ContextScriptProcessor> contextScriptProcessors = new ArrayList<ContextScriptProcessor>();
    private RuntimeComponent runtimeComponent;

    public static final WebSingleton INSTANCE = new WebSingleton();
    private WebSingleton() {
    }
        
    public ComponentReference getComponentReference(String name)  {
        if (runtimeComponent == null) {
            throw new IllegalStateException("RuntimeComponent is null. Missing a <implementation.web>?");
        }
        for (ComponentReference cr : runtimeComponent.getReferences()) {
            if (cr.getName().equals(name)) {
                return cr;
            }
        }
        return null;
    }

    public void addContextScriptProcessor(ContextScriptProcessor csp) {
        contextScriptProcessors.add(csp);
    }

    public List<ContextScriptProcessor> getContextScriptProcessors() {
        return contextScriptProcessors;
    }

    public RuntimeComponent getRuntimeComponent() {
        return runtimeComponent;
    }

    public void setRuntimeComponent(RuntimeComponent rc) {
        if (this.runtimeComponent != null) {
            throw new IllegalStateException("adding component '" + rc.getName() + "' but web module already has a component: " + runtimeComponent.getName());
        }
        this.runtimeComponent = rc;
    }
}
