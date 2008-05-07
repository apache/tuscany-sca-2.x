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
package org.apache.tuscany.sca.implementation.bpel.impl;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;

/**
 * The model representing a BPEL implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public class BPELImplementationImpl extends ImplementationImpl implements BPELImplementation {

    private QName _processName;
    private BPELProcessDefinition _processDefinition;
    private ComponentType componentType;

    /**
     * Constructs a new BPEL implementation.
     */
    public BPELImplementationImpl(AssemblyFactory assemblyFactory,
                              WSDLFactory wsdlFactory) {

    }

    public QName getProcess() {
        return _processName;
    }
    
    public void setProcess(QName processName) {
        _processName = processName;
    }

    public BPELProcessDefinition getProcessDefinition() {
        return this._processDefinition;
    }

    public void setProcessDefinition(BPELProcessDefinition processDefinition) {
        this._processDefinition = processDefinition;
    }

    @Override
    public String getURI() {
        // The sample BPEL implementation does not have a URI
        return null;
    }

    @Override
    public void setURI(String uri) {
        // The sample BPEL implementation does not have a URI
    }

    @Override
    public ConstrainingType getConstrainingType() {
        // The sample BPEL implementation does not support constrainingTypes
        return null;
    }

    @Override
    public List<Property> getProperties() {
        // The sample BPEL implementation does not support properties
        return Collections.emptyList();
    }
    
    /* 
     * Returns the componentType for this BPEL process implementation 
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /*
     * Sets the componentType for this BPEL process implementation
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }
    
    @Override
    /**
     * Returns a List of the services for this BPEL process implementation
     */
    public List<Service> getServices() {
        return componentType.getServices();
    }

    @Override
    /**
     * Returns a List of the references for this BPEL process implementation
     */
    public List<Reference> getReferences() {
        return componentType.getReferences();
    }
    
    @Override
    public int hashCode() {
        return String.valueOf(this.getProcess()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BPELImplementation) {
            if (getProcess() != null) {
                return getProcess().equals(((BPELImplementation)obj).getProcess());
            } else {
                return ((BPELImplementation)obj).getProcess() == null;
            }
        } else {
            return false;
        }
    }
}
