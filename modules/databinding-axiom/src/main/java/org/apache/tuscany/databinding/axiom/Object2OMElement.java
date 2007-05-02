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
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.impl.Java2SimpleTypeTransformer;

/**
 * Transformer to convert data from an simple OMElement to Java Object
 */
public class Object2OMElement extends Java2SimpleTypeTransformer<OMElement> {

    private OMFactory factory;

    public Object2OMElement() {
        super();
        factory = OMAbstractFactory.getOMFactory();
    }

    protected OMElement createElement(QName element, String text, TransformationContext context) {
        OMElement omElement = factory.createOMElement(element, null);
        factory.createOMText(omElement, text);
        return omElement;
    }

    @Override
    public Class getTargetType() {
        return OMElement.class;
    }

}
