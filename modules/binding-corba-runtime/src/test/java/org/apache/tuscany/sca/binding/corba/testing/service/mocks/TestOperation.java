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

import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Mock Operation implementation. Only few methods needs to be implemented.
 */
public class TestOperation implements Operation {

    private DataType<List<DataType>> inputType;
    private DataType outputType;
    private String name;

    public ConversationSequence getConversationSequence() {
        return null;
    }

    public String getDataBinding() {
        return null;
    }

    public Map<QName, List<DataType<XMLType>>> getFaultBeans() {
        return null;
    }

    public List<DataType> getFaultTypes() {
        return null;
    }

    public DataType<List<DataType>> getInputType() {
        return inputType;
    }

    public Interface getInterface() {
        return null;
    }

    public String getName() {
        return name;
    }

    public DataType getOutputType() {
        return outputType;
    }

    public WrapperInfo getWrapper() {
        return null;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isNonBlocking() {
        return false;
    }

    public boolean isUnresolved() {
        return false;
    }

    public boolean isWrapperStyle() {
        return false;
    }

    public void setConversationSequence(ConversationSequence sequence) {

    }

    public void setDataBinding(String dataBinding) {

    }

    public void setDynamic(boolean b) {

    }

    public void setFaultBeans(Map<QName, List<DataType<XMLType>>> faultBeans) {

    }

    public void setFaultTypes(List<DataType> faultTypes) {

    }

    public void setInputType(DataType<List<DataType>> inputType) {
        this.inputType = inputType;
    }

    public void setInterface(Interface interfaze) {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNonBlocking(boolean nonBlocking) {

    }

    public void setOutputType(DataType outputType) {
        this.outputType = outputType;
    }

    public void setUnresolved(boolean unresolved) {

    }

    public void setWrapper(WrapperInfo wrapperInfo) {

    }

    public void setWrapperStyle(boolean wrapperStyle) {

    }

    public List<PolicySet> getApplicablePolicySets() {
        return null;
    }

    public List<PolicySet> getPolicySets() {
        return null;
    }

    public Object clone() {
        return null;
    }

    public List<Intent> getRequiredIntents() {
        return null;
    }

    public IntentAttachPointType getType() {
        return null;
    }

    public void setType(IntentAttachPointType type) {
    }

}
