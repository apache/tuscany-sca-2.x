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
package org.apache.tuscany.runtime.standalone.host;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;

import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.runtime.standalone.StandaloneRuntime;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class StandaloneRuntimeImpl extends AbstractRuntime implements StandaloneRuntime {

    protected void registerSystemComponents() throws InitializationException {
        super.registerSystemComponents();
        try {
            getComponentManager().registerJavaObject(StandaloneRuntimeInfo.STANDALONE_COMPONENT_URI,
                StandaloneRuntimeInfo.class,
                (StandaloneRuntimeInfo) getRuntimeInfo());
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }
    
    /**
     * Deploys the specified application SCDL.
     * 
     * @param compositeUri URI by which the composite is deployed.
     * @param applicationScdl Application SCDL that implements the composite.
     * @param applicationClassLoader Classloader used to deploy the composite.
     * @return The component context for the deployed composite.
     * @deprecated This is a hack for deployment and should be removed.
     */
    public ComponentContext deploy(URI compositeUri, URL applicationScdl, ClassLoader applicationClassLoader) throws Exception {
        
        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(applicationScdl);
        impl.setClassLoader(applicationClassLoader);

        ComponentDefinition<CompositeImplementation> definition =
            new ComponentDefinition<CompositeImplementation>(compositeUri, impl);
        
        return getDeployer().deploy(null, definition).getComponentContext();
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
