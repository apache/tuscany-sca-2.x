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
package myserver;

import java.io.IOException;
import java.net.Socket;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the callback server is available
 */
public class CallbackServerTestCase {

    private SCANode node;

    @Before
    public void startServer() throws Exception {
        node = SCANodeFactory.newInstance().createSCANodeFromClassLoader("callbackws.composite", null);
        node.start();
    }

    @Test
    public void testPing() throws IOException {
        new Socket("127.0.0.1", 8086);
    }

    @After
    public void stopServer() throws Exception {
        node.stop();
    }
}
