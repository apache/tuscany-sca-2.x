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

package org.apache.tuscany.sca.distributed.node;

import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.core.assembly.ActivationException;

/**
 * A set of operations for managing the components in a node
 * 
 * @version $Rev: 552343 $ $Date$
 */
public interface ComponentManager {

    /**
     * Get a list of component objects 
     * 
     * @return list of components
     */
    List<Component> getComponents();

    /**
     * Get a component object by name
     * 
     * @param componentName
     * @return component
     */
    Component getComponent(String componentName);
    
    /** 
     * Returns true if the named component is started
     * @param component
     * @return true if component is started
     */
    boolean isComponentStarted(Component component);

    /** 
     * Start a component. Makes it ready to receive messages
     * 
     * @param component
     * @throws ActivationException
     */
    void startComponent(Component component) throws ActivationException;

    /**
     * Stop  component. 
     * @param component
     * @throws ActivationException
     */
    void stopComponent(Component component) throws ActivationException;

    /**
     * Add a listener that will be called back when the component state changes
     * 
     * @param listener
     */
    void addComponentListener(ComponentListener listener);

    /**
     * Remove a component listener
     * 
     * @param listener
     */
    void removeComponentListener(ComponentListener listener);
    
}
