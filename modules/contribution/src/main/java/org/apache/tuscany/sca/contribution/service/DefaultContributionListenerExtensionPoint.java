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

package org.apache.tuscany.sca.contribution.service;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Default implementation of a contribution listener extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultContributionListenerExtensionPoint implements ContributionListenerExtensionPoint {
    
    private List<ContributionListener> listeners = new CopyOnWriteArrayList<ContributionListener>();
    private boolean loadedListeners;
    private ModelFactoryExtensionPoint modelFactories;
    
    /**
     * Constructs a new DefaultContributionListenerExtensionPoint.
     *  
     */
    public DefaultContributionListenerExtensionPoint(ModelFactoryExtensionPoint modelFactories) {
        this.modelFactories = modelFactories;
    }

    /**
     * Constructs a new DefaultContributionListenerExtensionPoint.
     *  
     */
    public DefaultContributionListenerExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
    }

    public void addContributionListener(ContributionListener listener) {
        listeners.add(listener);
    }

    public List<ContributionListener> getContributionListeners() {
        loadListeners();
        return listeners;
    }

    public void removeContributionListener(ContributionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Dynamically load listeners declared under META-INF/services
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadListeners() {
        if (loadedListeners)
            return;

        // Get the listener service declarations
        Set<ServiceDeclaration> listenerDeclarations; 
        try {
            listenerDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(ContributionListener.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load and instantiate the listeners
        for (ServiceDeclaration listenerDeclaration: listenerDeclarations) {
            ContributionListener listener;
            try {
                Class<ContributionListener> listenerClass = (Class<ContributionListener>)listenerDeclaration.loadClass();
                try {
                    Constructor<ContributionListener> constructor = listenerClass.getConstructor(ModelFactoryExtensionPoint.class);
                    try {
                        listener = constructor.newInstance(modelFactories);
                    } catch (InvocationTargetException e) {
                        throw new IllegalArgumentException(e);
                    }
                } catch (NoSuchMethodException e) {
                    listener = listenerClass.newInstance();
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
            addContributionListener(listener);
        }
        
        loadedListeners = true;
    }

}
