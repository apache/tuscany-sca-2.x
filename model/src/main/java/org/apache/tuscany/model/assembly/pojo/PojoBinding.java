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
import org.apache.tuscany.model.assembly.Binding;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PojoBinding implements Binding {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoBinding() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String uri;
    
    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        check();
        this.uri=uri;
    }

    private Object runtimeConfiguration;
    
    public void setRuntimeConfiguration(Object configuration) {
        check();
        runtimeConfiguration=configuration;
    }

    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        return visitor.visit(this);
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
