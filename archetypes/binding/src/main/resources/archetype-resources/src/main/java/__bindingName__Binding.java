#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.binding.foo;

import javax.xml.namespace.QName;

import ${package}.assembly.Binding;
import ${package}.assembly.OperationSelector;
import ${package}.assembly.WireFormat;

/**
 * Represents a binding to an Foo service.
 */
public class FooBinding implements Binding {

    QName TYPE = new QName(SCA11_TUSCANY_NS, "binding.foo");

    private String name;
    private String uri;
    private String bindingType;

    public String getBindingType() {
        return bindingType;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean arg0) {
    }

    public QName getType() {
        return TYPE;
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
