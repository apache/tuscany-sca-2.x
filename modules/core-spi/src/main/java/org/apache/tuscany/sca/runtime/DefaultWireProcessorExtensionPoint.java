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
package org.apache.tuscany.sca.runtime;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * The default implementation of a <code>WireProcessorExtensionPoint</code>
 *
 * @version $Rev$ $Date$
 */
public class DefaultWireProcessorExtensionPoint implements RuntimeWireProcessorExtensionPoint, LifeCycleListener {
    private ExtensionPointRegistry registry;
    private boolean loaded;

    /**
     * @param registry
     */
    public DefaultWireProcessorExtensionPoint(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    /**
     * The list of WireProcessors available to the runtime
     */
    private final List<RuntimeWireProcessor> processors = new ArrayList<RuntimeWireProcessor>();

    /**
    * Registers a wire-processor in the runtime
    * 
    * @param processor The processor to register
    */
    public void addWireProcessor(RuntimeWireProcessor processor) {
        processors.add(processor);
        if (processor instanceof LifeCycleListener) {
            ((LifeCycleListener)processor).start();
        }
    }

    /**
     * De-registers a wire-processor in the runtime
     * 
     * @param processor The processor to de-register
     */
    public void removeWireProcessor(RuntimeWireProcessor processor) {
        boolean found = processors.remove(processor);
        if (found && (processor instanceof LifeCycleListener)) {
            ((LifeCycleListener)processor).stop();
        }
    }

    /**
     * Returns a list of registered wire-processors.
     * 
     * @return The list of wire processors
     */
    public List<RuntimeWireProcessor> getWireProcessors() {
        loadWireProcessors();
        return processors;
    }

    private synchronized void loadWireProcessors() {
        if (loaded) {
            return;
        }
        try {
            Collection<ServiceDeclaration> sds =
                registry.getServiceDiscovery().getServiceDeclarations(RuntimeWireProcessor.class, true);
            for (ServiceDeclaration sd : sds) {
                Class type = sd.loadClass();
                Constructor constructor = null;
                RuntimeWireProcessor processor = null;
                try {
                    constructor = type.getConstructor(ExtensionPointRegistry.class);
                    processor = (RuntimeWireProcessor)constructor.newInstance(registry);

                } catch (NoSuchMethodException e) {
                    constructor = type.getConstructor();
                    processor = (RuntimeWireProcessor)constructor.newInstance();
                }
                if (processor != null) {
                    addWireProcessor(processor);
                }
            }
            loaded = true;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public void start() {
    }

    public void stop() {
        for (RuntimeWireProcessor processor : processors) {
            if ((processor instanceof LifeCycleListener)) {
                ((LifeCycleListener)processor).stop();
            }
        }
        processors.clear();
    }
}
