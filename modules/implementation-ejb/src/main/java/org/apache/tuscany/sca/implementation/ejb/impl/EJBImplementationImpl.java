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
package org.apache.tuscany.sca.implementation.ejb.impl;

import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementation;


/**
 * The model representing an EJB implementation in an SCA assembly model.
 *
 * @version $Rev$ $Date$
 */
public class EJBImplementationImpl extends ImplementationImpl implements EJBImplementation {
    private String ejbLink;
    private String uri;
    private boolean unresolved;

    /**
     * Constructs a new EJB implementation.
     */
    public EJBImplementationImpl() {
        super(TYPE);
    }

    public String getEJBLink() {
        return ejbLink;
    }
    
    public void setEJBLink(String ejbLink) {
        this.ejbLink = ejbLink;
    }
    
    public String getURI() {
        return uri;
    }
    
    public void setURI(String uri) {
        this.uri = uri;
    }
}
