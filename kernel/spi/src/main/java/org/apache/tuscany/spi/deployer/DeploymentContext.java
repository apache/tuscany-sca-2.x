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
package org.apache.tuscany.spi.deployer;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;

/**
 * A holder that can be used during the load process to store information that is not part of the logical assembly
 * model. This should be regarded as transient and references to this context should not be stored inside the model.
 *
 * @version $Rev$ $Date$
 */
public interface DeploymentContext {
    /**
     * Returns the parent of this deployment context. Will be null for the context created at the root of a deployment.
     *
     * @return the parent of this deployment context; may be null
     */
    DeploymentContext getParent();

    /**
     * Returns a class loader that can be used to load application resources.
     *
     * @return a class loader that can be used to load application resources
     */
    ClassLoader getClassLoader();

    /**
     * Returns a factory that can be used to obtain an StAX XMLStreamReader.
     *
     * @return a factory that can be used to obtain an StAX XMLStreamReader
     */
    XMLInputFactory getXmlFactory();

    /**
     * Returns the ScopeContainer for the COMPOSITE scope that will be associated with this deployment unit.
     *
     * @return the ScopeContainer for the COMPOSITE scope that will be associated with this deployment unit
     */
    @Deprecated
    ScopeContainer getCompositeScope();

    /**
     * Returns the URI of the composite scope group.
     * @return the URI of the composite scope group
     */
    URI getGroupId();

    /**
     * Returns the location of the SCDL definition being deployed.
     *
     * @return the location of the SCDL definition being deployed
     */
    URL getScdlLocation();

    /**
     * Returns the URI of the composite component currently being deployed.
     *
     * @return the URI of the composite component currently being deployed
     */
    URI getComponentId();

    /**
     * Returns true if the autowire is enabled for the current deployment.
     *
     * @return true if the autowire is enabled for the current deployment
     */
    boolean isAutowire();

    /**
     * Sets if the autowire is enabled for the current deployment.
     *
     * @param autowire true if autowire is enabled
     */
    void setAutowire(boolean autowire);

    @Deprecated
    Map<URI, Component> getComponents();
}
