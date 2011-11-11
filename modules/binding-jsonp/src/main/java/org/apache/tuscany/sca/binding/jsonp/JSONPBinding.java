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

package org.apache.tuscany.sca.binding.jsonp;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.binding.http.operationselector.HTTPRPCOperationSelector;
import org.apache.tuscany.sca.binding.http.wireformat.HTTPJSONWireFormat;

/**
 * JSONP Binding model
 */
public class JSONPBinding implements HTTPBinding {
    public static final QName TYPE = new QName(SCA11_TUSCANY_NS, "binding.jsonp");
    
    private String name;
    private String uri;

    private WireFormat wireFormat;
    private OperationSelector operationSelector;    
    
    public JSONPBinding() {
        // configure the HTTP binding for JSONP (which for the moment is the default wireFormat)
        setOperationSelector(new HTTPRPCOperationSelector());
        setRequestWireFormat(new HTTPJSONWireFormat());
        setResponseWireFormat(new HTTPJSONWireFormat());
    }

    public QName getType() {
        return TYPE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample binding is always resolved
    }

    // Wireformat and Operation selection

    public WireFormat getRequestWireFormat() {
        return wireFormat;
    }

    public void setRequestWireFormat(WireFormat wireFormat) {
        //no op
    }

    public WireFormat getResponseWireFormat() {
        return wireFormat;
    }

    public void setResponseWireFormat(WireFormat wireFormat) {
        //no op
    }    

    public OperationSelector getOperationSelector() {
        return operationSelector;
    }

    public void setOperationSelector(OperationSelector operationSelector) {
        //no op
    }    

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }     
}