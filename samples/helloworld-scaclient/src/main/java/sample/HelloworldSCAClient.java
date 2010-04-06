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
package sample;

import java.net.URI;

import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

public class HelloworldSCAClient {

    public static void main(String[] args) throws NoSuchDomainException, NoSuchServiceException {

        if (args.length != 3) {
            System.out.println("Missing args");
            System.out.println("HelloworldSCAClient <domain URI> <name>");
            System.exit(1);
        }
        String domainURI = args[0];
        String name = args[1];
        
        SCAClientFactory factory = SCAClientFactory.newInstance(URI.create(domainURI));
        Helloworld service = factory.getService(Helloworld.class, "HelloworldComponent");
        
        System.out.println("Calling HelloworldComponent.sayHello(\"" + name + "\")");
        System.out.println(service.sayHello(name));
    }
    
}
