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


public class ServiceImpl implements MyService {

    public static Object lock = new Object();
    public static String name;
    public static String n2;
    
    public void sayHello(String name, String n2) {
        System.out.println("SelectorServiceImpl1 " + name + n2);
        ServiceImpl.name = name;
        ServiceImpl.n2 = n2;
        synchronized (ServiceImpl.lock) {
            ServiceImpl.lock.notify();
        }
    }

}
