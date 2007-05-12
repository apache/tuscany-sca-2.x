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

package org.apache.tuscany.databinding.axiom;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Helper for AXIOM
 * 
 * @version $Rev$ $Date$
 */
public class AxiomHelper {

    private AxiomHelper() {
    }

    /**
     * @param context
     * @param element
     */
    public static void adjustElementName(TransformationContext context, OMElement element) {
        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object logical = dataType == null ? null : dataType.getLogical();
            if (!(logical instanceof XMLType)) {
                return;
            }
            XMLType xmlType = (XMLType)logical;
            if (xmlType.isElement() && !xmlType.getElementName().equals(element.getQName())) {
                // TODO: Throw expection or switch to the new Element
                OMFactory factory = OMAbstractFactory.getOMFactory();
                QName name = xmlType.getElementName();
                OMNamespace namespace = factory.createOMNamespace(name.getNamespaceURI(), name.getPrefix());
                element.setNamespace(namespace);
                element.setLocalName(name.getLocalPart());
            }
        }
    }
}
