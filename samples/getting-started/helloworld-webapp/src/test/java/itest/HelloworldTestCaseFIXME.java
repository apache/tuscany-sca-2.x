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

package itest;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 */
public class HelloworldTestCaseFIXME {

    @Test
    public void testHelloworld() throws NoSuchDomainException, NoSuchServiceException {
// TODO: need to fix the config URI so it works properly
//        SCAClientFactory factory = SCAClientFactory.newInstance(URI.create("uri:default?remote=127.0.0.1:54321"));
//        SCAClientFactory factory = SCAClientFactory.newInstance(URI.create("tuscany:default?remotes=192.168.1.64"));
//        Helloworld helloworld = factory.getService(Helloworld.class, "HelloworldComponent");
//        assertEquals("Hello World", helloworld.sayHello("World"));
    }

}
