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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.types.OperationType;

import commonj.sdo.Type;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PojoOperationType implements OperationType {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoOperationType() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        check();
        this.name = name;
    }

    private Type inputType;

    public Type getInputType() {
        return inputType;
    }

    public void setInputType(Type type) {
        check();
        inputType = type;
    }

    private Type outputType;

    public Type getOutputType() {
        return outputType;
    }

    public void setOutputType(Type type) {
        check();
        outputType = type;
    }

    private List<Type> exceptionTypes = new ArrayList();

    private List<Type> unModifiableExceptionTypes;

    public List<Type> getExceptionTypes() {
        if (frozen) {
            if (unModifiableExceptionTypes == null) {
                unModifiableExceptionTypes = Collections.unmodifiableList(exceptionTypes);
            }
            return unModifiableExceptionTypes;
        } else {
            return exceptionTypes;
        }
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
