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
package org.apache.tuscany.core.deployer;

import java.net.URI;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * An holder that can be used during the load process to store information that is not part of the logical assembly
 * model. This should be regarded as transient and references to this context should not be stored inside the model.
 *
 * @version $Rev$ $Date$
 */
public class ChildDeploymentContext extends AbstractDeploymentContext {
    private final DeploymentContext parent;

    /**
     * Constructor defining properties of this context.
     *
     * @param parent       the parent of this context
     * @param classLoader  the classloader for loading application resources
     * @param componentId  the id of the component being deployed
     */
    public ChildDeploymentContext(DeploymentContext parent,
                                  ClassLoader classLoader,
                                  URI componentId) {
        super(classLoader, componentId);
        assert parent != null;
        this.parent = parent;
    }

    public DeploymentContext getParent() {
        return parent;
    }

    public XMLInputFactory getXmlFactory() {
        return parent.getXmlFactory();
    }

    public ScopeContainer getCompositeScope() {
        return parent.getCompositeScope();
    }

    public URI getGroupId() {
        return parent.getGroupId();
    }
}
