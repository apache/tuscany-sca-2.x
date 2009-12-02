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
package org.apache.tuscany.sca.assembly.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A test cheel for the SCA binding.
 *
 * @version $Rev$ $Date$
 */
public class TestSCABindingImpl implements SCABinding, OptimizableBinding, PolicySubject {
    private String name;
    private String uri;
    private List<Object> extensions = new ArrayList<Object>();

    private Component targetComponent;
    private ComponentService targetComponentService;
    private Binding targetBinding;

    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private ExtensionType bindingType = new TestSCABindingType();

    /**
     * Constructs a new SCA binding.
     */
    protected TestSCABindingImpl() {
    }

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public List<Object> getExtensions() {
        return extensions;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the targetComponent
     */
    public Component getTargetComponent() {
        return targetComponent;
    }

    /**
     * @param targetComponent the targetComponent to set
     */
    public void setTargetComponent(Component targetComponent) {
        this.targetComponent = targetComponent;
    }

    /**
     * @return the targetComponentService
     */
    public ComponentService getTargetComponentService() {
        return targetComponentService;
    }

    /**
     * @param targetComponentService the targetComponentService to set
     */
    public void setTargetComponentService(ComponentService targetComponentService) {
        this.targetComponentService = targetComponentService;
    }

    /**
     * @return the targetBinding
     */
    public Binding getTargetBinding() {
        return targetBinding;
    }

    /**
     * @param targetBinding the targetBinding to set
     */
    public void setTargetBinding(Binding targetBinding) {
        this.targetBinding = targetBinding;
    }

    public List<PolicySet> getPolicySets() {
        // TODO Auto-generated method stub
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        // TODO Auto-generated method stub
        return requiredIntents;
    }

    public ExtensionType getExtensionType() {
        // TODO Auto-generated method stub
        return bindingType;
    }

    public void setExtensionType(ExtensionType type) {
        this.bindingType = type;
    }

    public QName getType() {
        return TYPE;
    }

    private class TestSCABindingType implements ExtensionType {
        private QName name = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200903","binding");
        public QName getBaseType() {
            return BINDING_BASE;
        }

        public QName getType() {
            return name;
        }

        public void setType(QName type) {
        }

        public List<Intent> getAlwaysProvidedIntents() {
            return Collections.emptyList();
        }

        public List<Intent> getMayProvidedIntents() {
            return Collections.emptyList();
        }

        public boolean isUnresolved() {
            return false;
        }

        public void setUnresolved(boolean unresolved) {
        }
    }

    public WireFormat getRequestWireFormat() {
        return null;
    }
    
    public void setRequestWireFormat(WireFormat wireFormat) {  
    }
    
    public WireFormat getResponseWireFormat() {
        return null;
    }
    
    public void setResponseWireFormat(WireFormat wireFormat) {
    }
    
    public OperationSelector getOperationSelector() {
        return null;
    }
    
    public void setOperationSelector(OperationSelector operationSelector) {
    }
}
