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
package org.apache.tuscany.sca.contribution.updater.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.updater.ComponentUpdater;
import org.apache.tuscany.sca.contribution.updater.CompositeUpdater;
import org.apache.tuscany.sca.contribution.updater.ContributionUpdater;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.contribution.updater.impl.ComponentUpdaterImpl;
import org.apache.tuscany.sca.contribution.updater.impl.CompositeUpdaterImpl;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;

public class ContributionUpdaterFactoryImpl implements ContributionUpdater {
    private AssemblyFactory assembly;
    private JavaInterfaceFactory javaFactory;
    private CompositeActivator compositeActivator;
    private CompositeBuilder compositeBuilder;
    private ExtensionPointRegistry registry;
    private ContributionService contribService;
    private InterfaceContractMapper mapper;

    public ContributionUpdaterFactoryImpl(ReallySmallRuntime runtime) {
        this.assembly = runtime.getAssemblyFactory();
        this.javaFactory = runtime.getExtensionPointRegistry()
                .getExtensionPoint(ModelFactoryExtensionPoint.class)
                .getFactory(JavaInterfaceFactory.class);
        this.compositeActivator = runtime.getCompositeActivator();
        this.compositeBuilder = runtime.getCompositeBuilder();
        this.registry = runtime.getExtensionPointRegistry();
        this.contribService = runtime.getContributionService();
        this.mapper = runtime.getInterfaceContractMapper();
    }

    public void setContributionService(ContributionService s) {
        this.contribService = s;
    }

    public ComponentUpdater getComponentUpdater(String contribURI,
            String compositeURI, String componentName) {

        return new ComponentUpdaterImpl(contribURI, compositeURI,
                componentName, assembly, javaFactory, compositeBuilder,
                compositeActivator, contribService, registry, mapper);
    }

    public CompositeUpdater getCompositeUpdater(String contribURI,
            String compositeURI) {
        return new CompositeUpdaterImpl(assembly, contribURI, compositeURI,
                compositeBuilder, compositeActivator, registry, contribService);
    }

}
