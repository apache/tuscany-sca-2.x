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

package org.apache.tuscany.sca.implementation.script;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.spi.ImplementationActivator;
import org.apache.tuscany.sca.spi.InvokerFactory;
import org.apache.tuscany.sca.spi.utils.PropertyValueObjectFactory;

public class ScriptImplementationActivator implements ImplementationActivator<ScriptImplementation> {

    // TODO: seems wrong to need PropertyValueObjectFactory, could it be on Property somehow? 
    protected PropertyValueObjectFactory propertyFactory;

    public ScriptImplementationActivator(PropertyValueObjectFactory propertyFactory) {
        this.propertyFactory = propertyFactory;
    }

    public Class<ScriptImplementation> getImplementationClass() {
        return ScriptImplementation.class;
    }

    public InvokerFactory createInvokerFactory(RuntimeComponent rc, ComponentType ct, ScriptImplementation implementation) {
        return new ScriptInvokerFactory(rc, ct, implementation, propertyFactory);
    }

}
