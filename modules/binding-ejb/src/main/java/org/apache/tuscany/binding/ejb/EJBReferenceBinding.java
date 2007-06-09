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
package org.apache.tuscany.binding.ejb;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * EJBReference
 */
public class EJBReferenceBinding<T> extends ReferenceBindingExtension {
    private EJBBindingDefinition ejbBinding;

    @SuppressWarnings("unchecked")
    public EJBReferenceBinding(String theName,
                        CompositeComponent parent,
                        EJBBindingDefinition ejbBinding) {
        super(theName, parent);
        this.ejbBinding = ejbBinding;
    }

    public TargetInvoker createTargetInvoker(ServiceContract serviceContract, Operation operation) {
        return new EJBTargetInvoker(ejbBinding, serviceContract.getInterfaceClass(), operation);
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        return new EJBTargetInvoker(operation);
    }

    public QName getBindingType() {
        return EJBBindingDefinition.BINDING_EJB;
    }

}
