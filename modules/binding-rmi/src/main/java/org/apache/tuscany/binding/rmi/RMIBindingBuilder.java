/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi;

import java.net.URI;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.rmi.RMIHostExtensionPoint;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;

/**
 * Builds a Service or Reference for an RMI binding.
 *
 */

public class RMIBindingBuilder extends BindingBuilderExtension<RMIBinding> {

    private RMIHostExtensionPoint rmiHost;
    
    //track reference bindings and service bindings so that resources can be released
    // needed because the stop methods in ReferenceImpl and ServiceImpl aren't being called
    // TODO: revisit this as part of the lifecycle work
    private List<ReferenceBinding> referenceBindings = new ArrayList<ReferenceBinding>();
    private List<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();
    
    
    protected Class<RMIBinding> getBindingType() {
        return RMIBinding.class;
    }

    @Override
    public ReferenceBinding build(CompositeReference compositeReference, RMIBinding rmiBindingDefn, DeploymentContext context) throws BuilderException {

        URI name = URI.create(context.getComponentId() + "#" + compositeReference.getName());
        ReferenceBinding referenceBinding = new RMIReferenceBinding(name,
                                                                    makeTargetURI(rmiBindingDefn),
                                                                    rmiBindingDefn, 
                                                                    rmiHost);
        referenceBindings.add(referenceBinding); // track binding so that its resources can be released
        return referenceBinding;
    }

    @Override
    public ServiceBinding build(CompositeService compositeService, RMIBinding rmiBindingDefn, DeploymentContext context) throws BuilderException {
        
        URI name = URI.create(context.getComponentId() + "#" + compositeService.getName());
        ServiceBinding serviceBinding = new RMIServiceBinding<Remote>(name,
                                            rmiBindingDefn, 
                                            rmiHost, 
                                            compositeService.getInterfaceContract().getInterface());
        serviceBindings.add(serviceBinding); // track binding so that its resources can be released
        return serviceBinding;
    }

    public RMIHostExtensionPoint getRmiHost() {
        return rmiHost;
    }

    public void setRmiHost(RMIHostExtensionPoint rmiHost) {
        this.rmiHost = rmiHost;
    }
    
    private URI makeTargetURI(RMIBinding rmiBinding) {
        StringBuffer sb = new StringBuffer();
        sb.append(RMIBindingConstants.FWD_SLASH);
        sb.append(RMIBindingConstants.FWD_SLASH);
        if (rmiBinding.getRmiHostName() != null && rmiBinding.getRmiHostName().length() > 0) {
            sb.append(rmiBinding.getRmiHostName());
        } else {
            sb.append(RMIBindingConstants.LOCAL_HOST);
        }
        
        if (rmiBinding.getRmiPort() != null && rmiBinding.getRmiPort().length() > 0) {
            sb.append(RMIBindingConstants.COLON);
            sb.append(rmiBinding.getRmiPort());
        } 
        sb.append(RMIBindingConstants.FWD_SLASH);
        sb.append(rmiBinding.getRmiServiceName());
        
        return URI.create(sb.toString());
    }
    
    //  release resources held by bindings
    // called by stop method of Axis2ModuleActivator
    // needed because the stop methods in ReferenceImpl and ServiceImpl aren't being called
    // TODO: revisit this as part of the lifecycle work
    protected void destroy() {
       for (ReferenceBinding binding : referenceBindings) {
          binding.stop();
       }
       for (ServiceBinding binding : serviceBindings) {
          binding.stop();
       }
    }

}
