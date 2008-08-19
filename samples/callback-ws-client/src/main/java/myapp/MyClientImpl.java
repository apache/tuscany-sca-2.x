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
package myapp;

import myserver.MyService;
import myserver.MyServiceCallback;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Remote Web service client with callback interface
 */
@Service(MyClient.class)
@Scope("COMPOSITE")
public class MyClientImpl implements MyClient, MyServiceCallback {
    @Reference
    protected MyService myService;

    public void aClientMethod() {
        System.out.println("aClientMethod on thread " + Thread.currentThread());
        myService.someMethod(" -> someMethod ");  // calls the server
        System.out.println("aClientMethod return from someMethod on thread " + Thread.currentThread());
    }

    public void receiveResult(String result) {
        System.out.println("receiveResult on thread " + Thread.currentThread());
        System.out.println("Result: " + result);  // callback from the server
    }

    public static void main(String[] args) throws Exception {
        SCANode node = SCANodeFactory.newInstance().createSCANodeFromClassLoader("myapp.composite", MyClientImpl.class.getClassLoader());
        node.start();
        run(node);
        System.out.println("Closing the domain");
        node.stop();
    }

    public static void run(SCANode node) throws InterruptedException {
        MyClient myClient = ((SCAClient)node).getService(MyClient.class, "MyClientComponent");
        myClient.aClientMethod();
        Thread.sleep(5000);  // don't exit before callback arrives
    }
}
