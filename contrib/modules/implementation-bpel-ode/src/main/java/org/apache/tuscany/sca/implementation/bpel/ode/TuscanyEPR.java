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

package org.apache.tuscany.sca.implementation.bpel.ode;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This should hold something that makes sense for Tuscany so that the
 * process has an address that makes sense from the outside world perspective
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyEPR implements EndpointReference {
    private final QName prcessName;
    private final Endpoint endpoint;
    private final Document doc = DOMUtils.newDocument();
    
    public TuscanyEPR(QName processName, Endpoint endpoint) {
        this.prcessName = processName;
        this.endpoint = endpoint;
        
        Element serviceref = doc.createElementNS(EndpointReference.SERVICE_REF_QNAME.getNamespaceURI(),
                                                 EndpointReference.SERVICE_REF_QNAME.getLocalPart());
        serviceref.setNodeValue(endpoint.serviceName + ":" + endpoint.portName);
        doc.appendChild(serviceref);
    }
    
    public Document toXML() {
        return doc;
    }
}
