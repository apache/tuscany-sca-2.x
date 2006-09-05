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
package org.apache.tuscany.binding.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

import commonj.sdo.helper.TypeHelper;

/**
 * Builds a {@link org.osoa.sca.annotations.Service} or {@link org.apache.tuscany.spi.component.Reference} configured with the Axis2 binding
 * 
 * @version $Rev$ $Date$
 */
public class Axis2BindingBuilder extends BindingBuilderExtension<WebServiceBinding> {

    private ServletHost servletHost;

    private ConfigurationContext configContext;

    public Axis2BindingBuilder() {
        initAxis();
    }

    @Autowire(required=false)
    public void setServletHost(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    @SuppressWarnings("unchecked")
    public Service<?> build(CompositeComponent parent, BoundServiceDefinition<WebServiceBinding> serviceDefinition, DeploymentContext deploymentContext) {

        WebServiceBinding wsBinding = serviceDefinition.getBinding();
        Class<?> interfaze = serviceDefinition.getServiceContract().getInterfaceClass();
        TypeHelper typeHelper = (TypeHelper) deploymentContext.getExtension(TypeHelper.class.getName());
        if(typeHelper==null) typeHelper = TypeHelper.INSTANCE;

        return new Axis2Service(serviceDefinition.getName(), interfaze, parent, wireService, wsBinding, servletHost, configContext, typeHelper);
    }

    @SuppressWarnings("unchecked")
    public Reference<?> build(CompositeComponent parent, BoundReferenceDefinition<WebServiceBinding> boundReferenceDefinition,
            DeploymentContext deploymentContext) {
        
        WebServiceBinding wsBinding = boundReferenceDefinition.getBinding();
        TypeHelper typeHelper = (TypeHelper) deploymentContext.getExtension(TypeHelper.class.getName());
        if(typeHelper==null) typeHelper = TypeHelper.INSTANCE;

        return new Axis2Reference(boundReferenceDefinition.getName(), parent, wireService, wsBinding, boundReferenceDefinition.getServiceContract(), typeHelper);
    }

    protected Class<WebServiceBinding> getBindingType() {
        return WebServiceBinding.class;
    }
    
    protected void initAxis() {
        // TODO: Fix classloader switching. See TUSCANY-647 
        // TODO: also consider having a system component wrapping the Axis2 ConfigContext
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = getClass().getClassLoader();
        try { 
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(scl);
            }
            try {
                this.configContext = new TuscanyAxisConfigurator().getConfigurationContext();
            } catch (AxisFault e) {
                throw new BuilderConfigException(e);
            }
        } finally {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }
}
