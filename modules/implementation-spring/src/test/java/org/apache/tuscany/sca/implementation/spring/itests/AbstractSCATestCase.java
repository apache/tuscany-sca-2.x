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

package org.apache.tuscany.sca.implementation.spring.itests;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public abstract class AbstractSCATestCase<T> extends TestCase {

    protected SCADomain domain;
    protected T service;

    @Override
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance(getCompositeName());
        service = (T)domain.getService(getServiceClass(), "ClientComponent");
    }

    abstract protected Class getServiceClass();

    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

    protected String getCompositeName() {
        String className = this.getClass().getName();
        String compositeName = className.substring(0, className.length() - 8).replace('.', '/') + ".composite";
        System.out.println("Using composite: " + compositeName);
        return compositeName;
    }

}
