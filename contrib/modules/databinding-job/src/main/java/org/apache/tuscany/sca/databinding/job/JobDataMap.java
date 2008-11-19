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
package org.apache.tuscany.sca.databinding.job;

import java.util.HashMap;

public class JobDataMap implements java.io.Serializable {
    private static final long serialVersionUID = -2602843967597362950L;
    private HashMap<String, Object> data = new HashMap<String, Object>();

    public JobDataMap() {
        super();
    }

    public <T> void addJobData(String name, T t) {
        data.put(name, t);
    }

    public Object getJobDataObject(String name) {
        if (data.containsKey(name)) {
            return data.get(name);
        }
        return null;
    }

    public Class<?> getJobDataClass(String name) {
        if (data.containsKey(name)) {
            return data.get(name).getClass();
        }
        return null;
    }

    public Class<?>[] getJobDataClasses() {
        int siz = data.keySet().size();
        int i = 0;
        Class<?>[] claz = new Class<?>[siz];
        for (Object e : data.values()) {
            claz[i] = e.getClass();
            ++i;
        }
        return claz;
    }

    public Object[] getJobDataObjects() {
        int siz = data.keySet().size();
        Object[] objs = new Object[siz];
        int i = 0;
        for (Object e : data.values()) {
            objs[i] = e.getClass();
            ++i;
        }
        return objs;
    }
}
