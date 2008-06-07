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

package org.apache.tuscany.sca.runtime.tomcat;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.catalina.core.StandardContext;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.builder.DefaultEndpointBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.endpointresolver.EndpointResolver;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactory;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactoryExtensionPoint;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.DefaultSCADomain;
import org.apache.tuscany.sca.host.webapp.WebAppServletHost;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;


/** 
 * The endpoint resolver allows unresolved endpoints to be plumbed into
 * the runtime start and message send processing as a hook to late resolution
 * of target services
 * 
 * @version $Rev$ $Date$
 */
public class EndpointResolverImpl implements EndpointResolver {

    private final static Logger logger = Logger.getLogger(EndpointResolverImpl.class.getName());

    private Endpoint endpoint;
    private List<EndpointResolver> endpointResolvers = new ArrayList<EndpointResolver>();    

    public EndpointResolverImpl(ExtensionPointRegistry extensionPoints,
                                Endpoint endpoint) {
        this.endpoint = endpoint;
        
        EndpointResolverFactoryExtensionPoint resolverFactories = 
            extensionPoints.getExtensionPoint(EndpointResolverFactoryExtensionPoint.class);
        
        for (Binding binding : endpoint.getCandidateBindings()){
            EndpointResolverFactory resolverFactory = resolverFactories.getEndpointResolverFactory(binding.getClass());
            
            // if the binding in question has a endpoint resolver factory they try and 
            // create an endpoint resolver
            if (resolverFactory != null){
                EndpointResolver resolver = resolverFactory.createEndpointResolver(endpoint, binding);
                
                if (resolver != null){
                    endpointResolvers.add(resolver);
                }
            }
        }
    }
    
    public void start(){
        // do nothing
    }
    
    public void resolve() {
        if (endpoint.isUnresolved()){
            logger.info("resolving endpoint: " + endpoint.getTargetName());

            Component target = findTarget();
            if (target != null) {
                logger.info("endpoint target found: " + endpoint.getTargetName() + " component " + target);
                resolveEndpoint(target);
            } else {
                logger.info("endpoint target not found: " + endpoint.getTargetName());
            }
            
        }
    }

    protected void resolveEndpoint(Component targetComponent) {
        
        endpoint.setTargetComponent(targetComponent);
        endpoint.setTargetComponentService(targetComponent.getServices().get(0)); // TODO real service

        DefaultEndpointBuilder ebi = new DefaultEndpointBuilder(new Monitor() {
            public void problem(Problem problem) {
                logger.warning(problem.toString());
            }});

        ebi.build(endpoint);
    }

    protected Component findTarget() {
        for (StandardContext sc : TuscanyHost.scaApps) {
            SCADomain scaDomain = (SCADomain)sc.getServletContext().getAttribute(WebAppServletHost.SCA_DOMAIN_ATTRIBUTE);
            if (scaDomain != null) {
                Component component = ((DefaultSCADomain)scaDomain).getComponent(endpoint.getTargetName());
                if ( component != null) {
                    return component;
                }
            }
        }
        return null;
    }

    public void stop() {
        if (!endpoint.isUnresolved()){
            // Currently the CompositeActivator stop() should take care of the providers and 
            // wires that have been added. 
        }
    }

}
