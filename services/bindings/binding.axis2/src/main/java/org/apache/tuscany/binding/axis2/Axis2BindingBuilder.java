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

import javax.wsdl.Port;
import javax.wsdl.PortType;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.idl.wsdl.InterfaceWSDLIntrospector;
import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;

/**
 * Builds a {@link org.osoa.sca.annotations.Service} or {@link org.apache.tuscany.spi.component.Reference} configured
 * with the Axis2 binding
 * 
 * @version $Rev$ $Date$
 */
public class Axis2BindingBuilder extends BindingBuilderExtension<WebServiceBinding> {
    private static final String OM_DATA_BINDING = OMElement.class.getName();

    private ServletHost servletHost;

    private ConfigurationContext configContext;

    private InterfaceWSDLIntrospector introspector;

    public Axis2BindingBuilder() {
        initAxis();
    }

    @Autowire(required = false)
    public void setServletHost(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    /**
     * @param introspector the introspector to set
     */
    @Autowire
    public void setIntrospector(InterfaceWSDLIntrospector introspector) {
        this.introspector = introspector;
    }

    @SuppressWarnings("unchecked")
    public Service build(
            CompositeComponent parent,
            BoundServiceDefinition<WebServiceBinding> serviceDefinition,
            DeploymentContext deploymentContext) {

        try {
            // Set the default databinding
            ServiceContract<?> outboundContract = serviceDefinition.getServiceContract();
            if (WSDLServiceContract.class.isInstance(outboundContract)) {
                outboundContract.setDataBinding(OM_DATA_BINDING);
            }

            // FIXME: We need to define how the WSDL PortType is honored in the case that
            // both the binding.ws and interface.wsdl are in place.
            // The WSDL portType from the WSDL Port decides the incoming SOAP message format
            // There are also cases that interface.java is used.
            
            ServiceContract<?> inboundContract = null;
            WebServiceBinding wsBinding = serviceDefinition.getBinding();
            Port port = wsBinding.getWSDLPort();
            if (port == null) {
                // FIXME: [rfeng] No WSDL is referenced by binding.ws, we need to create one from
                // the outbound service contract if it's JavaServiceContract
                inboundContract = outboundContract;
            }            
            
            PortType portType = wsBinding.getWSDLPort().getBinding().getPortType();
            inboundContract = introspector.introspect(portType);
            
            // FIXME:  
            inboundContract.setInterfaceClass(serviceDefinition.getServiceContract().getInterfaceClass());
            inboundContract.setDataBinding(OM_DATA_BINDING);
            inboundContract.setCallbackName(serviceDefinition.getServiceContract().getCallbackName());
            inboundContract.setInteractionScope(serviceDefinition.getServiceContract().getInteractionScope());
            try {
                wireService.checkCompatibility(inboundContract, outboundContract, true);
            } catch (IncompatibleServiceContractException e) {
                throw new Axis2BindingBuilderRuntimeException(e);
            }
            
            Service service = new Axis2Service(serviceDefinition.getName(), outboundContract, parent, wireService, wsBinding,
                    servletHost, configContext);
            service.setBindingServiceContract(inboundContract);
            
            return service;
            
        } catch (InvalidServiceContractException e) {
            throw new Axis2BindingBuilderRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Reference build(
            CompositeComponent parent,
            BoundReferenceDefinition<WebServiceBinding> boundReferenceDefinition,
            DeploymentContext deploymentContext) {

        try {
            // Set the default binding
            ServiceContract<?> inboundContract = boundReferenceDefinition.getServiceContract();
            if (WSDLServiceContract.class.isInstance(inboundContract)) {
                inboundContract.setDataBinding(OM_DATA_BINDING);
            }
            
            // FIXME: We need to define how the WSDL PortType is honored in the case that
            // both the binding.ws and interface.wsdl are in place
            // The WSDL portType from the WSDL Port decides the incoming SOAP message format

            ServiceContract<?> outboundContract = inboundContract;
            WebServiceBinding wsBinding = boundReferenceDefinition.getBinding();
            Port port = wsBinding.getWSDLPort();
            if (port == null) {
                // FIXME: [rfeng] No WSDL is referenced by binding.ws, we need to create one from
                // the inbound service contract if it's JavaServiceContract
                outboundContract = inboundContract;
            }
            PortType portType = port.getBinding().getPortType();
            outboundContract = introspector.introspect(portType);
            
            // Set the default databinding
            outboundContract.setDataBinding(OM_DATA_BINDING);
            
            try {
                wireService.checkCompatibility(inboundContract, outboundContract, true);
            } catch (IncompatibleServiceContractException e) {
                throw new Axis2BindingBuilderRuntimeException(e);
            }
            
            Reference reference = new Axis2Reference(boundReferenceDefinition.getName(), parent, wireService, wsBinding,
                    inboundContract);
            reference.setBindingServiceContract(outboundContract);
            
            return reference;
            
        } catch (InvalidServiceContractException e) {
            throw new Axis2BindingBuilderRuntimeException(e);
        }
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
