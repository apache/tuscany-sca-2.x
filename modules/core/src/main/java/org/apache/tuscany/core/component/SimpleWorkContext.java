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
package org.apache.tuscany.core.component;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * A simple WorkContext implementation that provides basic thread-local support
 * for storing work context information. The implementation is <em>not</em>
 * thread safe.
 * 
 * @version $Rev$ $Date$
 */
public class SimpleWorkContext implements WorkContext {
    private final Map<Object, Object> identifiers = new HashMap<Object, Object>();
    private final List<String> serviceNameStack = new ArrayList<String>();

    private LinkedList<URI> callbackUris;
    private LinkedList<RuntimeWire> callbackWires;
    private Object correlationId;
    private RuntimeComponent currentAtomicComponent;

    public Object getIdentifier(Object type) {
        return identifiers.get(type);
    }

    public void setIdentifier(Object type, Object identifier) {
        identifiers.put(type, identifier);
    }

    public void clearIdentifier(Object type) {
        identifiers.remove(type);
    }

    public void clearIdentifiers() {
        identifiers.clear();
    }

    public LinkedList<URI> getCallbackUris() {
        return callbackUris;
    }

    public void setCallbackUris(LinkedList<URI> uris) {
        this.callbackUris = uris;
    }

    public LinkedList<RuntimeWire> getCallbackWires() {
        return callbackWires;
    }

    public void setCallbackWires(LinkedList<RuntimeWire> wires) {
        this.callbackWires = wires;
    }

    public Object getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(Object correlationId) {
        this.correlationId = correlationId;
    }

    public RuntimeComponent getCurrentAtomicComponent() {
        return currentAtomicComponent;
    }

    public void setCurrentAtomicComponent(RuntimeComponent currentAtomicComponent) {
        this.currentAtomicComponent = currentAtomicComponent;
    }

    public String getCurrentServiceName() {
        assert !serviceNameStack.isEmpty();
        return serviceNameStack.get(serviceNameStack.size() - 1);
    }

    public void pushServiceName(String name) {
        serviceNameStack.add(name);
    }

    public String popServiceName() {
        assert !serviceNameStack.isEmpty();
        return serviceNameStack.remove(serviceNameStack.size() - 1);
    }

    public void clearServiceNames() {
        serviceNameStack.clear();
    }
}
