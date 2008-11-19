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

package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.w3c.dom.Element;

/**
 * A generic XmlAdapter for JAXB to marshal/unmarshal between the java objects and DOM elements
 */
public class DOMElementXmlAdapter extends XmlAdapter<Element, Object> {
    private Mediator mediator;
    private DataType dataType;
    private DataType domType;
    
    public DOMElementXmlAdapter(Mediator mediator, DataType dataType) {
        this.mediator = mediator;
        this.dataType = dataType;
        this.domType = new DataTypeImpl(DOMDataBinding.NAME, Element.class, dataType.getLogical());
    }

    @Override
    public Element marshal(Object value) throws Exception {
        return (Element) mediator.mediate(value, dataType, domType, null);
    }

    @Override
    public Object unmarshal(Element element) throws Exception {
        return mediator.mediate(element, domType, dataType, null);
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
