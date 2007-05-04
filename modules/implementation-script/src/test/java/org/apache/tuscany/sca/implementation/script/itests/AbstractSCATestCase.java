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

package org.apache.tuscany.sca.implementation.script.itests;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntimeActivator;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

public abstract class AbstractSCATestCase<T> extends TestCase {

    protected T service;

    protected void setUp() throws Exception {
//        SCARuntimeActivator.start(getCompositeName());
//        ComponentContext context = SCARuntimeActivator.getComponentContext("ClientComponent");
//        ServiceReference<T> serviceReference = context.createSelfReference(getServiceClass());
//        service = serviceReference.getService();
    }
    
    abstract protected Class getServiceClass();

    protected void tearDown() throws Exception {
//        SCARuntimeActivator.stop();
    }

    protected String getCompositeName() {
        String className = this.getClass().getName();
        return className.substring(0, className.length() - 8).replace('.', '/') + ".composite";
    }

}
