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
package org.apache.tuscany.sca.binding.comet.runtime.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * Manager for Tuscany comet operations. This is a thread-safe singleton class.
 */
public class CometOperationManager {

    private static final ConcurrentMap<String, Operation> operations = new ConcurrentHashMap<String, Operation>();

    private CometOperationManager() {
    }

    public static void add(String url, Operation operation) {
        operations.put(url, operation);
    }

    public static Operation get(String url) {
        return operations.get(url);
    }

    public static void remove(String url) {
        operations.remove(url);
    }

    public static void clear() {
        operations.clear();
    }
}
