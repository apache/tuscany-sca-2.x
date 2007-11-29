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
    
    public void buildDomainComposite(Composite composite) {
        // apply wires
        
        // check component references
    }

    private String getComponentNameFromReference(String referenceName){
        // Extract the component name
        String componentName = referenceName;
        int i = referenceName.indexOf('/');
        if (i != -1) {
            componentName = referenceName.substring(0, i);
        } 
        
        return componentName;
    }
    
    private String getServiceNameFromReference(String referenceName){
        // Extract the component name
        String serviceName = null;
        int i = referenceName.indexOf('/');
        if (i != -1) {
            serviceName = referenceName.substring(i + 1);

        } 
        return serviceName;
    }
    
    
    public List<Reference> findReferenceForService(Composite composite, String referenceName, String bindingClass){

        List<Reference> referenceList = new ArrayList<Reference>();
        
        String componentName = getComponentNameFromReference(referenceName);
        String serviceName = getServiceNameFromReference(referenceName);
        
        for (Reference reference: composite.getReferences()) {
            for (ComponentService componentService : reference.getTargets()){
                for (Binding binding : componentService.getBindings()){
                    if ((binding.getClass().getName().equals(bindingClass)) &&
                        (binding.getURI().equals(referenceName))){
                        referenceList.add(reference);
                    }
                }
            }
        }
        
        for (Component component: composite.getComponents()) {
            for (ComponentReference reference: component.getReferences()) {
                for (ComponentService componentService : reference.getTargets()){
                    for (Binding binding : componentService.getBindings()){
                        if ((binding.getClass().getName().equals(bindingClass)) &&
                            (binding.getURI().equals(referenceName))){
                            referenceList.add(reference);
                        }
                    }
                }
            }
        }          

        return referenceList;
    }
    
    public List<Reference> findDomainLeveReferenceForService(Composite composite, String referenceName, String bindingClass){
        List<Reference> referenceList = new ArrayList<Reference>();
        
        for (Composite tmpComposite : composite.getIncludes()) { 
            List<Reference> tmpReferenceList = findReferenceForService(tmpComposite, referenceName, bindingClass);
            
            referenceList.addAll(tmpReferenceList);

        } 
        
        return referenceList;
    }
    

    public Service findService(Composite composite, String referenceName){
        
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
            service = findService(tmpComposite, referenceName);
            if (service != null) {
                break;
            }
        } 
        
        return service;
    }

}
