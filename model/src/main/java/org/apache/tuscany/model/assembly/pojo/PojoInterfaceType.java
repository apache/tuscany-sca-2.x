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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.types.InterfaceType;
import org.apache.tuscany.model.types.OperationType;

public class PojoInterfaceType implements InterfaceType {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoInterfaceType() {
        super();
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private List<OperationType> types = new ArrayList();

    private List<OperationType> unModifiableOperationTypes;

    private Map<String, OperationType> typesMap = new HashMap();

    public List<OperationType> getOperationTypes() {
        if (frozen) {
            if (unModifiableOperationTypes == null) {
                unModifiableOperationTypes = Collections.unmodifiableList(types);
            }
            return unModifiableOperationTypes;
        } else {
            return types;
        }
    }

    public OperationType getOperationType(String name) {
        return typesMap.get(name);
    }

    public void addOperationType(OperationType type) {
        check();
        types.add(type);
        typesMap.put(type.getName(), type);
    }

    private String uri;

    public String getURI() {
        return uri;
    }

    private void setURI(String uri) {
        check();
        this.uri = uri;
    }

    private Class claz;

    public Class getInstanceClass() {
        return claz;
    }

    public void setInstanceClass(Class instanceClass) {
        check();
        claz = instanceClass;
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (visitor.visit(this)) {
            for (OperationType type : types) {
                if (!type.accept(visitor)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
