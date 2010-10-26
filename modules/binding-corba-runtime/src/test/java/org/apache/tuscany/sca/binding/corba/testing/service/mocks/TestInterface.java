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

package org.apache.tuscany.sca.binding.corba.testing.service.mocks;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Mock TestInterface implementation. Only few methods needs to be implemented.
 */
public class TestInterface implements JavaInterface {

    private List<Operation> operations;
    private Class<?> javaClass;

    public TestInterface(List<Operation> opearations, Class<?> javaClass) {
        this.operations = opearations;
        this.javaClass = javaClass;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public boolean isConversational() {
        return false;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isRemotable() {
        return false;
    }

    public void resetDataBinding(String dataBinding) {

    }

    public void setConversational(boolean conversational) {

    }

    public void setDefaultDataBinding(String dataBinding) {

    }

    public void setRemotable(boolean remotable) {

    }

    public List<PolicySet> getApplicablePolicySets() {
        return null;
    }

    public List<PolicySet> getPolicySets() {
        return null;
    }

    public List<Intent> getRequiredIntents() {
        return null;
    }

    @Override
    public Object clone() {
        return null;
    }

    public Class<?> getCallbackClass() {
        return null;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public String getName() {
        return null;
    }

    public QName getQName() {
        return null;
    }

    public void setCallbackClass(Class<?> arg0) {
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    public void setName(String arg0) {   
    }

    public void setQName(QName arg0) {
        
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean arg0) {   
    }

    public Map<Object, Object> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isRemotableSet() {
        // TODO Auto-generated method stub
        return false;
    }

    public void resetInterfaceInputTypes(Interface newInterface) {
        // TODO Auto-generated method stub
        
    }

    public void resetInterfaceOutputTypes(Interface newInterface) {
        // TODO Auto-generated method stub
        
    }

    public ExtensionType getExtensionType() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setExtensionType(ExtensionType type) {
        // TODO Auto-generated method stub
        
    }
    
    public String getJAXWSJavaInterfaceName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setJAXWSJavaInterfaceName(String javaInterfaceName) {
        // TODO Auto-generated method stub
        
    }
    
    public String getJAXWSWSDLLocation() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setJAXWSWSDLLocation(String wsdlLocation) {
        // TODO Auto-generated method stub
        
    }
    
    public Contribution getContributionContainingClass() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setContributionContainingClass(
            Contribution contributionContainingClass) {
        // TODO Auto-generated method stub
        
    }
}
