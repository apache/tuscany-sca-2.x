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

import javax.xml.stream.XMLInputFactory;

import org.osoa.sca.SCA;

import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;

/**
 * @version $Rev$ $Date$
 */
public class MavenEmbeddedRuntime extends AbstractRuntime {
    private CompositeContextImpl context;
    private RuntimeComponent runtime;
    private CompositeComponent systemComponent;
    private CompositeComponent tuscanySystem;
    private CompositeComponent application;

    public void initialize() {
        ClassLoader bootClassLoader = getClass().getClassLoader();

        // Read optional system monitor factory classname
        MonitorFactory mf = getMonitorFactory();

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", bootClassLoader);

        Bootstrapper bootstrapper = new DefaultBootstrapper(mf, xmlFactory);
        runtime = bootstrapper.createRuntime();
        runtime.start();
        systemComponent = runtime.getSystemComponent();

        // register the runtime info provided by the host
        RuntimeInfo runtimeInfo = getRuntimeInfo();
        systemComponent.registerJavaObject(RuntimeInfo.COMPONENT_NAME, RuntimeInfo.class, runtimeInfo);
        systemComponent.registerJavaObject(MavenRuntimeInfo.COMPONENT_NAME,
                                           MavenRuntimeInfo.class,
                                           (MavenRuntimeInfo) runtimeInfo);

        // register the monitor factory provided by the host
        systemComponent.registerJavaObject("MonitorFactory", MonitorFactory.class, mf);

        systemComponent.start();

        try {
            // deploy the system scdl
            Deployer deployer = bootstrapper.createDeployer();
            tuscanySystem = deploySystemScdl(deployer,
                systemComponent,
                ComponentNames.TUSCANY_SYSTEM,
                getSystemScdl(),
                bootClassLoader);
            tuscanySystem.start();

            // switch to the system deployer
            deployer = (Deployer) tuscanySystem.getSystemChild("deployer").getServiceInstance();

            application = deployApplicationScdl(deployer,
                runtime.getRootComponent(),
                getApplicationName(),
                getApplicationScdl(),
                getApplicationClassLoader());
            application.start();

            context = new CompositeContextImpl(application);
        } catch (LoaderException e) {
            // FIXME do something with this
            e.printStackTrace();
        }
    }

    public void destroy() {
        context = null;
        if (application != null) {
            application.stop();
            application = null;
        }
        if (tuscanySystem != null) {
            tuscanySystem.stop();
            tuscanySystem = null;
        }
        if (systemComponent != null) {
            systemComponent.stop();
            systemComponent = null;
        }
        if (runtime != null) {
            runtime.stop();
            runtime = null;
        }
    }

    public SCA getContext() {
        return context;
    }

}
