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

package org.apache.tuscany.sca.extensibility.test;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;

/**
 * 
 */
public class Test2Impl implements TestInterface, LifeCycleListener {
    private ExtensionPointRegistry registry;
    public int state = 0;

    public Test2Impl(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public Test2Impl() {
    }

    /**
     * @see org.apache.tuscany.sca.extensibility.test.TestInterface#test(java.lang.String)
     */
    public String test(String str) {
        System.out.println("Test 2: " + str);
        return "Test 2: " + str;
    }

    public ExtensionPointRegistry getRegistry() {
        return registry;
    }

    public void start() {
        state = 1;
    }

    public void stop() {
        state = -1;
    }

    public int getState() {
        return state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((registry == null) ? 0 : registry.hashCode());
        result = prime * result + state;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Test2Impl other = (Test2Impl)obj;
        if (registry == null) {
            if (other.registry != null)
                return false;
        } else if (!registry.equals(other.registry))
            return false;
        if (state != other.state)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Test2Impl [state=" + state + "]";
    }

    public QName getArtifactType() {
        // TODO Auto-generated method stub
        return new QName("http://sample", "Test2");
    }

}
