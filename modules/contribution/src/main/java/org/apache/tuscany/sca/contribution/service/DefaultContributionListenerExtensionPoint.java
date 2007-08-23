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
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.contribution.util.ServiceConfigurationUtil;

/**
 * Default implementation of a contribution listener extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultContributionListenerExtensionPoint implements ContributionListenerExtensionPoint {
    
    private List<ContributionListener> listeners = new ArrayList<ContributionListener>();
    private boolean loadedListeners;

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
    private void loadListeners() {
        if (loadedListeners)
            return;

        // Get the databinding service declarations
        ClassLoader classLoader = ContributionListener.class.getClassLoader();
        List<String> listenerDeclarations; 
        try {
            listenerDeclarations = ServiceConfigurationUtil.getServiceClassNames(classLoader, ContributionListener.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load data bindings
        for (String listenerDeclaration: listenerDeclarations) {
            ContributionListener listener;
            try {
                Class<ContributionListener> listenerClass = (Class<ContributionListener>)Class.forName(listenerDeclaration, true, classLoader);
                listener = listenerClass.newInstance();
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
