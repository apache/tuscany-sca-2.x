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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.PortType;

import org.eclipse.emf.ecore.impl.EClassImpl;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;
import org.apache.tuscany.model.types.OperationType;
import org.apache.tuscany.model.types.wsdl.WSDLInterfaceType;

/**
 */
public class WSDLInterfaceTypeImpl extends EClassImpl implements WSDLInterfaceType {

    private Map<String, OperationType> operationTypesMap;
    private PortType portType;

    /**
     * Constructor
     */
    public WSDLInterfaceTypeImpl(PortType portType) {
        super();
        this.portType = portType;
    }

    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLInterfaceType#getWSDLPortType()
     */
    public PortType getWSDLPortType() {
        return portType;
    }

    /**
     * @see org.apache.tuscany.model.types.InterfaceType#getOperationTypes()
     */
    public List<OperationType> getOperationTypes() {
        return getEOperations();
    }

    /**
     * @see org.apache.tuscany.model.types.InterfaceType#getOperationType(java.lang.String)
     */
    public OperationType getOperationType(String name) {
        return operationTypesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.types.InterfaceType#getURI()
     */
    public String getURI() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        operationTypesMap = new HashMap<String, OperationType>();
        for (Iterator<OperationType> i = getOperationTypes().iterator(); i.hasNext();) {
            OperationType operationType = i.next();
            operationTypesMap.put(operationType.getName(), operationType);
            operationType.initialize(modelContext);
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }
}
