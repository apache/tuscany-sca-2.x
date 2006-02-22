/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context.impl;

import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

/**
 * The default implementation of an external service
 * 
 * @version $Rev$ $Date$
 */
public class ExternalServiceImpl extends AbstractContext implements ExternalServiceContext {

    private ProxyFactory factory;

    private Object targetProxy;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ExternalServiceImpl(String name, ProxyFactory factory) {
        super(name);
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public Object getInstance(QualifiedName qName) throws TargetException {
        return targetProxy;
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        return getInstance(qName);
    }

    public void start() throws CoreRuntimeException {
        try {
            // create the target proxy at startup since it is stateless
            // (assumes stateful capabilities will be provided in an interceptor)
            targetProxy = factory.createProxy();
            lifecycleState = RUNNING;
        } catch (ProxyCreationException e) {
            lifecycleState = ERROR;
            ContextInitException ce = new ContextInitException(e);
            ce.setIdentifier(getName());
            throw ce;
        }
    }

    public void stop() throws CoreRuntimeException {
        lifecycleState = STOPPED;
        targetProxy = null;
    }

    public Object getImplementationInstance() throws TargetException{
        return null;
    }

    public Object getImplementationInstance(boolean notify) throws TargetException{
        return null;
    }
}
