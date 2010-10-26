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
package org.apache.tuscany.sca.provider;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;

/**
 * Base abstract impl for a binding model.
 */
public abstract class BaseBindingImpl implements Binding {

    private String name;
    private String uri;
    private boolean unresolved;
    private OperationSelector operationSelector;
    private WireFormat requestWireFormat;
    private WireFormat responseWireFormat;

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public abstract QName getType();
    
    public WireFormat getRequestWireFormat() {
        return requestWireFormat;
    }
    
    public void setRequestWireFormat(WireFormat wireFormat) {  
        this.requestWireFormat = wireFormat;
    }
    
    public WireFormat getResponseWireFormat() {
        return responseWireFormat;
    }
    
    public void setResponseWireFormat(WireFormat wireFormat) {
        this.responseWireFormat = wireFormat;
    }
    
    public OperationSelector getOperationSelector() {
        return operationSelector;
    }
    
    public void setOperationSelector(OperationSelector operationSelector) {
        this.operationSelector = operationSelector;
    }    

}
