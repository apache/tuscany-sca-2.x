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
package voidtest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(TestClient.class)
@Scope("COMPOSITE")
public class TestClientImpl implements TestClient {

    private HelloWorldService helloService;

    @Reference
    public void setHelloService(HelloWorldService helloService) {
        this.helloService = helloService;
    }

    public void runTest() {
        assertEquals("Hello  Smith", helloService.getGreetings(" Smith"));
        helloService.giveGreetings("Jones");
        helloService.giveGreetingsOneWay("Brown");
        assertEquals("Hello Stranger", helloService.takeGreetings());
        helloService.noGreetings();
        
        assertEquals("wang",helloService.getBean().getFirst());
        assertEquals("feng",helloService.getBean().getLast());
        
        assertNull(helloService.getNullString());
        
        assertNull(helloService.getNullBean());
    }
}
