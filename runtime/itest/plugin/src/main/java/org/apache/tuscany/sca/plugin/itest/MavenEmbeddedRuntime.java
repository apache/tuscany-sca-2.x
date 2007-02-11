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

import java.io.File;
import java.net.URI;

import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

/**
 * @version $Rev$ $Date$
 */
public class MavenEmbeddedRuntime extends AbstractRuntime {
    private ArtifactRepository artifactRepository;

    protected void registerSystemComponents() throws InitializationException {
        super.registerSystemComponents();
        try {
            getComponentManager().registerJavaObject(MavenRuntimeInfo.COMPONENT_NAME,
                                                     MavenRuntimeInfo.class,
                                                     (MavenRuntimeInfo) getRuntimeInfo());

            getComponentManager().registerJavaObject(MavenEmbeddedArtifactRepository.COMPONENT_NAME,
                                                     ArtifactRepository.class,
                                                     artifactRepository);
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }

    public void setArtifactRepository(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public void deployTestScdl(File testScdl, ClassLoader testClassLoader) throws Exception {
        Deployer deployer = getDeployer();

        URI name = URI.create("itest://testDomain/");
        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(testScdl.toURI().toURL());
        impl.setClassLoader(testClassLoader);

        ComponentDefinition<CompositeImplementation> definition =
            new ComponentDefinition<CompositeImplementation>(name, impl);
        Component testComponent = deployer.deploy(null, definition);
    }

    protected Deployer getDeployer() {
        try {
            URI uri = URI.create("sca://root.system/main/deployer");
            AtomicComponent component = (AtomicComponent) getComponentManager().getComponent(uri);
            return (Deployer) component.getTargetInstance();
        } catch (TargetResolutionException e) {
            throw new AssertionError(e);
        }
    }
}
