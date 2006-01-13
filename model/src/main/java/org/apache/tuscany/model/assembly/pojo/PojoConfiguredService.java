/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.model.assembly.pojo;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Port;
import org.apache.tuscany.model.assembly.Service;

public class PojoConfiguredService implements ConfiguredService {

    private boolean frozen;
    
    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoConfiguredService() {
        super();
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private Service service;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        check();
        this.service = service;
    }

    private Part part;

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        check();
        this.part = part;
    }

    private Port port;
    
    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        check();
        this.port = port;
    }

    private Object factory;
    
    public Object getProxyFactory() {
        return factory;
    }

    public void setProxyFactory(Object proxyFactory) {
        check();
        factory = proxyFactory;
    }

    private Object configuration;
    
    public void setRuntimeConfiguration(Object configuration) {
        check();
        this.configuration = configuration;
    }

    public Object getRuntimeConfiguration() {
        return configuration;
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (visitor.visit(this)) {
            if (service != null && !service.accept(visitor)){
                return false;
            }
            if (port != null && !port.accept(visitor)){
                return false;
            }
            if (part != null && !part.accept(visitor)){
                return false;
            }
        }
        return true;
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
