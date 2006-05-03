/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.wire.service;

import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.spi.wire.WireFactoryFactory;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * The default implementation of a <code>WireFactoryFactory</code>
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
@Service(interfaces = {org.apache.tuscany.core.wire.service.WireFactoryService.class})
public class DefaultWireFactoryService implements org.apache.tuscany.core.wire.service.WireFactoryService {

    private MessageFactory messageFactory;
    private WireFactoryFactory wireFactoryFactory;
    private PolicyBuilderRegistry policyRegistry;


    public DefaultWireFactoryService() {

    }

    public DefaultWireFactoryService(MessageFactory messageFactory, WireFactoryFactory wireFactoryFactory, PolicyBuilderRegistry registry) {
        this.messageFactory = messageFactory;
        this.wireFactoryFactory = wireFactoryFactory;
        this.policyRegistry = registry;
    }

    @Autowire
    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Autowire
    public void setWireFactoryService(WireFactoryFactory wireFactoryFactory) {
        this.wireFactoryFactory = wireFactoryFactory;
    }

    @Autowire
    public void setPolicyRegistry(PolicyBuilderRegistry policyRegistry) {
        this.policyRegistry = policyRegistry;
    }

    @Init(eager = true)
    public void init() {
    }

    public List<SourceWireFactory> createSourceFactory(ConfiguredReference configuredReference) throws BuilderConfigException {
        String referenceName = configuredReference.getPort().getName();
        Class interfaze;
        // FIXME hack for NPE when entry points with no set service contract on their configuredReference
        if (configuredReference.getPort().getServiceContract() != null){
            interfaze = configuredReference.getPort().getServiceContract().getInterface();
        }else if(configuredReference.getPart() instanceof EntryPoint){
            interfaze = ((EntryPoint)configuredReference.getPart()).getConfiguredService().getPort().getServiceContract().getInterface();
        }else{
            BuilderConfigException bce = new BuilderConfigException("No interface found on configured reference");
            bce.setIdentifier(configuredReference.getName());
            throw bce;
        }
        List<SourceWireFactory> wireFactories = new ArrayList<SourceWireFactory>();
        List<WireSourceConfiguration> wireConfigurations = new ArrayList<WireSourceConfiguration>();
        for (ConfiguredService configuredService : configuredReference.getTargetConfiguredServices()) {
            String targetCompName = configuredService.getPart().getName();
            String targetSerivceName = configuredService.getPort().getName();
            QualifiedName targetName = new QualifiedName(targetCompName + QualifiedName.NAME_SEPARATOR + targetSerivceName);
            SourceWireFactory wireFactory = wireFactoryFactory.createSourceWireFactory();
            Map<Method, SourceInvocationConfiguration> iConfigMap = new HashMap<Method, SourceInvocationConfiguration>();
            Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
            for (Method method : javaMethods) {
                SourceInvocationConfiguration iConfig = new SourceInvocationConfiguration(method);
                iConfigMap.put(method, iConfig);
            }
            WireSourceConfiguration wireConfiguration = new WireSourceConfiguration(referenceName, targetName, iConfigMap, interfaze.getClassLoader(),
                    messageFactory);
            wireConfigurations.add(wireConfiguration);
            wireFactory.setBusinessInterface(interfaze);
            wireFactory.setConfiguration(wireConfiguration);
            wireFactories.add(wireFactory);
        }
        if (policyRegistry != null) {
            // invoke policy builders
            policyRegistry.buildSource(configuredReference, wireConfigurations);
        }
        return wireFactories;

    }

    public TargetWireFactory createTargetFactory(ConfiguredService configuredService) {
        org.apache.tuscany.model.assembly.Service service = configuredService.getPort();
        Class interfaze = service.getServiceContract().getInterface();
        QualifiedName targetName = new QualifiedName(configuredService.getPart().getName() + QualifiedName.NAME_SEPARATOR
                + service.getName());

        Map<Method, TargetInvocationConfiguration> iConfigMap = new MethodHashMap<TargetInvocationConfiguration>();
        TargetWireFactory wireFactory = wireFactoryFactory.createTargetWireFactory();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            TargetInvocationConfiguration iConfig = new TargetInvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        WireTargetConfiguration wireConfiguration = new WireTargetConfiguration(targetName, iConfigMap, interfaze.getClassLoader(), messageFactory);
        wireFactory.setBusinessInterface(interfaze);
        wireFactory.setConfiguration(wireConfiguration);
        if (policyRegistry != null) {
            // invoke policy builders
            policyRegistry.buildTarget(configuredService, wireConfiguration);
        }
        // add tail interceptor
        for (TargetInvocationConfiguration iConfig : wireFactory.getConfiguration().getInvocationConfigurations().values()) {
            iConfig.addInterceptor(new InvokerInterceptor());
        }
        return wireFactory;
    }


}
