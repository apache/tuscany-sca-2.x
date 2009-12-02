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

package org.apache.tuscany.sca.binding.jsonrpc.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;

/**
 * A model for the JSONRPC binding.
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingImpl implements JSONRPCBinding {
    private String name;
    private String uri;

    public QName getType() {
		return TYPE;
	}
    
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

    public boolean isUnresolved() {
        // The binding is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The binding is always resolved
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
