/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.binding.rmi;

import java.lang.reflect.Method;
import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.rmi.RMIHostExtensionPoint;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class RMIReferenceBinding extends ReferenceBindingExtension {
    private RMIHostExtensionPoint rmiHost;
    
    private RMIBinding rmiBindingDefn;
    
    public RMIReferenceBinding(URI name, 
                               URI targetUri, 
                               RMIBinding rmiBindingDefn, 
                               RMIHostExtensionPoint rmiHost) {
        super(name, targetUri);
        this.rmiBindingDefn = rmiBindingDefn;
        this.rmiHost = rmiHost;
    }

    public TargetInvoker createTargetInvoker(String targetName, 
                                             Operation operation, 
                                             boolean isCallback) throws TargetInvokerCreationException {

        try {
            Method remoteMethod = 
                JavaInterfaceUtil.findMethod(((JavaInterface)wire.getTargetContract().getInterface()).getJavaClass(),
                                                operation);
            
            return new RMIInvoker(rmiHost, 
                                  rmiBindingDefn.getRmiHostName(), 
                                  rmiBindingDefn.getRmiPort(), 
                                  rmiBindingDefn.getRmiServiceName(), 
                                  remoteMethod);
        } catch (NoSuchMethodException e) {
            throw new NoRemoteMethodException(operation.toString(), e);
        }
    }

    public QName getBindingType() {
        return RMIBindingConstants.BINDING_RMI_QNAME;
    }
}
