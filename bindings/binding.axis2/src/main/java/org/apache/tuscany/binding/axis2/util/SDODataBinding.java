/**
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
package org.apache.tuscany.binding.axis2.util;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

import commonj.sdo.helper.TypeHelper;

public class SDODataBinding implements DataBinding {

    private TypeHelper typeHelper;

    private QName typeQN;

    private boolean isWrapped;

    public SDODataBinding(TypeHelper typeHelper, QName typeQN, boolean isWrapped) {
        this.typeHelper = typeHelper;
        this.typeQN = typeQN;
        this.isWrapped = isWrapped;
    }

    public Object[] fromOMElement(OMElement omElement) {
        ClassLoader oldCL = ClassLoaderHelper.setApplicationClassLoader();
        try {
            Object[] args = AxiomHelper.toObjects(typeHelper, omElement, isWrapped);
            return args;
        } finally {
            if (oldCL != null) {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }

    public OMElement toOMElement(Object[] args) {
        ClassLoader oldCL = ClassLoaderHelper.setApplicationClassLoader();
        try {
            OMElement omElement = AxiomHelper.toOMElement(typeHelper, args, typeQN, isWrapped);
            return omElement;
        } finally {
            if (oldCL != null) {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }

}
