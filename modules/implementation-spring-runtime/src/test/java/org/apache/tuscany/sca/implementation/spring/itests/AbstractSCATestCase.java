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

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractSCATestCase<T> extends TestCase {

    protected Node node;
    protected T service;

    @Override
    protected void setUp() throws Exception {
        String contribution = ContributionLocationHelper.getContributionLocation(getClass());
        node = NodeFactory.newInstance().createNode(getCompositeName(), new Contribution("test", contribution));
        node.start();
        service = (T)node.getService(getServiceClass(), "ClientComponent");
    }

    protected abstract Class getServiceClass();

    @Override
    protected void tearDown() throws Exception {
        node.stop();
        node.destroy();
    }

    protected String getCompositeName() {
        String className = this.getClass().getName();
        String compositeName = className.substring(0, className.length() - 8).replace('.', '/') + ".composite";
        System.out.println("Using composite: " + compositeName);
        return compositeName;
    }

}
