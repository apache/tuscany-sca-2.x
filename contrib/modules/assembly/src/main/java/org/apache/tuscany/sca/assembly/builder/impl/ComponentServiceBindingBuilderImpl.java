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

package org.apache.tuscany.sca.assembly.builder.impl;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BindingBuilderExtension;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that performs any additional building steps that
 * component service bindings may need.  Used for WSDL generation.
 *
 * @version $Rev$ $Date$
 */
public class ComponentServiceBindingBuilderImpl implements CompositeBuilder {
    private Monitor monitor;

    public ComponentServiceBindingBuilderImpl(Monitor monitor) {
        this.monitor = monitor;
    }

    public void build(Composite composite) throws CompositeBuilderException {
        buildServiceBindings(composite);
    }
    
    private void buildServiceBindings(Composite composite) {
        
        // build bindings recursively
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                buildServiceBindings((Composite)implementation);
            }
        }

        // find all the component service bindings     
        for (Component component : composite.getComponents()) {
            for (ComponentService componentService : component.getServices()) {
                for (Binding binding : componentService.getBindings()) {
                    if (binding instanceof BindingBuilderExtension) {
                        BindingBuilder builder = ((BindingBuilderExtension)binding).getBuilder();
                        if (builder != null) {
                            builder.build(component, componentService, binding, monitor);
                        }
                    }
                }
            }
        }
    }

}
