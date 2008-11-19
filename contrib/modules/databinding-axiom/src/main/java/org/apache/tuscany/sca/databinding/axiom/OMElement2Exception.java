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
package org.apache.tuscany.sca.databinding.axiom;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.tuscany.sca.databinding.javabeans.XML2JavaBeanTransformer;
import org.apache.tuscany.sca.databinding.javabeans.XML2JavaMapperException;

/**
 * Transformer to convert data from an OMElement to a Java Exception
 *
 * @version $Rev$ $Date$
 */
public class OMElement2Exception extends XML2JavaBeanTransformer<OMElement> {

    @Override
    public OMElement getRootElement(OMElement element) throws XML2JavaMapperException {
        return element;
    }

    @Override
    public Iterator<OMElement> getChildElements(OMElement parent) throws XML2JavaMapperException {
        return parent.getChildElements();
    }

    @Override
    public String getElementName(OMElement element) throws XML2JavaMapperException {
        return element.getLocalName();
    }

    @Override
    public String getText(OMElement element) throws XML2JavaMapperException {
        return element.getText();
    }

    @Override
    public boolean isTextElement(OMElement element) throws XML2JavaMapperException {
        return false;
    }

    @Override
    public boolean isTextOnly(OMElement element) throws XML2JavaMapperException {
        OMNode firstChild = element.getFirstOMChild();
        return firstChild instanceof OMText && firstChild.getNextOMSibling() == null;
    }

    @Override
    public OMElement getFirstChildWithName(OMElement element, QName name) throws XML2JavaMapperException {
        return element.getFirstChildWithName(name);
    }

    @Override
    public Class getSourceType() {
        return OMElement.class;
    }

}
