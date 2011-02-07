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

package org.apache.tuscany.sca.itest.allowspassbyreference;

import java.util.HashMap;
import java.util.Map;

import org.oasisopen.sca.annotation.AllowsPassByReference;
import org.oasisopen.sca.annotation.Scope;

/**
 * 
 */
@Scope("COMPOSITE")
public class ServiceImplA implements AService {
    private volatile int count = 0;
    private Map<Integer, MutableObject> objects = new HashMap<Integer, MutableObject>();

    @Override
    public MutableObject create(MutableObject req) {
        req.setId(count++);
        objects.put(req.getId(), req);
        return req;
    }

    @AllowsPassByReference
    public MutableObject read(MutableObject req) {
        // Change the state of the request so that the client side can verify if PBR is used
        req.setState("READ");
        return objects.get(req.getId());
    }

    @Override
    public MutableObject update(MutableObject req) {
        objects.put(req.getId(), req);
        return req;
    }

    @Override
    public MutableObject delete(MutableObject req) {
        return objects.remove(req.getId());
    }

}
