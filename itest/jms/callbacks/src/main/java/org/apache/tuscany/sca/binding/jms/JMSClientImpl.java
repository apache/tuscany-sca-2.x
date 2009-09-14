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
package org.apache.tuscany.sca.binding.jms;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * Remote Web service client with callback interface
 */
@Service(JMSClient.class)
public class JMSClientImpl implements JMSClient, JMSServiceCallback {

    @Reference protected JMSService myService;

    public static String result;
    public static Object lock = new Object();
	
    public void aClientMethod() {
        System.out.println("aClientMethod " + this + " on thread " + Thread.currentThread());
        myService.someMethod(" -> someMethod ");  // calls the server
        System.out.println("aClientMethod return from someMethod on thread " + Thread.currentThread());
    }

    public void receiveResult(String result) {
        System.out.println("receiveResult " + this + " '" + result + "' on thread " + Thread.currentThread());
        JMSClientImpl.result = result;
        
        // wakeup the waiting testcase
        synchronized (lock) {
            lock.notifyAll();
	}
    }
}
