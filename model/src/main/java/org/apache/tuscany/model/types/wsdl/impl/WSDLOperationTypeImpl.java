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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Operation;

import commonj.sdo.Type;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.impl.EOperationImpl;
import org.eclipse.emf.ecore.sdo.util.SDOUtil;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;
import org.apache.tuscany.model.types.wsdl.WSDLOperationType;

/**
 */
public class WSDLOperationTypeImpl extends EOperationImpl implements WSDLOperationType {

    private Operation operation;
    private Type inputType;
    private Type outputType;
    private List<Type> exceptionTypes = new ArrayList<Type>();

    /**
     *
     */
    public WSDLOperationTypeImpl(Operation operation) {
        super();
        this.operation = operation;
    }

    /**
     * @see org.apache.tuscany.model.types.wsdl.WSDLOperationType#getWSDLOperation()
     */
    public Operation getWSDLOperation() {
        return operation;
    }

    /**
     * @see org.apache.tuscany.model.types.OperationType#getInputType()
     */
    public Type getInputType() {
        return inputType;
    }

    /**
     * @see org.apache.tuscany.model.types.OperationType#getOutputType()
     */
    public Type getOutputType() {
        return outputType;
    }

    /**
     * @see org.apache.tuscany.model.types.OperationType#getExceptionTypes()
     */
    public List<Type> getExceptionTypes() {
        return exceptionTypes;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (getEParameters().size() != 0) {
            EParameter parameter = (EParameter) getEParameters().get(0);
            inputType = SDOUtil.adaptType(parameter.getEType());
        }
        if (getEType() != null) {
            outputType = SDOUtil.adaptType(getEType());
        }
        for (Iterator<EClassifier> i = getEExceptions().iterator(); i.hasNext();) {
            exceptionTypes.add(SDOUtil.adaptType(i.next()));
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }
}
