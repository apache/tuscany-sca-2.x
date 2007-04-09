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
package org.apache.tuscany.sca.runtime.standalone.smoketest;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
public class Launched {
    private HelloService hello;

    @Reference
    public void setHello(HelloService hello) {
        this.hello = hello;
    }

    public int main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("No Args");
            return 0;
        }
        String command = args[0];
        if ("testReference".equals(command)) {
            if ("Hello World".equals(hello.getGreeting())) {
                return 0;
            } else {
                return 1;
            }
        }
        return 1;
    }
}
