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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem.Severity;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;

public class DomainWireBuilderImpl {
    
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
                } else {
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
/*
            // update any references that refer to this service
            List<Reference> referenceList = new ArrayList<Reference>();
            
            for (Composite composite : domainLevelComposite.getIncludes()){
                referenceList.addAll(findReferenceForService(composite, referenceName));
            }
           
            for (Reference reference : referenceList){
                // find if a  bindings that are already resolved against the target
                Binding binding = null;
                
                for (Binding tmpBinding : reference.getBindings()){
                    if (tmpBinding.getClass().getName().equals(bindingClassName) &&
                        (tmpBinding.getName().equals(referenceName) || 
                                tmpBinding.getName().equals(componentName))){
                        binding = tmpBinding;
                    }
                }
                
                if (binding == null) {
                    // find the named target and if it's not already resolved resolve it  
                    for (ComponentService targetService : reference.getTargets()){
                        if ( (targetService.getName().equals(referenceName) || targetService.getName().equals(componentName) ) &&
                             targetService.isUnresolved()){
                            
                            List<Binding> source = targetService.getBindings();
                            List<Binding> target = service.getBindings();
                            
                            // Resolve the binding that will be used
                            // TODO - this cast to ComponentReference should not be here as the service
                            //        could be a composite level service
                            binding = BindingUtil.matchBinding(null, (ComponentService)service, source, target);
                            
                            if ( binding != null){
                                // put the selected binding into the reference 
                                Binding clonedBinding = binding;//.clone();
                                reference.getBindings().remove(binding);
                                reference.getBindings().add(clonedBinding);
                            }
                        }
                    }
                }
                
                if ( binding != null){
                    binding.setURI(URI);
                    binding.setName(referenceName);
                }   
            }
 */            
        }   
    }

    public List<Composite> wireDomain(Composite domainLevelComposite){
        List<Composite> changedComposites = new ArrayList<Composite>();
        
        // process included composites
        for(Composite composite : domainLevelComposite.getIncludes()) {
            boolean compositeChanged = false;
            for(Component component : composite.getComponents()){
                for (Reference reference : component.getReferences()){
                    for (ComponentService targetService : reference.getTargets()){
                        String targetName = targetService.getName();
                        String componentName = getComponentNameFromReference(targetName);
                        
                        Service service = findDomainLevelService(domainLevelComposite, targetName);
                        
                        if ( targetService.isUnresolved()){
                            
                            if (service != null){
                                // Find the binding already in use for this target
                                Binding binding = null;
                                
                                for (Binding tmpBinding : reference.getBindings()){
                                    if ((tmpBinding.getName().equals(targetName) || 
                                         tmpBinding.getName().equals(componentName))){
                                        binding = tmpBinding;
                                    }
                                }

                                // Resolve the binding that should be used for this target
                                List<Binding> source = targetService.getBindings();
                                List<Binding> target = service.getBindings();
                                Binding newBinding = BindingUtil.matchBinding(null, (ComponentService)service, source, target);
                                
                                // update the existing binding to the new binding if required
                                if (newBinding != null) {
                                    if (binding != null){
                                        // there is a binding already so see if the URI has changed
                                        if (!binding.getURI().equals(newBinding.getURI())){
                                            binding.setURI(newBinding.getURI());
                                            compositeChanged = true;
                                        }
                                    } else {
                                        // this is a newly configured binding so add it
                                        Binding clonedBinding = newBinding;//.clone();
                                        clonedBinding.setName(targetName);
                                        reference.getBindings().add(clonedBinding);
                                        compositeChanged = true;
                                    }
                                }
                            } else {
                                // Do nothing - the target service hasn't been contributed yet
                            }
                        } else {
                            // this is a wired reference within a composite. check that the 
                            // reference and service binding uris match
                            
                            // TODO - If we had the name of the target service stored on the 
                            //        binding we could go straight to it
                            // Resolve the binding that should be used for this target
                            List<Binding> source = service.getBindings();
                            List<Binding> target = reference.getBindings();
                            Binding newBinding = BindingUtil.matchBinding(null, (ComponentService)service, source, target);
                            
                            if (newBinding instanceof SCABinding){
                                // do nothing as it will already be sorted
                            } else {
                                // find this binding in the reference and copy the URL
                                for (Binding binding : reference.getBindings()){
                                    if ((binding.getClass() == newBinding.getClass()) && 
                                        (!binding.getURI().equals(newBinding.getURI()))){
                                        binding.setURI(newBinding.getURI());
                                        compositeChanged = true;
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
        
        // process wires
        
        // autowire
        
        // wire by impl?
        
        
        return changedComposites;
    }
    

}
