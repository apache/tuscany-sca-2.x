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

package org.apache.tuscany.sca.itest.interfaces;

import java.io.File;

import org.apache.tuscany.sca.interfacedef.InvalidAnnotationException;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.Test;

public class InvalidRemoteAttributeTestCase {
    private static final String PKG = "org/apache/tuscany/sca/itest/interfaces/invalid";
    private static String ROOT = new File("target/classes/" + PKG).toURI().toString();

    @Test
    public void testInvalidRemoteAttribute() throws Exception {
        Node node = null;

        try {
            String location = ROOT;
            node = NodeFactory.newInstance().createNode("InvalidRemoteAttribute.composite", new Contribution("c1", location));
            node.start();
        } catch (Exception e) {
            if (! e.getCause().getClass().equals(InvalidAnnotationException.class)) {
                throw e;
            }
        } finally {
            if (node != null) {
                node.stop();
            }
        }
    }
}
