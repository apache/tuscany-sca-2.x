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
package org.apache.tuscany.core.services.extension;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.TuscanyException;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.services.VoidService;
import org.apache.tuscany.spi.services.info.RuntimeInfo;

import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;

/**
 * Service that extends the runtime by loading composites located in a directory.
 *
 * @version $Rev$ $Date$
 */
public class DirectoryScanExtender implements VoidService {
    private String path;
    private RuntimeInfo runtimeInfo;
    private Deployer deployer;
    private CompositeComponent parent;

    @Property
    public void setPath(String path) {
        this.path = path;
    }

    @Autowire
    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    @Autowire
    public void setDeployer(Deployer deployer) {
        this.deployer = deployer;
    }

    @Autowire
    public void setParent(CompositeComponent parent) {
        this.parent = parent;
    }

    @Init(eager = true)
    public void init() {
        assert runtimeInfo != null;
        File extensionDir = new File(runtimeInfo.getInstallDirectory(), path);
        if (!extensionDir.isDirectory()) {
            // we don't have an extension directory, there's nothing to do
            return;
        }

        File[] files = extensionDir.listFiles();
        for (File file : files) {
            deployExtension(file);
        }
    }

    private void deployExtension(File file) {
        // extension name is file name less any extension
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            name = name.substring(0, dot);
        }

        // todo do we want to support unpacked directories as extensions?
        // get the URL of the JAR file and the SCDL inside
        URL extensionURL;
        URL scdl;
        try {
            extensionURL = new URL("jar:" + file.toURI().toURL() + "!/");
            scdl = new URL(extensionURL, "META-INF/sca/default.scdl");
        } catch (MalformedURLException e) {
            // file may not be a JAR file
            return;
        }

        // assume this class's ClassLoader is the Tuscany system classloader
        // and use it as the extension's parent ClassLoader
        ClassLoader extensionCL = new CompositeClassLoader(new URL[]{extensionURL}, getClass().getClassLoader());

        // create a ComponentDefinition to represent the component we are going to deploy
        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
        implementation.setScdlLocation(scdl);
        implementation.setClassLoader(extensionCL);
        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(name, implementation);

        try {
            Component<?> component = deployer.deploy(parent, definition);
            component.start();
        } catch (LoaderException e) {
            // FIXME handle the exception
            e.printStackTrace();
        } catch (TuscanyException e) {
            // FIXME handle the exception
            e.printStackTrace();
        }
    }
}
