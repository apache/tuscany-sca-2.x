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
package org.apache.tuscany.sca.binding.ejb;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;

/**
 * An implementation of EJBBinding.
 *
 * @version $Rev$ $Date$
 */
public interface EJBBinding extends Binding, Base {
    // Constants used when describing the EJB binding
    // model and for setting up defaults
    String BINDING_EJB = "binding.ejb";
    QName BINDING_EJB_QNAME = new QName(SCA11_NS, BINDING_EJB);
    QName TYPE = new QName(SCA11_NS, BINDING_EJB);
    
    // Constants for the XML describing the EJB Binding
    String HOME_INTERFACE = "homeInterface";
    String EJB_LINK_NAME = "ejb-link-name";
    String SESSION_TYPE = "session-type";
    String EJB_VERSION = "ejb-version";
    String NAME = "name";
    String POLICY_SETS = "policySets";
    String REQUIRES = "requires";
    String URI = "uri";

    // Enums for the EJB Binding
    enum EJBVersion {
        EJB2, EJB3
    };

    enum SessionType {
        STATEFUL, STATELESS
    };

    /**
     * Gets the homeInterface.
     * 
     * @return home interface of the service binding
     */
    String getHomeInterface();

    /**
     * Set homeInterface
     * 
     * @param homeInterface
     */
    void setHomeInterface(String homeInterface);

    /**
     * get ejb-link-name
     * 
     * @return ejb-link-name
     */
    String getEjbLinkName();

    /**
     * Set ejb-link-name
     * 
     * @param ejb-link-name
     */
    void setEjbLinkName(String ejbLinkName);

    SessionType getSessionType();
    void setSessionType(SessionType sessionType);

    EJBVersion getEjbVersion();
    void setEjbVersion(EJBVersion ejbVersion);

    // FIXME: Should use Intent instead of String
    String getRequires();
    void setRequires(String requires);
}
