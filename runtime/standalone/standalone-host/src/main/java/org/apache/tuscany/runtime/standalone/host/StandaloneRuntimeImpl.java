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

import javax.xml.stream.XMLInputFactory;

import org.osoa.sca.SCA;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;

/**
 * @version $Rev$ $Date$
 */
public class StandaloneRuntimeImpl extends AbstractRuntime {
    private CompositeContextImpl context;
    private RuntimeComponent runtime;
    private CompositeComponent systemComponent;
    private CompositeComponent tuscanySystem;
    private CompositeComponent application;

    public void initialize() throws InitializationException {
        ClassLoader bootClassLoader = getClass().getClassLoader();

        // Read optional system monitor factory classname
        MonitorFactory mf = getMonitorFactory();

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", bootClassLoader);

        TuscanyManagementService managementService = null;
        Bootstrapper bootstrapper = new DefaultBootstrapper(mf, xmlFactory, managementService);
        runtime = bootstrapper.createRuntime();
        runtime.start();
        systemComponent = runtime.getSystemComponent();

        // register the runtime info provided by the host
        RuntimeInfo runtimeInfo = getRuntimeInfo();
        try {
            systemComponent.registerJavaObject(RuntimeInfo.COMPONENT_NAME, RuntimeInfo.class, runtimeInfo);
            systemComponent.registerJavaObject(StandaloneRuntimeInfo.COMPONENT_NAME,
                                               StandaloneRuntimeInfo.class,
                                               (StandaloneRuntimeInfo)runtimeInfo);

            // register the monitor factory provided by the host
            systemComponent.registerJavaObject("MonitorFactory", MonitorFactory.class, mf);
        } catch (ComponentRegistrationException e) {
            throw new InitializationException(e);
        }

        systemComponent.start();

        try {
            // deploy the system scdl
            Deployer deployer = bootstrapper.createDeployer();
            tuscanySystem =
                deploySystemScdl(deployer,
                                 systemComponent,
                                 ComponentNames.TUSCANY_SYSTEM,
                                 getSystemScdl(),
                                 bootClassLoader);
            tuscanySystem.start();

            // switch to the system deployer
            SCAObject deployerComponent = tuscanySystem.getSystemChild(ComponentNames.TUSCANY_DEPLOYER);
            if (!(deployerComponent instanceof AtomicComponent)) {
                throw new InitializationException("Deployer must be an atomic component");
            }
            deployer = (Deployer)((AtomicComponent)deployerComponent).getTargetInstance();

            SCAObject wireServiceComponent = tuscanySystem.getSystemChild(ComponentNames.TUSCANY_WIRE_SERVICE);
            if (!(wireServiceComponent instanceof AtomicComponent)) {
                throw new InitializationException("WireService must be an atomic component");
            }
            WireService wireService = (WireService)((AtomicComponent)wireServiceComponent).getTargetInstance();

            if (getApplicationScdl() != null) {
                application =
                    deployApplicationScdl(deployer,
                                          runtime.getRootComponent(),
                                          getApplicationName(),
                                          getApplicationScdl(),
                                          getApplicationClassLoader());
                application.start();
            }

            context = new CompositeContextImpl(application, wireService);
        } catch (LoaderException ex) {
            throw new InitializationException(ex);
        } catch (BuilderException ex) {
            throw new InitializationException(ex);
        } catch (ComponentException ex) {
            throw new InitializationException(ex);
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
