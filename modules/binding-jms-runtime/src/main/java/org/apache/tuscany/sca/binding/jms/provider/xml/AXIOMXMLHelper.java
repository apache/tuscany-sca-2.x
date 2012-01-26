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

package org.apache.tuscany.sca.binding.jms.provider.xml;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.util.FaultException;

public class AXIOMXMLHelper implements XMLHelper<OMElement> {

    private OMFactory factory;
    private StAXHelper staxhelper;

    public AXIOMXMLHelper(ExtensionPointRegistry epr) {
        this.staxhelper = StAXHelper.getInstance(epr);
        this.factory = OMAbstractFactory.getOMFactory();
    }

    @Override
    public OMElement load(String xml) throws IOException {
        StAXOMBuilder builder;
        try {
            builder = new StAXOMBuilder(staxhelper.createXMLStreamReader(xml));
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return builder.getDocumentElement();
    }

    @Override
    public String saveAsString(OMElement t) {
        // TODO: The JMS compliance tests require the XML prefix but AXIOM doesn't include that
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + t.toString();
    }

    @Override
    public String getOperationName(OMElement t) {
        return t.getLocalName();
    }

    @Override
    public Object wrap(OMElement template, OMElement os) {
        OMElement wrapper;
        if (os != null) {
            OMNamespace ns = os.declareNamespace(template.getNamespace().getNamespaceURI(), "");
            wrapper = factory.createOMElement(template.getLocalName(), ns);
            wrapper.addChild(os);
        } else {
            wrapper = template.cloneOMElement();
        }
        return wrapper;
    }

    @Override
    public OMElement createWrapper(QName qname) {
        return factory.createOMElement(qname);
    }

    @Override
    public String getDataBindingName() {
        return OMElement.class.getName();
    }

    @Override
    public OMElement getFirstChild(OMElement o) {
        return o.getFirstElement();
    }

    @Override
    public void setFaultName(FaultException e, Object o) {
        OMElement om = (OMElement)o;
        e.setFaultName(new QName(om.getNamespace().getNamespaceURI(), om.getLocalName()));
    }
}
