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

package org.apache.tuscany.sca.binding.sca.axis2;

import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * The very minimum node implementation to get these tests going without creating a dependency on 
 * any runtime/hosting implementation
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-20 14:53:40 +0100 (Thu, 20 Sep 2007) $
 */
public class TestNode  {
    
    private static final Logger logger = Logger.getLogger(TestNode.class.getName());    
    
    private String nodeName;
    private ReallySmallRuntime nodeRuntime;
    
    private ClassLoader cl = TestNode.class.getClassLoader();
    
    private Composite nodeComposite = null;
    private Composite appComposite = null;
    
    public TestNode(String nodeName)
      throws Exception {
        this.nodeName = nodeName;
              
        try {

            // create and start domainA
            nodeRuntime = new ReallySmallRuntime(cl);
            nodeRuntime.start();
                        
            // Create an in-memory domain level composite
            AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
            nodeComposite = assemblyFactory.createComposite();
            nodeComposite.setName(new QName(Constants.SCA10_NS, "domain"));
            nodeComposite.setURI("http://localhost");
            
            // add the top level composite into the composite activator
            nodeRuntime.getCompositeActivator().setDomainComposite(nodeComposite);                     

            // add a contribution to the domain
            ContributionService contributionService = nodeRuntime.getContributionService();          

            // find the current directory as a URL. This is where our contribution 
            // will come from
            URL contributionURL = Thread.currentThread().getContextClassLoader().getResource(nodeName + "/");

            // Contribute the SCA application
            Contribution contribution = contributionService.contribute("http://calculator", contributionURL, null, false);
            appComposite = contribution.getDeployables().get(0);

            // Add the deployable composite to the domain
            nodeComposite.getIncludes().add(appComposite);
            nodeRuntime.buildComposite(appComposite);
            nodeRuntime.getCompositeActivator().activate(appComposite);
            

        } catch (Exception ex) {
            System.err.println("Exception when creating node " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }         
    }
    
    public void start() {
        
        try {
            nodeRuntime.getCompositeActivator().start(appComposite);
        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
        }         
        
    }
    
    public void stop() {
        try {
            nodeRuntime.stop();
        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    } 
    
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    private <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI) {
        try {
            AssemblyFactory assemblyFactory = nodeRuntime.getAssemblyFactory();
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName(Constants.SCA10_TUSCANY_NS, "default"));
            RuntimeComponent component = (RuntimeComponent)assemblyFactory.createComponent();
            component.setName("default");
            component.setURI("default");
            nodeRuntime.getCompositeActivator().configureComponentContext(component);
            composite.getComponents().add(component);
            RuntimeComponentReference reference = (RuntimeComponentReference)assemblyFactory.createComponentReference();
            reference.setName("default");
            ModelFactoryExtensionPoint factories =
                nodeRuntime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
            InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
            interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(businessInterface));
            reference.setInterfaceContract(interfaceContract);
            component.getReferences().add(reference);
            reference.setComponent(component);
            SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
            SCABinding binding = scaBindingFactory.createSCABinding();
            binding.setURI(targetURI);
            reference.getBindings().add(binding);       
            return new ServiceReferenceImpl<B>(businessInterface, component, reference, binding, nodeRuntime
                .getProxyFactory(), nodeRuntime.getCompositeActivator());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {

        // Extract the component name
        String componentName;
        String serviceName;
        int i = name.indexOf('/');
        if (i != -1) {
            componentName = name.substring(0, i);
            serviceName = name.substring(i + 1);

        } else {
            componentName = name;
            serviceName = null;
        }

        // Lookup the component in the domain
        
        Component component = null;
        
        for (Composite composite: nodeComposite.getIncludes()) {
            for (Component componentLoop: composite.getComponents()) {
                if (componentLoop.getName().equals(componentName)) {
                    component = componentLoop;
                    break;
                }
            }
        }        
        if (component == null) {
            // The component is not local in the partition, try to create a remote service ref
            return createServiceReference(businessInterface, name);
        }
        RuntimeComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        if (serviceName != null) {
                            serviceName = "$promoted$" + component.getName() + "$slash$" + serviceName;
                        }
                        componentContext =
                            ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                        return componentContext.createSelfReference(businessInterface, compositeService
                            .getPromotedService());
                    }
                    break;
                }
            }
            // No matching service is found
            throw new ServiceRuntimeException("Composite service not found: " + name);
        } else {
            componentContext = ((RuntimeComponent)component).getComponentContext();
            if (serviceName != null) {
                return componentContext.createSelfReference(businessInterface, serviceName);
            } else {
                return componentContext.createSelfReference(businessInterface);
            }
        }
    }
   
}

