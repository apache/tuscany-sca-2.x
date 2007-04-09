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
package org.apache.tuscany.sca.plugin.itest;

import java.net.URI;
import java.util.Collection;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.GroupInitializationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.maven.plugin.logging.Log;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.implementation.PojoWorkContextTunnel;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.runtime.InitializationException;

/**
 * @version $Rev$ $Date$
 */
public class MavenEmbeddedRuntime extends AbstractRuntime<MavenRuntimeInfo> {
    private ArtifactRepository artifactRepository;

    public MavenEmbeddedRuntime(Log log) {
        super(MavenRuntimeInfo.class);
        MonitorFactory monitorFactory = new MavenMonitorFactory(log);
        setMonitorFactory(monitorFactory);
    }

    protected void registerBaselineSystemComponents() throws InitializationException {
        super.registerBaselineSystemComponents();
        registerSystemComponent(MavenEmbeddedArtifactRepository.COMPONENT_NAME,
            ArtifactRepository.class,
            artifactRepository);
    }

    public void setArtifactRepository(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public Collection<Component> deployTestScdl(ComponentDefinition<CompositeImplementation> definition)
        throws Exception {
        Deployer deployer = getDeployer();
        return deployer.deploy(null, definition);
    }

    public void startContext(URI compositeId) throws GroupInitializationException {
        getScopeRegistry().getScopeContainer(Scope.COMPOSITE).startContext(compositeId, compositeId);
    }

    public void executeTest(URI contextId, URI componentId, Operation<?> operation) throws Exception {
        Component testComponent = getComponentManager().getComponent(componentId);
        TargetInvoker targetInvoker = testComponent.createTargetInvoker("testService", operation);
        getWorkContext().setIdentifier(Scope.COMPOSITE, contextId);
        WorkContext workContext = new SimpleWorkContext();
        workContext.setIdentifier(Scope.COMPOSITE, contextId);
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            targetInvoker.invokeTarget(null, TargetInvoker.NONE, workContext);
        } finally {
            getWorkContext().clearIdentifier(Scope.COMPOSITE);
        }
    }
}
