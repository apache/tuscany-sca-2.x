/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.model.types.wsdl.impl;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.impl.ServiceContractImpl;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.util.XMLNameUtil;

/**
 * An implementation of WSDLServiceContract.
 */
public class WSDLServiceContractImpl extends ServiceContractImpl implements WSDLServiceContract {

    private PortType portType;
    private PortType callbackPortType;

    /**
     * Constructor
     */
    public WSDLServiceContractImpl() {
    }

    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLServiceContract#getPortType()
     */
    public PortType getPortType() {
        return portType;
    }
    
    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLServiceContract#setPortType(javax.wsdl.PortType)
     */
    public void setPortType(PortType portType) {
        checkNotFrozen();
        this.portType=portType;
    }

    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLServiceContract#getCallbackPortType()
     */
    public PortType getCallbackPortType() {
        return callbackPortType;
    }
    
    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLServiceContract#setCallbackPortType(javax.wsdl.PortType)
     */
    public void setCallbackPortType(PortType portType) {
        checkNotFrozen();
        callbackPortType=portType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.ExtensibleImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Load the Java interface for the portType 
        if (portType!=null && getInterface()==null) {
            QName qname=portType.getQName();
            String interfaceName=XMLNameUtil.getPackageNameFromNamespace(qname.getNamespaceURI())+XMLNameUtil.getValidNameFromXMLName(qname.getLocalPart(), true);
            try {
                super.setInterface(modelContext.getResourceLoader().loadClass(interfaceName));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        // Load the Java interface for the callback portType 
        if (callbackPortType!=null && getCallbackInterface()==null) {
            QName qname=callbackPortType.getQName();
            String interfaceName=XMLNameUtil.getPackageNameFromNamespace(qname.getNamespaceURI())+XMLNameUtil.getValidNameFromXMLName(qname.getLocalPart(), true);
            try {
                super.setCallbackInterface(modelContext.getResourceLoader().loadClass(interfaceName));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
}
