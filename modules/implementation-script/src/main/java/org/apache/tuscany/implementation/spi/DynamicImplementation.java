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

package org.apache.tuscany.implementation.spi;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.ComponentPreProcessor;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class DynamicImplementation extends AbstractImplementation implements ComponentPreProcessor {
    
    public void preProcess(Component component) {
        RuntimeComponent rtc = (RuntimeComponent) component;
        
        for (Service service : rtc.getServices()) {
            if (getService(service.getName()) == null) {
                getServices().add(createService(service));
            }
        }
        
        Service dynamicService = getService("$dynamic$");
        if (dynamicService != null && getServices().size() > 1) {
            getServices().remove(dynamicService);
            dynamicService = null;
        }

        for (Reference reference : rtc.getReferences()) {
            if (getReference(reference.getName()) == null) {
                getReferences().add(createReference(reference));
            }
        }
        
        // TODO: support properties
    }
    
    protected Service createService(Service service) {
        Service newService;
        try {
            newService = (Service)service.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should not ever happen
        }

        return newService;
    }

    protected Reference createReference(Reference reference) {
        Reference newReference;
        try {
            newReference = (Reference)reference.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should not ever happen
        }
        return newReference;
    }

}
