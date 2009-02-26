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
package org.apache.tuscany.sca.binding.ejb.impl;

import org.apache.tuscany.sca.binding.ejb.EJBBinding;

/**
 * An implementation of EJBBinding.
 *
 * @version $Rev$ $Date$
 */
public class EJBBindingImpl implements EJBBinding {

    /**
     * CORBA location For example,
     * "corbaname:iiop:localhost:2809/NameServiceServerRoot#ejb/MyEJBHome"
     */
    private String uri;

    /**
     * homeInterface. remote or local
     */
    private String homeInterface;

    /**
     * The ejb-link-name attribute allows a SCA client to bind to an EJB that is
     * packaged in the same JEE EAR file as the SCA client. This is functionally
     * equivalent to using the <ejb-link/> subelement of the <ejb-ref/> element
     * in s EJB deployment descriptor. Used only for Service binding
     */
    private String ejbLinkName;

    /**
     * The name of this binding
     */
    private String name;

    /**
     * Whether the binding is unresolved
     */
    private boolean unresolved;
    
    /**
     * Clone the binding
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }    

    /**
     * The type of session for this EJB Binding 
     */
    private SessionType sessionType;

    /**
     * The EJB version for this EJB Binding 
     */
    private EJBVersion ejbVersion;

    private String requires;

    /**
     * Constructor
     */
    public EJBBindingImpl() {
        super();
        unresolved = true;
    }

    /**
     * Gets the homeInterface.
     * 
     * @return home interface of the service binding
     */
    public String getHomeInterface() {
        return homeInterface;
    }

    /**
     * Set homeInterface
     * 
     * @param homeInterface
     */
    public void setHomeInterface(String homeInterface) {
        this.homeInterface = homeInterface;
    }

    /**
     * get ejb-link-name
     * 
     * @return ejb-link-name
     */
    public String getEjbLinkName() {
        return ejbLinkName;
    }

    /**
     * Set ejb-link-name
     * 
     * @param ejb-link-name
     */
    public void setEjbLinkName(String ejbLinkName) {
        this.ejbLinkName = ejbLinkName;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    public String getURI() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    public boolean isUnresolved() {
        return this.unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public void setSessionType(SessionType ejb_version_enum) {
        this.sessionType = ejb_version_enum;
    }

    public void setEjbVersion(EJBVersion ejb_version_enum) {
        this.ejbVersion = ejb_version_enum;
    }

    public void setRequires(String requires) {
        this.requires = requires;

    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public EJBVersion getEjbVersion() {
        return ejbVersion;
    }

    public String getRequires() {
        return requires;
    }
}
