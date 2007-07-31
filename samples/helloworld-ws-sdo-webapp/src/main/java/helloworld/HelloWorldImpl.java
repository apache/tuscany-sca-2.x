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
package helloworld;

import java.rmi.RemoteException;
import java.util.List;

import org.osoa.sca.annotations.Service;

@Service(HelloWorld.class)
public class HelloWorldImpl implements HelloWorld {

    public String getGreetings(Party party) throws RemoteException {
        System.out.println("Greeting party");
        StringBuffer greetings = new StringBuffer();
        greetings.append("Hello ");
        List<Person> people = party.getPeople();
        int i=0;
        for (Person person : people) {
            greetings.append(person.getFirstName());
            greetings.append(" ");
            greetings.append(person.getLastName());
            i++;
            if (i < people.size()) {
                greetings.append(", ");
            }
        }
        greetings.append("!");
        
        return greetings.toString();
    }

}
