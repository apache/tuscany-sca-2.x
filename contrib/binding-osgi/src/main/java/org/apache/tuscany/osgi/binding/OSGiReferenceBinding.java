/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.osgi.binding;

import java.lang.reflect.Method;
import java.rmi.Remote;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.idl.java.JavaIDLUtils;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class OSGiReferenceBinding extends ReferenceBindingExtension {
    private static final QName BINDING_OSGI = new QName("http://tuscany.apache.org/xmlns/osgi/1.0", "binding.osgi");
    //private final String uri;

    public OSGiReferenceBinding(String name, CompositeComponent parent) {
        super(name, parent);
        //this.uri = uri;
    }

    public QName getBindingType() {
        return BINDING_OSGI;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        try {
            Object proxy = getProxy();
            String name = operation.getName();
            Method method = JavaIDLUtils.findMethod(operation, contract.getInterfaceClass().getMethods());
            Class<?>[] parameterTypes = method.getParameterTypes();
            Method remoteMethod = proxy.getClass().getMethod(name, parameterTypes);
            return new OSGiInvoker(proxy, remoteMethod);
        } catch (NoSuchMethodException e) {
            // FIXME we should probably have this as a checked exception, which will entail adding to the SPI signature
            throw new NoRemoteMethodException(operation.toString(), e);
        }
    }

    protected Remote getProxy() {
        //FIXME use service reference
        return null;
    }

}
