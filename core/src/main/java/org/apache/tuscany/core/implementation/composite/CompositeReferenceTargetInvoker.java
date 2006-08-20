/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.Method;

import org.apache.tuscany.core.injection.WireObjectFactory;
import org.apache.tuscany.core.wire.PojoTargetInvoker;
import org.apache.tuscany.spi.component.TargetException;

public class CompositeReferenceTargetInvoker extends PojoTargetInvoker {

    private WireObjectFactory wireFactory;
    
    public CompositeReferenceTargetInvoker(Method operation, WireObjectFactory wireFactory) {
        super(operation);
        this.wireFactory = wireFactory;
    }

    public CompositeReferenceTargetInvoker clone() throws CloneNotSupportedException {
        CompositeReferenceTargetInvoker invoker = (CompositeReferenceTargetInvoker) super.clone();
        invoker.wireFactory = this.wireFactory;
        return invoker;
    }

    protected Object getInstance() throws TargetException {
        Object instance = wireFactory.getInstance();
        return instance;
    }
}
