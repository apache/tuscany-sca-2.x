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

package org.apache.tuscany.sca.binding.atom;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractProviderConsumerTestCase {
    protected static Node scaProviderNode;
    protected static Node scaConsumerNode;

    protected static void initTestEnvironment(Class<?> testClazz) throws Exception {
        String contribution = ContributionLocationHelper.getContributionLocation(testClazz);

        scaProviderNode = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/atom/Provider.composite", new Contribution("provider", contribution));
        scaProviderNode.start();

        scaConsumerNode = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/atom/Consumer.composite", new Contribution("consumer", contribution));
        scaConsumerNode.start();
    }

    protected static void destroyTestEnvironment() throws Exception {
        if (scaConsumerNode != null) {
            scaConsumerNode.stop();
            scaConsumerNode.destroy();
        }
        if (scaProviderNode != null) {
            scaProviderNode.stop();
            scaProviderNode.destroy();
        }
    }
}
