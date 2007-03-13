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
package org.apache.tuscany.persistence.datasource;

import java.net.URI;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * A system component that creates <code>DataSource</code> instances. The component uses a {@link ProviderObjectFactory}
 * to instantiate the actual <code>DataSource</code>
 *
 * @version $Rev$ $Date$
 */
public class DataSourceComponent extends AtomicComponentExtension {
    private ProviderObjectFactory instanceFactory;

    /**
     * Creates a <code>DataSourceComponent</code>
     *
     * @param uri             the uri of the component
     * @param instanceFactory the provider factory
     * @param initLevel       the initialization level
     */
    public DataSourceComponent(URI uri,
                               ProviderObjectFactory instanceFactory,
                               int initLevel) {

        super(uri, null, null, initLevel, -1, -1);
        this.instanceFactory = instanceFactory;
    }

    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        return null;
    }

    public Object createInstance() throws ObjectCreationException {
        return instanceFactory.getInstance();
    }

    public Object getTargetInstance() throws TargetResolutionException {
        return null;
    }


    public Object getAssociatedTargetInstance() throws TargetResolutionException {
        return null;
    }

    public void destroy(Object instance) throws TargetDestructionException {
        if (instance instanceof DataSourceProvider) {
            try {
                ((DataSourceProvider) instance).close();
            } catch (ProviderException e) {
                throw new DataSourceCloseException("Error closing data source provider", e);
            }
        }
    }

    public List<Wire> getWires(String name) {
        return null;
    }

    public void attachCallbackWire(Wire wire) {

    }

    public void attachWire(Wire wire) {

    }

    public void attachWires(List<Wire> wires) {

    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation)
        throws TargetInvokerCreationException {
        return null;
    }

    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        return null;
    }
}
