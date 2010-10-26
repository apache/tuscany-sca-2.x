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
package org.apache.tuscany.sca.binding.comet.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.comet.CometBinding;

/**
 * Represents a binding through Comet to a service.
 */
public class CometBindingImpl implements CometBinding {

    private String name;
    private String uri;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public void setURI(final String uri) {
        this.uri = uri;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }

    @Override
    public void setUnresolved(final boolean unresolved) {
    }

    @Override
    public QName getType() {
        return CometBinding.TYPE;
    }

    @Override
    public WireFormat getRequestWireFormat() {
        return null;
    }

    @Override
    public void setRequestWireFormat(final WireFormat wireFormat) {
    }

    @Override
    public WireFormat getResponseWireFormat() {
        return null;
    }

    @Override
    public void setResponseWireFormat(final WireFormat wireFormat) {
    }

    @Override
    public OperationSelector getOperationSelector() {
        return null;
    }

    @Override
    public void setOperationSelector(final OperationSelector operationSelector) {
    }

}
