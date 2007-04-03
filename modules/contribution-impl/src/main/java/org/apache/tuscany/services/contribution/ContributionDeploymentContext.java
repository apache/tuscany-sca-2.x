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
package org.apache.tuscany.services.contribution;

import java.net.URI;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * A holder that can be used during the load process to store information that is not part of the logical assembly
 * model. This should be regarded as transient and references to this context should not be stored inside the model.
 *
 * @version $Rev: 519446 $ $Date: 2007-03-17 14:38:00 -0700 (Sat, 17 Mar 2007) $
 */
public class ContributionDeploymentContext extends AbstractDeploymentContext {
    private final XMLInputFactory xmlFactory;
    private final ScopeContainer scopeContainer;

    /**
     * Constructor defining properties of this context.
     *
     * @param classLoader    the classloader for loading application resources
     * @param scdlLocation   the location of the SCDL defining this composite
     * @param componentId    the id of the component being deployed
     * @param xmlFactory     a factory that can be used to obtain an StAX XMLStreamReader
     * @param scopeContainer the scope context representing this deployment's COMPOSITE scope
     * @param autowire       if autowire is enabled
     */
    public ContributionDeploymentContext(ClassLoader classLoader,
                                 URL scdlLocation,
                                 URI componentId,
                                 XMLInputFactory xmlFactory,
                                 ScopeContainer scopeContainer,
                                 boolean autowire) {
        super(classLoader, scdlLocation, componentId, autowire);
        this.xmlFactory = xmlFactory;
        this.scopeContainer = scopeContainer;
    }

    public DeploymentContext getParent() {
        return null;
    }

    public XMLInputFactory getXmlFactory() {
        return xmlFactory;
    }

    public ScopeContainer getCompositeScope() {
        return scopeContainer;
    }

    public URI getGroupId() {
        return getComponentId();
    }
}
