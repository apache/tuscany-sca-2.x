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
package org.apache.tuscany.core.context;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.TargetWireFactory;

/**
 * The default implementation of an external service context
 *
 * @version $Rev$ $Date$
 */
public class ReferenceContextImpl extends AbstractContext implements ReferenceContext {

    private TargetWireFactory<?> targetWireFactory;

    private ObjectFactory targetInstanceFactory;

    /**
     * Creates a reference context
     *
     * @param name the name of the reference context
     * @param targetWireFactory the factory which creates proxies implementing the configured service interface for the
     *        reference context. There is always only one proxy factory as an reference context is configured with one
     *        service
     * @param targetInstanceFactory the object factory that creates an artifact capabile of communicating over the
     *        binding transport configured on the reference context. The object factory may implement a caching strategy.
     */
    public ReferenceContextImpl(String name, TargetWireFactory targetWireFactory, ObjectFactory targetInstanceFactory) {
        super(name);
        assert (targetWireFactory != null) : "Target proxy factory was null";
        assert (targetInstanceFactory != null) : "Target instance factory was null";
        this.targetWireFactory = targetWireFactory;
        this.targetInstanceFactory = targetInstanceFactory;
    }

    public void start() {
        lifecycleState = RUNNING;
    }

    public void stop() {
        lifecycleState = STOPPED;
    }


    public Object getInstance(QualifiedName qName) throws TargetException {
        try {
            return targetWireFactory.createProxy();
            // TODO do we cache the proxy, (assumes stateful capabilities will be provided in an interceptor)
        } catch (ProxyCreationException e) {
            TargetException te = new TargetException(e);
            te.addContextName(getName());
            throw te;
        }
    }

    public Object getHandler() throws TargetException {
        return targetInstanceFactory.getInstance();
    }
}
