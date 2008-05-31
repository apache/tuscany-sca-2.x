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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

public class GeneratedClassLoader extends SecureClassLoader {
    private String className;
    private byte[] content;
    private Class<?> cls;
    
    private static class GeneratedClass {
        private String className;
        private byte[] content;
        private Class<?> cls;
    }
    
    private Map<String, GeneratedClass> generatedClasses = new HashMap<String, GeneratedClass>();

    public GeneratedClassLoader(ClassLoader parentLoader, String className, byte[] content) {
        super(parentLoader);
        this.className = className;
        this.content = content;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (this.className.equals(className)) {
            return getGeneratedClass();
        }
        return super.findClass(className);
    }

    public synchronized Class<?> getGeneratedClass() {
        if (cls == null) {
            cls = defineClass(className, content, 0, content.length);
        }
        return cls;
    }
}
