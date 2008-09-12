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

package org.apache.tuscany.sca.implementation.openjpa.impl;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.apache.openjpa.persistence.PersistenceUnitInfoImpl;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.openjpa.JPAImplementation;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

public class JPAImplementationImpl implements JPAImplementation {
    private Service jpaService;
    private PersistenceUnitInfoImpl puii;
    private Properties dsmeta;

    public void setPersistenceUnitInfoImpl(PersistenceUnitInfoImpl puii) {
        this.puii = puii;
    }

    public PersistenceUnitInfoImpl getPersistenceUnitInfoImpl() {
        return this.puii;
    }

    public void setDataSourceMeta(Properties p) {
        dsmeta = p;
    }

    public Properties getDataSourceMeta() {
        return dsmeta;
    }

    JPAImplementationImpl(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {

        jpaService = assemblyFactory.createService();
        jpaService.setName("EntityManager");
        JavaInterface javaInterface;
        try {
            javaInterface = javaFactory.createJavaInterface(EntityManager.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        jpaService.setInterfaceContract(interfaceContract);
    }

    public boolean isUnresolved() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // TODO Auto-generated method stub

    }

    public ConstrainingType getConstrainingType() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Property> getProperties() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    public List<Reference> getReferences() {
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        return Collections.singletonList(jpaService);
    }

    public String getURI() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // TODO Auto-generated method stub

    }

    public void setURI(String uri) {
        // TODO Auto-generated method stub

    }

}
