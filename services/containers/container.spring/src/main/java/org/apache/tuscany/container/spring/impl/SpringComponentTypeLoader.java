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
package org.apache.tuscany.container.spring.impl;

import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.sca.ScaServiceExporter;

import org.apache.tuscany.container.spring.config.ScaApplicationContext;
import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.host.RuntimeInfo;

/**
 * Loads a component type for a Spring <code>ApplicationContext</code>. The implementation creates a new instance of a
 * Spring application context which is configured with SCA namespace handlers for generating component type information
 *
 * @version $$Rev$$ $$Date$$
 */

public class SpringComponentTypeLoader extends ComponentTypeLoaderExtension<SpringImplementation> {
    public static final String SERVICE_BEAN_SUFFIX = ".SCAService";

    private org.apache.tuscany.host.RuntimeInfo runtimeInfo;

    public SpringComponentTypeLoader(@Autowire LoaderRegistry loaderRegistry, @Autowire RuntimeInfo runtimeInfo) {
        super(loaderRegistry);
        this.runtimeInfo = runtimeInfo;
    }

    @Override
    protected Class<SpringImplementation> getImplementationClass() {
        return SpringImplementation.class;
    }

    /**
     * Responsible for loading the Spring composite component type. The the application context is instantiated here as
     * it is needed to derive component type information. Since the component type is loaded per SCDL entry (i.e.
     * composite use) one application context instance will be created per Spring composite instance.
     */
    @SuppressWarnings("unchecked")
    public void load(CompositeComponent parent,
                     SpringImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {
        if (implementation.getComponentType() != null) {
            // FIXME hack since the builder registry loads the implementation type and the Spring implementation loader
            // needs to as well. The second call is done by the builder registry and we just ignore it.
            return;
        }
        Resource resource = implementation.getApplicationResource();
        SpringComponentType componentType = new SpringComponentType();
        // REVIEW andyp -- pass in deploymentContext.getClassLoader()?
        AbstractRefreshableApplicationContext ctx;
        if (runtimeInfo instanceof SpringRuntimeInfo) {
            ctx = ((SpringRuntimeInfo) runtimeInfo).getApplicationContext();
        } else {
            ctx = new ScaApplicationContext(resource, componentType);
        }
        componentType.setApplicationContext(ctx); // FIXME andyp@bea.com -- don't do this!

        // If there are <sca:service> elements, they define (and limit) the services exposed
        // in the componentType.
        String [] serviceBeanNames = ctx.getBeanNamesForType(ScaServiceExporter.class);
        for (String serviceBeanName : serviceBeanNames) {
            int nSuffix = serviceBeanName.indexOf(SERVICE_BEAN_SUFFIX);
            if (nSuffix == -1) {
                continue;
            }

            String serviceName = serviceBeanName.substring(0, nSuffix);
            ScaServiceExporter serviceBean = (ScaServiceExporter) ctx.getBean(serviceName);
            // REVIEW andyp -- use the class directly?
            String serviceTypeName = serviceBean.getServiceType().getName();
            try {
                Class serviceInterface = Class.forName(serviceTypeName, true, deploymentContext.getClassLoader());
                componentType.addServiceType(serviceName, serviceInterface);
                //ServiceDefinition service = createService(serviceInterface);
                //componentType.getServices().put(serviceName, service);
            } catch (ClassNotFoundException e) {
                throw new LoaderException(e);
            }
        }
        // if no service tags are specified, expose all beans
        componentType.setExposeAllBeans(componentType.getServiceTypes().isEmpty());
        implementation.setComponentType(componentType);
    }
}
