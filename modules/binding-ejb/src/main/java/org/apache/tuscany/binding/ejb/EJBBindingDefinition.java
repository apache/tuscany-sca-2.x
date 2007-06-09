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
package org.apache.tuscany.binding.ejb;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.BindingDefinition;

/**
 * An implementation of EJBBinding.
 */
public class EJBBindingDefinition extends BindingDefinition {
    public static final QName BINDING_EJB = new QName(XML_NAMESPACE_1_0, "binding.ejb");
    /**
     * corba location For exmaple,
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
     * Sets binding URI.
     * 
     * @param value the binding uri
     */
    public void setURI(String value) {
        this.uri = value;
    }

    /**
     * gets binding URI.
     * 
     * @return value the binding uri
     */
    public String getURI() {
        return uri;
    }
}
