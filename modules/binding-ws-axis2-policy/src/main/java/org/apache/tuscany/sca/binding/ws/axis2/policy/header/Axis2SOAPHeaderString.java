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
package org.apache.tuscany.sca.binding.ws.axis2.policy.header;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.policy.Policy;

/**
 * Implementation for policies that could be injected as parameter
 * into the axis2config.
 *
 * @version $Rev$ $Date$
 */
public class Axis2SOAPHeaderString extends Axis2SOAPHeader {

    private String headerString;

    public String getHeaderString() {
        return headerString;
    }
    
    public void setHeaderString(String headerString) {
        this.headerString = headerString;
    }
    
    public OMElement getAsSOAPHeaderBlock(OMFactory factory) {
        OMElement header = super.getAsSOAPHeaderBlock(factory);
        OMText headerText = factory.createOMText(header, headerString);
        header.addChild(headerText);
        return header;
    }

    public void setAsSOAPHeaderBlock(OMElement header) {
        super.setAsSOAPHeaderBlock(header);
        headerString = header.getText();
    }
}
