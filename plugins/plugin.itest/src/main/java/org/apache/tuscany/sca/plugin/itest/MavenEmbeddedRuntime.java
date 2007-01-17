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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.CompositeContext;

import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.sca.plugin.itest.TuscanyStartMojo.MavenEmbeddedArtifactRepository;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

/**
 * @version $Rev$ $Date$
 */
public class MavenEmbeddedRuntime extends AbstractRuntime {
    private CompositeContextImpl context;
    private CompositeComponent application;

    private ArtifactRepository artifactRepository;
    // leave untyped b/c of QDox error
    private Map extensions = new HashMap();

    public void addExtension(String extensionName, URL extentionSCDL) {
        extensions.put(extensionName, extentionSCDL);
    }

    protected void registerSystemComponents() throws InitializationException {
        super.registerSystemComponents();
        try {
            getSystemComponent().registerJavaObject(MavenRuntimeInfo.COMPONENT_NAME,
                MavenRuntimeInfo.class,
                (MavenRuntimeInfo) getRuntimeInfo());

            getSystemComponent().registerJavaObject(MavenEmbeddedArtifactRepository.COMPONENT_NAME,
                ArtifactRepository.class,
                artifactRepository);
        } catch (ComponentRegistrationException e) {
            throw new InitializationException(e);
        }
    }

    public void initialize() throws InitializationException {
        super.initialize();

        try {
            for (Object extensionName : extensions.keySet()) {
                deployExtension(getTuscanySystem(), getDeployer(), (String) extensionName, (URL) extensions.get(extensionName));
            }

            application = deployApplicationScdl(getDeployer(),
                getRuntime().getRootComponent(),
                getApplicationName(),
                getApplicationScdl(),
                getApplicationClassLoader());
            application.start();

            context = new CompositeContextImpl(application, getWireService());
        } catch (LoaderException e) {
            throw new InitializationException(e);
        } catch (BuilderException e) {
            throw new InitializationException(e);
        } catch (TargetResolutionException e) {
            throw new InitializationException(e);
        } catch (ComponentException e) {
            throw new InitializationException(e);
        }
    }

    protected void deployExtension(CompositeComponent composite, Deployer deployer, String extensionName, URL url)
        throws LoaderException, BuilderException, ComponentException {
        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
        URL scdlLocation;
        try {
            scdlLocation = new URL("jar:" + url.toExternalForm() + "!/META-INF/sca/default.scdl");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        implementation.setScdlLocation(scdlLocation);
        implementation.setClassLoader(new CompositeClassLoader(new URL[]{url}, getClass().getClassLoader()));

        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(extensionName, implementation);

        Component component = deployer.deploy(composite, definition);
        component.start();
    }

    public void destroy() {
        context = null;
        if (application != null) {
            application.stop();
            application = null;
        }
        super.destroy();
    }

    public CompositeContext getContext() {
        return context;
    }

    public void setArtifactRepository(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

}
