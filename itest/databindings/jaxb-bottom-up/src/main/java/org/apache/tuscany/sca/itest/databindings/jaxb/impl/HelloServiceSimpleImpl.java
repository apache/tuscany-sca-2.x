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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.itest.databindings.jaxb.HelloServiceSimple;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of HelloServiceSimple.
 */
@Service(HelloServiceSimple.class)
public class HelloServiceSimpleImpl implements HelloServiceSimple {

    public String getGreetings(String name) {
        return "Hello "+name;
    }

    public String[] getGreetingsArray(String[] names) {
        for(int i = 0; i < names.length; ++i) {
            names[i] = "Hello "+names[i];
        }
        return names;
    }

    public List<String> getGreetingsList(List<String> names) {
        for(int i = 0; i < names.size(); ++i) {
            names.set(i, "Hello "+names.get(i));
        }
        return names;
    }

    public ArrayList<String> getGreetingsArrayList(ArrayList<String> names) {
        for(int i = 0; i < names.size(); ++i) {
            names.set(i, "Hello "+names.get(i));
        }
        return names;
    }

    public Map<String, String> getGreetingsMap(Map<String, String> namesMap) {
        for(Map.Entry<String, String> entry: namesMap.entrySet()) {
            entry.setValue("Hello "+entry.getKey());
        }
        return namesMap;
    }

    public HashMap<String, String> getGreetingsHashMap(HashMap<String, String> namesMap) {
        for(Map.Entry<String, String> entry: namesMap.entrySet()) {
            entry.setValue("Hello "+entry.getKey());
        }
        return namesMap;
    }
}
