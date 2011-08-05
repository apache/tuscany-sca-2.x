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

package recursive;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import recursive.Composer;

/**
 * Test case for hweb service client 
 */
public class ComposerClientTestCase {

    private Node node;
    private Composer composer;

    @Before
    public void startClient() throws Exception {
        try {
            TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();
            node = tuscanyRuntime.createNode("myDomain");
            
            node.installContribution("contrib1", "src/main/resources/recursive", null, null);
            node.startComposite("contrib1", "Outer.composite");
            node.startComposite("contrib1", "Client.composite");

            composer = node.getService(Composer.class, "ClientComponent/Composer");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testWSClient() throws Exception {
        String msg = composer.Compose("ABC");
        Assert.assertEquals("Composed: ABC", msg);
    }
    
    @After
    public void stopClient() throws Exception {
        node.stop();
    }

}
