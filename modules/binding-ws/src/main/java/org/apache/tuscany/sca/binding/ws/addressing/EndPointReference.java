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

package org.apache.tuscany.sca.binding.ws.addressing;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Defines a model for WS-Addressing
 * &lt;wsa:EndpointReference&gt;
 *     &lt;wsa:Address&gt;xs:anyURI&lt;/wsa:Address&gt;
 *     &lt;wsa:ReferenceProperties&gt;... &lt;/wsa:ReferenceProperties&gt; ?
 *     &lt;wsa:ReferenceParameters&gt;... &lt;/wsa:ReferenceParameters&gt; ?
 *     &lt;wsa:PortType&gt;xs:QName&lt;/wsa:PortType&gt; ?
 *     &lt;wsa:ServiceName PortName="xs:NCName"?&gt;xs:QName&lt;/wsa:ServiceName&gt; ?
 *     &lt;wsp:Policy&gt; ... &lt;/wsp:Policy&gt;*
 * &lt;/wsa:EndpointReference&gt;
 * @version $Rev$ $Date$
 */
public interface EndPointReference {
    URI getAddress();

    void setAddress(URI address);

    QName getPortType();

    void setPortType(QName portType);

    QName getServiceName();

    void setServiceName(QName serviceName);

    QName getPortName();

    void setPortName(QName portName);

    List<Object> getReferenceProperties();

    List<Object> getReferenceParameters();

    List<Object> getPolicies();

}
