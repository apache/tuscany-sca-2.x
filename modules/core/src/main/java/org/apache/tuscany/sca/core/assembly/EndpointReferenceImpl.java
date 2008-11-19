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

package org.apache.tuscany.sca.core.assembly;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * @version $Rev$ $Date$
 */
public class EndpointReferenceImpl implements EndpointReference {
    private RuntimeComponent component;
    private Contract contract;
    private Binding binding;
    private InterfaceContract interfaceContract;
    private String uri;
    private EndpointReference callbackEndpoint;
    private ReferenceParameters parameters = new ReferenceParametersImpl();

    /**
     * @param component
     * @param contract
     * @param binding
     * @param interfaceContract
     */
    public EndpointReferenceImpl(RuntimeComponent component,
                                 Contract contract,
                                 Binding binding,
                                 InterfaceContract interfaceContract) {
        super();
        this.component = component;
        this.contract = contract;
        this.binding = binding;
        this.interfaceContract = interfaceContract;
        this.uri = (component != null ? component.getURI() : "") + '/' +
                   (contract != null ? contract.getName() : "");
    }

    /**
     * @param uri
     */
    public EndpointReferenceImpl(String uri) {
        super();
        this.uri = uri;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public RuntimeComponent getComponent() {
        return component;
    }

    public void setComponent(RuntimeComponent component) {
        this.component = component;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public InterfaceContract getInterfaceContract() {
        return interfaceContract;
    }

    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public EndpointReference getCallbackEndpoint() {
        return callbackEndpoint;
    }

    public void setCallbackEndpoint(EndpointReference callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EndpointReferenceImpl other = (EndpointReferenceImpl)obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        EndpointReferenceImpl copy = (EndpointReferenceImpl)super.clone();
        /* [nash] no need to copy callback endpoint
        if (callbackEndpoint != null) {
            copy.callbackEndpoint = (EndpointReference)callbackEndpoint.clone();
        }
        */
        if (parameters != null) {
            copy.parameters = (ReferenceParameters)parameters.clone();
        }
        return copy;
    }

    /**
     * @return the parameters
     */
    public ReferenceParameters getReferenceParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setReferenceParameters(ReferenceParameters parameters) {
        this.parameters = parameters;
    }

    public void mergeEndpoint(EndpointReference epr) {
        this.component = epr.getComponent();
        this.contract = epr.getContract();
        this.binding = epr.getBinding();
        this.interfaceContract = epr.getInterfaceContract();
        this.uri = epr.getURI();
        this.callbackEndpoint = epr.getCallbackEndpoint();
    }
   
}
