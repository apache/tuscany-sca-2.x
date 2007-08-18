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
package org.apache.tuscany.sca.core.component;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.WireObjectFactory;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.CompositeActivatorImpl;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

/**
 * Base class for implementations of service and callback references.
 * 
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public abstract class CallableReferenceImpl<B> implements CallableReference<B>, Externalizable {
    protected transient CompositeActivator compositeActivator;
    protected transient ProxyFactory proxyFactory;

    protected transient Class<B> businessInterface;
    protected transient Conversation conversation;

    protected transient RuntimeComponent component;
    protected transient RuntimeComponentReference reference;
    protected transient Binding binding;

    protected String baseURI;
    protected List<QName> bindingTypes;
    protected String businessInterfaceName; // Pass the class name over seralization
    protected Object conversationID; // The conversationID should be serializable
    protected Object callbackID; // The callbackID should be serializable

    protected transient WireObjectFactory<B> factory;

    protected CallableReferenceImpl(Class<B> businessInterface,
                                    RuntimeComponent component,
                                    RuntimeComponentReference reference,
                                    Binding binding,
                                    ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        this.component = component;
        this.reference = reference;
        this.binding = binding;
        // FIXME: Should we normalize the componentName/serviceName URI into an absolute SCA URI in the SCA binding?
        // sca:component1/component11/component112/service1?
        String componentURI = component.getURI(); // The target will be relative to this base URI
        this.baseURI = componentURI;
        this.factory = getObjectFactory();
    }

    protected CallableReferenceImpl(Class<B> businessInterface, WireObjectFactory<B> factory) {
        this.proxyFactory = factory.getProxyFactory();
        this.businessInterface = businessInterface;
        this.factory = factory;
        this.component = factory.getRuntimeWire().getSource().getComponent();
        this.reference = (RuntimeComponentReference) factory.getRuntimeWire().getSource().getContract();
        this.binding = factory.getRuntimeWire().getSource().getBinding();
        // FIXME: Should we normalize the componentName/serviceName URI into an absolute SCA URI in the SCA binding?
        // sca:component1/component11/component112/service1?
        String componentURI = component.getURI(); // The target will be relative to this base URI
        this.baseURI = componentURI;
    }

    private WireObjectFactory<B> getObjectFactory() {
        // FIXME: The SCA spec is not clear how we should handle multiplicty for CallableReference
        if (binding == null) {
            binding = reference.getBinding(SCABinding.class);
            if (binding == null) {
                binding = reference.getBindings().get(0);
            }
        }
        RuntimeWire wire = reference.getRuntimeWire(binding);
        return new WireObjectFactory<B>(businessInterface, wire, proxyFactory);
    }
    
    public B getService() {
        return factory.getInstance();
    }

    public Class<B> getBusinessInterface() {
        return businessInterface;
    }

    public boolean isConversational() {
        return factory.getConversation() != null;
    }

    public Conversation getConversation() {
        return factory.getConversation();
    }

    public Object getCallbackID() {
        return callbackID;
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String scdl = in.readUTF();
        Component c = ((CompositeActivatorImpl)compositeActivator).read(new StringReader(scdl));
        this.component = (RuntimeComponent)c;
        this.reference = (RuntimeComponentReference)c.getReferences().get(0);
        Interface i = reference.getInterfaceContract().getInterface();
        if (i instanceof JavaInterface) {
            this.businessInterface = (Class<B>)((JavaInterface)i).getJavaClass();
        }
        this.factory = getObjectFactory();
        
        String uri = in.readUTF();
        int index = uri.indexOf('?');
        if (index == -1) {
            baseURI = uri;
        } else {
            baseURI = uri.substring(0, index);
            String query = uri.substring(index);
            StringTokenizer tokenizer = new StringTokenizer(query, "&");
            while (tokenizer.hasMoreTokens()) {
                String attr = tokenizer.nextToken();
                String pair[] = attr.split("=");
                if (pair[0].equals("sca.conversationID")) {
                    this.conversationID = pair[1];
                } else if (pair[0].equals("sca.callbackID")) {
                    this.callbackID = pair[1];
                }
            }
        }
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        try {
            String scdl = ((CompositeActivatorImpl)compositeActivator).write(component, reference);
            out.writeUTF(scdl);
            StringBuffer uri = new StringBuffer(baseURI);
            boolean first = true;
            if (conversationID != null) {
                uri.append("?sca.conversationID=").append(conversationID);
                first = false;
            }
            if (callbackID != null) {
                if (!first) {
                    uri.append("&");
                } else {
                    uri.append("?");
                }
                uri.append("sca.callbackID=").append(callbackID);
            }
            out.writeUTF(uri.toString());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
