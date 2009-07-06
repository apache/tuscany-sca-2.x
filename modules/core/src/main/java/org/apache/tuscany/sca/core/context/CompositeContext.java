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

package org.apache.tuscany.sca.core.context;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public abstract class CompositeContext {
    /**
     * Create a self-reference for a component service
     * @param component
     * @param service
     * @throws CloneNotSupportedException 
     * @throws InvalidInterfaceException 
     */
    public abstract ComponentReference createSelfReference(Component component,
                                                           ComponentService service,
                                                           Class<?> businessInterface)
        throws CloneNotSupportedException, InvalidInterfaceException;

    /**
     * Bind a component reference to a component service
     * @param <B>
     * @param businessInterface
     * @param reference
     * @param service
     * @return
     * @throws CloneNotSupportedException
     * @throws InvalidInterfaceException
     */
    public abstract RuntimeComponentReference bindComponentReference(Class<?> businessInterface,
                                                                     RuntimeComponentReference reference,
                                                                     RuntimeComponent component,
                                                                     RuntimeComponentService service)
        throws CloneNotSupportedException, InvalidInterfaceException;

    /**
     * @param component
     * @param reference
     * @param writer
     * @throws IOException
     */
    public abstract void write(Component component, ComponentReference reference, Writer writer) throws IOException;

    /**
     * @param component
     * @param reference
     * @param service
     * @param writer
     * @throws IOException
     */
    public abstract void write(Component component,
                               ComponentReference reference,
                               ComponentService service,
                               Writer writer) throws IOException;

    /**
     * @param component
     * @param reference
     * @return
     * @throws IOException
     */
    public abstract String toXML(Component component, ComponentReference reference) throws IOException;

    /**
     * @param component
     * @param service
     * @return
     * @throws IOException
     */
    public abstract String toXML(Component component, ComponentService service) throws IOException;

    /**
     * @param reader
     * @return
     * @throws IOException
     */
    public abstract RuntimeComponent read(Reader reader) throws IOException;

    /**
     * @param streamReader
     * @return
     * @throws IOException
     */
    public abstract RuntimeComponent read(XMLStreamReader streamReader) throws IOException;

    /**
     * @param xml
     * @return
     * @throws IOException
     */
    public abstract Component fromXML(String xml) throws IOException;

    /**
     * @param streamReader
     * @return
     * @throws IOException
     */
    public abstract Component fromXML(XMLStreamReader streamReader) throws IOException;

    /**
     * @return
     */
    public static RuntimeComponent getCurrentComponent() {
        Message message = ThreadMessageContext.getMessageContext();
        if (message != null) {
            Endpoint to = message.getTo();
            if (to == null) {
                return null;
            }
            RuntimeComponent component = (RuntimeComponent) message.getTo().getComponent();
            return component;
        }
        return null;
    }

    /**
     * @return
     */
    public static CompositeActivator getCurrentCompositeActivator() {
        RuntimeComponent component = getCurrentComponent();
        if (component != null) {
            ComponentContextExt context = (ComponentContextExt)component.getComponentContext();
            return context.getCompositeActivator();
        }
        return null;
    }

    /**
     * @return
     */
    public static CompositeContext getCurrentCompositeContext() {
        CompositeActivator activator = getCurrentCompositeActivator();
        if (activator != null) {
            return activator.getCompositeContext();
        }
        return null;
    }

    /**
     * @param component
     */
    public static ComponentService getSingleService(Component component) {
        ComponentService targetService;
        List<ComponentService> services = component.getServices();
        List<ComponentService> regularServices = new ArrayList<ComponentService>();
        for (ComponentService service : services) {
            if (service.isForCallback()) {
                continue;
            }
            String name = service.getName();
            if (!name.startsWith("$") || name.startsWith("$dynamic$")) {
                regularServices.add(service);
            }
        }
        if (regularServices.size() == 0) {
            throw new ServiceRuntimeException("No service is declared on component " + component.getURI());
        }
        if (regularServices.size() != 1) {
            throw new ServiceRuntimeException("More than one service is declared on component " + component.getURI()
                + ". Service name is required to get the service.");
        }
        targetService = regularServices.get(0);
        return targetService;
    }

    public abstract ExtensionPointRegistry getExtensionPointRegistry();

    /**
     * Get the java interface factory
     * @return
     */
    public abstract JavaInterfaceFactory getJavaInterfaceFactory();

    /**
     * Get the proxy factory
     * @return
     */
    public abstract ProxyFactory getProxyFactory();

}
