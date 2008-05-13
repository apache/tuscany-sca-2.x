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

package org.apache.tuscany.sca.assembly.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.DomainBuilder;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * Implementation of a DomainBuilder.
 *
 * @version $Date$ $Revision$
 */
public class DomainWireBuilderImpl implements DomainBuilder {
    
    public DomainWireBuilderImpl(AssemblyFactory assemblyFactory,
            SCABindingFactory scaBindingFactory,
            IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
            InterfaceContractMapper interfaceContractMapper,
            Monitor monitor) {
    }
    
    public String getComponentNameFromReference(String referenceName){
        // Extract the component name
        String componentName = referenceName;
        int i = referenceName.indexOf('/');
        if (i != -1) {
            componentName = referenceName.substring(0, i);
        } 
        
        return componentName;
    }
    
    public String getServiceNameFromReference(String referenceName){
        // Extract the component name
        String serviceName = null;
        int i = referenceName.indexOf('/');
        if (i != -1) {
            serviceName = referenceName.substring(i + 1);

        } 
        return serviceName;
    }
    
    
    public List<Reference> findReferenceForService(Composite composite, String serviceName){

        List<Reference> referenceList = new ArrayList<Reference>();
        
        String componentName = getComponentNameFromReference(serviceName);
        
        for (Reference reference: composite.getReferences()) {
            for (ComponentService componentService : reference.getTargets()){
                if (componentService.getName().equals(serviceName) || 
                    componentService.getName().equals(componentName)) {
                    referenceList.add(reference);                    
                }
            }
        }
        
        for (Component component: composite.getComponents()) {
            for (ComponentReference reference: component.getReferences()) {
                for (ComponentService componentService : reference.getTargets()){
                    if (componentService.getName().equals(serviceName) || 
                        componentService.getName().equals(componentName)) {
                        referenceList.add(reference);                 
                    }
                }
            }
        }          

        return referenceList;
    }
    
    public List<Reference> findDomainLevelReferenceForService(Composite composite, String referenceName){
        List<Reference> referenceList = new ArrayList<Reference>();
        
        for (Composite tmpComposite : composite.getIncludes()) { 
            List<Reference> tmpReferenceList = findReferenceForService(tmpComposite, referenceName);
            
            referenceList.addAll(tmpReferenceList);
        } 
        
        return referenceList;
    }    

    public Service findServiceForReference(Composite composite, String referenceName){
        
        String componentName = getComponentNameFromReference(referenceName);
        String serviceName = getServiceNameFromReference(referenceName);

        for (Service service: composite.getServices()) {
            if (service.getName().equals(serviceName)){
                return service; 
            }
        }
        
        for (Component component: composite.getComponents()) {
            if (component.getName().equals(componentName)){
                if (component.getServices().size() > 1) {
                    for (Service service: component.getServices()) {
                        if (service.getName().equals(serviceName)){
                            return service; 
                        }
                    }
                } else if (component.getServices().size() == 1) {
                    return component.getServices().get(0);
                } 
            }
        }          

        return null;
    } 
    
    public Service findDomainLevelService(Composite composite, String referenceName){
        Service service = null;
        
        for (Composite tmpComposite : composite.getIncludes()) { 
            service = findServiceForReference(tmpComposite, referenceName);
            if (service != null) {
                break;
            }
        } 
        
        return service;
    }
    
    public void updateDomainLevelServiceURI(Composite domainLevelComposite, String referenceName, String bindingClassName, String URI){
        
        String componentName = getComponentNameFromReference(referenceName);
        String serviceName = getServiceNameFromReference(referenceName);
        
        // get the named service 
        Service service = null;
        for(Composite composite : domainLevelComposite.getIncludes()) {
            service = findServiceForReference(composite, referenceName);                
            if (service != null){
                break;
            }
        }

        if (service != null) {
            // find the named binding
            for (Binding binding : service.getBindings()){
                if (binding.getClass().getName().equals(bindingClassName)){
                    binding.setURI(URI);
                    break;
                }
            }        
        }   
    }

    public List<Composite> wireDomain(Composite domainLevelComposite){
        List<Composite> changedComposites = new ArrayList<Composite>();
        
        // process wires
        
        // autowire
        
        // wire by impl?
        
        // process all wired references
        for(Composite composite : domainLevelComposite.getIncludes()) {
            boolean compositeChanged = false;
            for(Component component : composite.getComponents()){
                for (Reference reference : component.getReferences()){
                    for (ComponentService targetService : reference.getTargets()){
                        String targetName = targetService.getName();
                        String componentName = getComponentNameFromReference(targetName);
                        String serviceName = getServiceNameFromReference(targetName);
                        
                        Service service = null;
                        Component serviceComponent = null;
                        
                        // find the real target service in the domain
                        for(Composite tmpComposite : domainLevelComposite.getIncludes()) {
                            for (Component tmpComponent: tmpComposite.getComponents()) {
                                if (tmpComponent.getName().equals(componentName)){
                                    serviceComponent = tmpComponent;
                                    if (tmpComponent.getServices().size() > 1) {
                                        for (Service tmpService: tmpComponent.getServices()) {
                                            if (tmpService.getName().equals(serviceName)){
                                                service = tmpService; 
                                                break;
                                            }
                                        }
                                    } else if (tmpComponent.getServices().size() == 1) {
                                        service = tmpComponent.getServices().get(0);
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if ( targetService.isUnresolved()){
                            
                            if (service != null){
                                // Find the binding already in use for this target
                                Binding binding = null;
                                
                                for (Binding tmpBinding : reference.getBindings()){
                                    if ((tmpBinding.getName() != null) &&
                                        (tmpBinding.getName().startsWith(reference.getName() + "#" + targetName))){
                                        binding = tmpBinding;
                                    }
                                }

                                // Resolve the binding that should be used for this target
                                // TODO - hang onto the old bindings at the domain level, i.e. 
                                //        don't rely on the target objects as we still need the
                                //        bindings if we are going to do autowiring 
                                List<Binding> source = targetService.getBindings();
                                List<Binding> target = service.getBindings();
                                Binding newBinding = BindingConfigurationUtil.matchBinding(serviceComponent, (ComponentService)service, source, target);
                                
                                // update the existing binding to the new binding if required
                                if (newBinding != null) {
                                    if (binding != null){
                                        // there is a binding already so see if the URI has changed
                                        if ((binding.getURI() == null) ||
                                            (!binding.getURI().equals(newBinding.getURI()))){
                                            binding.setURI(newBinding.getURI());
                                            compositeChanged = true;
                                        }
                                    } else {
                                        // this is a newly configured binding so add it
                                        reference.getBindings().add(newBinding);
                                        compositeChanged = true;
                                    }
                                }
                            } else {
                                // Do nothing - the target service hasn't been contributed yet
                            }
                        } else {
                            // find the reference binding with the right name 
                            for (Binding refBinding : reference.getBindings()){
                                if ((refBinding.getName() != null) &&
                                    (refBinding.getName().startsWith(reference.getName() + "#" + targetName))){
                                    // find the matching service binding
                                    for (Binding serviceBinding : service.getBindings()){
                                        if (refBinding.getClass() == serviceBinding.getClass()){
                                            refBinding.setURI(serviceBinding.getURI());
                                        }
                                    }
                                }  
                            }
                        }
                    }
                }
            }
          
            if (compositeChanged) {
                changedComposites.add(composite);
            }
        }       
        
        return changedComposites;
    }
    

}
