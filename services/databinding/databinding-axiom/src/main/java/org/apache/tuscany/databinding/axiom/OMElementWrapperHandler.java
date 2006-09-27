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

import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.idl.ElementInfo;

/**
 * OMElement wrapper handler implementation
 */
public class OMElementWrapperHandler implements WrapperHandler<OMElement> {

    private OMFactory factory;

    public OMElementWrapperHandler() {
        super();
        this.factory = OMAbstractFactory.getOMFactory();
    }

    public OMElement create(ElementInfo element, TransformationContext context) {
        OMElement wrapper = factory.createOMElement(element.getQName(), null);
        return wrapper;
    }

    public Object getChild(OMElement wrapper, int i, ElementInfo element) {
        int index = 0;
        for (Iterator e = wrapper.getChildElements(); e.hasNext();) {
            OMElement child = (OMElement) e.next();
            if (index != i) {
                index++;
                continue;
            }
            if (child.getQName().equals(element.getQName())) {
                return child;
            }
        }
        return null;
    }

    public void setChild(OMElement wrapper, int i, ElementInfo childElement, Object value) {
        wrapper.addChild((OMElement) value);
    }

}
