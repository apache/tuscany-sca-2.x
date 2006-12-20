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
package org.apache.tuscany.container.javascript;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

public class JavaScriptComponentType extends ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> {

    private Scope lifecycleScope = Scope.COMPOSITE;

    public JavaScriptComponentType() {
    }

    @SuppressWarnings("unchecked")
    public JavaScriptComponentType(ComponentType ct) {
        // TODO: A bit hacky but this is so the non-JavaScript .componentType side file can be used for now
        setInitLevel(ct.getInitLevel());
        for (Object property : ct.getProperties().values()) {
            add((Property)property);
        }
        for (Object reference : ct.getReferences().values()) {
            add((ReferenceDefinition)reference);
        }
        for (Object service : ct.getServices().values()) {
            add((ServiceDefinition)service);
        }
    }
    
    public Scope getLifecycleScope() {
        return lifecycleScope;
    }

    public void setLifecycleScope(Scope lifecycleScope) {
        this.lifecycleScope = lifecycleScope;
    }

}
