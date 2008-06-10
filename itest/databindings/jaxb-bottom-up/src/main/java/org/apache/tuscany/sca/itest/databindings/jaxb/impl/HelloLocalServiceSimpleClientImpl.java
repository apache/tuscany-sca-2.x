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

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.itest.databindings.jaxb.HelloLocalServiceSimple;
import org.apache.tuscany.sca.itest.databindings.jaxb.HelloServiceSimpleClient;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of HelloServiceSimpleClient.
 * The client forwards the request to the service component and returns the response from the service component.
 */
@Service(HelloServiceSimpleClient.class)
public class HelloLocalServiceSimpleClientImpl implements HelloServiceSimpleClient {

    private HelloLocalServiceSimple service;

    @Reference(required=false)
    protected void setHelloLocalServiceSimple(HelloLocalServiceSimple service) {
        this.service = service;
    }

    public String getGreetingsForward(String name) {
        return service.getGreetings(name);
    }

    public String[] getGreetingsArrayForward(String[] names) {
        return service.getGreetingsArray(names);
    }

    public List<String> getGreetingsListForward(List<String> names) {
        return service.getGreetingsList(names);
    }

    public Map<String, String> getGreetingsMapForward(Map<String, String> namesMap) {
        return service.getGreetingsMap(namesMap);
    }

    public ArrayList<String> getGreetingsArrayListForward(ArrayList<String> names) {
        return service.getGreetingsArrayList(names);
    }

    public HashMap<String, String> getGreetingsHashMapForward(HashMap<String, String> namesMap) {
        return service.getGreetingsHashMap(namesMap);
    }

    public String getGreetingsVarArgsForward(String... names) {
        return service.getGreetingsVarArgs(names[0], names[1], names[2]);
    }
}
