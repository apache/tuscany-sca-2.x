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
package org.apache.tuscany.sca.implementation.jaxrs.impl;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.implementation.jaxrs.JAXRSImplementation;

/**
 * The model representing an Web implementation in an SCA assembly model.
 */
public class JAXRSImplementationImpl extends ImplementationImpl implements JAXRSImplementation {

    private String application;
    private Class<?> applicationClass;

    /**
     * Constructs a new JAXRS implementation.
     */
    public JAXRSImplementationImpl() {
        super(TYPE);
    }

    public List<Service> getServices() {
        // The Web implementation does not offer services
        return Collections.emptyList();
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((application == null) ? 0 : application.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof JAXRSImplementationImpl)) {
            return false;
        }
        JAXRSImplementationImpl other = (JAXRSImplementationImpl)obj;
        if (application == null) {
            if (other.application != null) {
                return false;
            }
        } else if (!application.equals(other.application)) {
            return false;
        }
        return true;
    }

    public Class<?> getApplicationClass() {
        return applicationClass;
    }

    public void setApplicationClass(Class<?> applicationClass) {
        this.applicationClass = applicationClass;
    }

}
