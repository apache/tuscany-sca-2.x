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

package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Base implementation class of Implementation model interface
 *
 * @version $Rev$ $Date$
 * @tuscany.extension.spi By Inheritance
 */
public abstract class ImplementationImpl extends ComponentTypeImpl implements Implementation, PolicySubject {
    private QName type;
    private ExtensionType extensionType;
    private List<Operation> operations = new ArrayList<Operation>();

    protected ImplementationImpl(QName type) {
        super();
        this.type = type;
    }

    public ExtensionType getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(ExtensionType extensionType) {
        this.extensionType = extensionType;
    }

    public QName getType() {
        return type;
    }
    
    public String toString() {
        return String.valueOf(getType());
    }

    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ImplementationImpl impl = (ImplementationImpl)super.clone();
        impl.operations = new ArrayList<Operation>();
        for (Operation operation : operations) {
            impl.operations.add((Operation)operation.clone());
        }
        return impl;
    }

    // Override the ComponentTypeImpl.hashCode()
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    // Override the ComponentTypeImpl.equals()
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
